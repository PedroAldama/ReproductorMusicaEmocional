package com.reproductor.music.services.feelings;

import com.reproductor.music.dto.response.DTOSong;
import com.reproductor.music.dto.response.DTOSongFeelings;
import com.reproductor.music.dto.response.DTOVectorSong;
import com.reproductor.music.entities.Feelings;
import com.reproductor.music.entities.Song;
import com.reproductor.music.entities.SongFeelings;
import com.reproductor.music.entities.Users;
import com.reproductor.music.exceptions.SongExceptions;
import com.reproductor.music.repositories.FeelingsRepository;
import com.reproductor.music.dto.request.FeelingsRequest;
import com.reproductor.music.repositories.SongFeelingRepository;
import com.reproductor.music.repositories.UserRepository;
import com.reproductor.music.services.history.HistoryService;
import com.reproductor.music.services.redis.RedisServiceImp;
import com.reproductor.music.services.song.SongService;
import com.reproductor.music.utils.Convert;
import com.reproductor.music.utils.UserUtils;
import com.reproductor.music.utils.VectorUtils;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class FeelingsServiceImpl implements FeelingsService {

    private final FeelingsRepository feelingsRepository;
    private final SongFeelingRepository songFeelingRepository;
    private final RedisServiceImp redisService;
    private final VectorUtils vectorUtils;
    private final UserRepository userRepository;
    private final SongService songService;
    private final HistoryService historyService;
    private final EntityManager entityManager;

    private  String  userName;
    @Override
    @Transactional
    public void addFeelings(List<FeelingsRequest> feelings) {
        List<Feelings> feelingList = getAllFeelings();
        List<SongFeelings> songFeelings = new ArrayList<>();
        Users userDb = userRepository.findByUsername(getCurrentUserName()).orElseThrow();
        for(FeelingsRequest feeling : feelings) {
            Song song = entityManager.getReference(Song.class,getSongIdByName(feeling.getSongName()));
            for(int i =0; i < feeling.getFeelings().size(); i++) {
            songFeelings.add(SongFeelings.builder()
                    .feeling(feelingList.get(i))
                            .song(song)
                            .user(userDb)
                            .value(feeling.getFeelings().get(i))
                    .build()
            );
        }
        }
             songFeelingRepository.saveAll(songFeelings);
    }

    @Override
    @Transactional
    public void updateFeelings(FeelingsRequest feelings) {
        addFeelings(List.of(feelings));
    }

    @Transactional
    @Override
    public List<DTOVectorSong> searchSongBySimilarFeelings(String song) {
        this.userName = getCurrentUserName();
        String songName = songService.getOnlyName(song);

        if(songName.isEmpty()) throw new SongExceptions.SongNotFoundException(song);

        List<Double> vector = vectorUtils.normalize(songFeelingRepository.findUserSongValues(this.userName,songName));
        if(vector.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> songs = getSongByUser(this.userName);

        return getFeelingsForSongListSimilarity(this.userName,songs,vector).stream()
                .filter(s -> !s.getTitle().equals(songName))
                .distinct()
                .sorted(Comparator.comparingDouble(DTOVectorSong::getSimilarity)
                        .reversed())
                .limit(2)
                .toList();
    }
    @Override
    public DTOSongFeelings searchByName(String song) {
        userName = (userName == null) ? getCurrentUserName() : userName;
        String songName = songService.getOnlyName(song);

        if(songName.isEmpty()) throw new SongExceptions.SongNotFoundException(song);

        List<SongFeelings> songFeelings = songFeelingRepository.findByUser_UsernameAndSong_Name(songName,this.userName);
        return DTOSongFeelings.builder().name(songName)
                .feelings(vectorUtils.getFeelingsMap(songFeelings)).build();
    }
    @Override
    public List<Double> getCurrentFeelingsByUser() {
        userName = (userName == null) ? getCurrentUserName() : userName;
        String feelings = redisService.getCurrentFeeling(this.userName);

        if(feelings != null && !feelings.isEmpty()){
            return Arrays.stream(feelings.split(",")).map(Double::valueOf).toList();
        }

        return createCurrentFeeling();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DTOVectorSong> findMostSimilarSongs() {
        userName = (userName == null) ? getCurrentUserName() : userName;
        List<String> songsOmitted = redisService.getFullList(this.userName);

        //Get all song for user, first check in redis and the count in sql
        List<String> songsName = getSongByUser(this.userName);

        List<Double> userVector = getCurrentFeelingsByUser();

        List<DTOVectorSong> allSongs = getFeelingsForSongListSimilarity(this.userName,songsName,userVector).stream()
                .filter(Objects::nonNull)
                .filter(dto -> !songsOmitted.contains(dto.getTitle()))
                .toList();

        return allSongs.stream().sorted(Comparator.comparingDouble(DTOVectorSong::getSimilarity).reversed())
                .limit(5)
                .distinct()
                .toList();
    }
    @Override
    public List<Double> createCurrentFeeling(){
        userName = (userName == null) ? getCurrentUserName() : userName;
        //Get the last 3 songs played by user storage in redis
        List<String> songsName = redisService.getListOf(this.userName);
        int i = 1;
        while(songsName.isEmpty() && i < 4){
            LocalDate localDate = new Date().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .minusDays(i);
            songsName = historyService.getSongsHistory(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                    .stream().distinct().toList();
            i++;
        }
        //if songsName.isEmpty() return not found any song in the history
        //Get the Songs from song service, I need to change this using redis instead to improve performance
        List<List<Double>> data = songsName.stream().map( s-> getDoubles(this.userName,s)).toList();
        List<Double> res = vectorUtils.normalize(vectorUtils.average(data));
        redisService.setCurrentFeeling(this.userName,res);
        return res;
    }
    @Override
    @Transactional(readOnly = true)
    public List<DTOSong> searchByUsername() {
        return Convert.convertSongList(songFeelingRepository.getSongsByUser(getCurrentUserName()));
    }

    @Override
    @Transactional
    public DTOSong recommendationWebSocket() {
        DTOVectorSong song = findMostSimilarSongs().stream().findFirst().get();
        updateUserFeelings(song);
        return DTOSong.builder().name(song.getTitle()).src(songService.getSrc(song.getTitle())).build();
    }

    @Override
    public void setUser(String user) {
        this.userName = user;
    }

    public List<DTOVectorSong> getFeelingsForSongListSimilarity(String username, List<String> songNames
            , List<Double> userVector) {

        List<DTOVectorSong> result = new ArrayList<>();

        for (String songName : songNames) {
            List<Double> songVector = redisService.getUserSongVector(username, songName);

            if (songVector.isEmpty()) {
                songVector = vectorUtils.normalize(getDoubles(username, songName));
                String raw = songVector.stream().map(String::valueOf).collect(Collectors.joining(","));
                String key = String.format("feelings:%s:%s", username, songName);
                redisService.saveUserSongVectorToRedis(key, raw);
                redisService.setUserSongsVectorCurrentlyStorage(username,songName);
            }
            if (!songVector.isEmpty()) {
                double similarity = vectorUtils.cosineSimilarity(userVector, songVector);

                result.add(DTOVectorSong.builder()
                        .title(songName)
                        .feelings(songVector)
                        .similarity(similarity)
                        .build());
            }
        }

        return result;
    }


    public List<Double> getDoubles(String username, String songName) {
        return songFeelingRepository.findUserSongValues(username, songName);
    }

    public List<String> getSongByUser(String user){
        List<String> songsName = redisService.getUserSongsVectorCurrentlyStorage(user).stream().map(String::valueOf).toList();
        //verify if the songs on
        if(songsName.size() < songFeelingRepository.countSongByUser(user)){
            songsName = songFeelingRepository.getSongsNameByUser(user);
            songsName.forEach(s -> redisService.setUserSongsVectorCurrentlyStorage(user,s));
        }
        return  songsName;
    }

    public void updateUserFeelings(DTOVectorSong song){
        //When a song is recommended, update the feeling vector to send new songs
        List<Double> newFeelings = vectorUtils.average(
                List.of(getCurrentFeelingsByUser(),song.getFeelings()));
        redisService.addSongToList(this.userName, song.getTitle());
        redisService.setCurrentFeeling(this.userName,newFeelings);
    }

    public List<Feelings> getAllFeelings(){
        //Getting from Redis or Mysql
        return feelingsRepository.findAll();
    }
    private int getSongIdByName(String songName) {
        return songService.findIdBySongName(songName);
    }

    public String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

}

