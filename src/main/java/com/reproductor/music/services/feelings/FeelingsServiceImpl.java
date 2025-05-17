package com.reproductor.music.services.feelings;

import com.reproductor.music.dto.DTOSong;
import com.reproductor.music.dto.DTOSongFeelings;
import com.reproductor.music.dto.request.DTOVectorSong;
import com.reproductor.music.entities.Song;
import com.reproductor.music.entities.SongFeelings;
import com.reproductor.music.entities.Users;
import com.reproductor.music.exceptions.SongException;
import com.reproductor.music.repositories.FeelingsRepository;
import com.reproductor.music.dto.request.FeelingsRequest;
import com.reproductor.music.repositories.SongFeelingRepository;
import com.reproductor.music.repositories.UserRepository;
import com.reproductor.music.services.history.HistoryService;
import com.reproductor.music.services.redis.RedisServiceImp;
import com.reproductor.music.services.song.SongService;
import com.reproductor.music.utils.Convert;
import com.reproductor.music.utils.VectorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
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

    @Override
    public void addFeelings(FeelingsRequest feelings, String user) {
        Song song = songService.getSongByName(feelings.getSongName());
        Users userDb = userRepository.findByUsername(user).orElseThrow();

        int i = 1;
        for(Double n: feelings.getFeelings()) {
             SongFeelings newSongFeeling = new SongFeelings();
             newSongFeeling.setSong(song);
             newSongFeeling.setFeeling(feelingsRepository.findById((long) i++).orElseThrow());
             newSongFeeling.setValue(n);
             newSongFeeling.setUser(userDb);
             songFeelingRepository.save(newSongFeeling);
        }
    }

    @Override
    public void updateFeelings(FeelingsRequest feelings,String user) {
        addFeelings(feelings,user);
    }

    @Override
    public List<DTOVectorSong> searchSongBySimilarFeelings(String song, String user) {
        String songName = songService.getSongByName(song).getName();

        if(songName.isEmpty()) throw new SongException.SongNotFoundException(song);

        List<Double> vector = vectorUtils.normalize(songFeelingRepository.findUserSongValues(user,songName));
        if(vector.isEmpty()) {
            return null;
        }

        List<String> songs = getSongByUser(user);

        return getFeelingsForSongListSimilarity(user,songs,vector).stream()
                .filter(s -> !s.getTitle().equals(songName))
                .distinct()
                .sorted(Comparator.comparingDouble(DTOVectorSong::getSimilarity)
                        .reversed())
                .limit(2)
                .toList();
    }
    @Override
    public DTOSongFeelings searchByName(String song, String user) {
        String songName = songService.getSongByName(song).getName();

        if(songName.isEmpty()) throw new SongException.SongNotFoundException(song);

        List<SongFeelings> songFeelings = songFeelingRepository.findByUser_UsernameAndSong_Name(songName,user);
        return DTOSongFeelings.builder().name(songName)
                .feelings(vectorUtils.getFeelingsMap(songFeelings)).build();
    }
    @Override
    public List<Double> getCurrentFeelingsByUser(String user) {
        String feelings = redisService.getCurrentFeeling(user);

        if(feelings != null && !feelings.isEmpty()){
            return Arrays.stream(feelings.split(",")).map(Double::valueOf).toList();
        }

        return createCurrentFeeling(user);
    }

    @Override
    public List<DTOVectorSong> findMostSimilarSongs(String user) {
        List<String> songsOmitted = redisService.getListOf(user);

        //Get all song for user, first check in redis and the count in sql
        List<String> songsName = getSongByUser(user);

        List<Double> userVector = getCurrentFeelingsByUser(user);

        List<DTOVectorSong> allSongs = getFeelingsForSongListSimilarity(user,songsName,userVector).stream()
                .filter(Objects::nonNull)
                .filter(dto -> !songsOmitted.contains(dto.getTitle()))
                .toList();

        return allSongs.stream().sorted(Comparator.comparingDouble(DTOVectorSong::getSimilarity).reversed())
                .limit(3)
                .distinct()
                .toList();
    }
    @Override
    public List<Double> createCurrentFeeling(String user){
        //Get the last 3 songs played by user storage in redis
        List<String> songsName = redisService.getListOf(user);
        int i = 1;
        while(songsName.isEmpty() && i < 4){
            LocalDate localDate = new Date().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .minusDays(i);
            songsName = historyService.getSongsHistory(user,Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                    .stream().distinct().toList();
            i++;
        }
        //if songsName.isEmpty() return not found any song in the history
        //Get the Songs from song service, I need to change this using redis instead to improve performance
        List<List<Double>> data = songsName.stream().map( s-> getDoubles(user,s)).toList();
        List<Double> res = vectorUtils.average(data);
        redisService.setCurrentFeeling(user,res);
        return res;
    }
    @Override
    public List<DTOSong> searchByUsername(String name) {
        return Convert.convertSongList(songFeelingRepository.getSongsByUser(name));
    }

    private List<DTOVectorSong> getFeelingsForSongListSimilarity(String username, List<String> songNames
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

    private List<Double> getDoubles(String username, String songName) {
        return songFeelingRepository.findUserSongValues(username, songName);
    }

    private List<String> getSongByUser(String user){
        List<String> songsName = redisService.getUserSongsVectorCurrentlyStorage(user);
        //verify if the songs on
        if(songsName.size() < songFeelingRepository.countSongByUser(user)){
            songsName = songFeelingRepository.getSongsNameByUser(user);
            songsName.forEach(s -> redisService.setUserSongsVectorCurrentlyStorage(user,s));
        }
        return  songsName;
    }
}

