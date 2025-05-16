package com.reproductor.music.services;

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
import com.reproductor.music.utils.Convert;
import com.reproductor.music.utils.VectorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class FeelingsServiceImpl implements FeelingsService {

    private final SongService songService;
    private final FeelingsRepository repository;
    private final SongFeelingRepository songFeelingRepository;
    private final UserRepository userRepository;
    private final RedisServiceImp redisService;
    private final VectorUtils vectorUtils;

    @Override
    public void addFeelings(FeelingsRequest feelings, String user) {
        Song song = songService.getSongByName(feelings.getSongName());
        Users userDb = userRepository.findByUsername(user).orElseThrow();
        if (song == null) {
            throw new SongException.SongNotFoundException(feelings.getSongName());
        }
        int i = 1;
        for(Double n: feelings.getFeelings()) {
             SongFeelings newSongFeeling = new SongFeelings();
             newSongFeeling.setSong(song);
             newSongFeeling.setFeeling(repository.findById((long) i++).orElseThrow());
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
    public DTOVectorSong searchSongBySimilarFeelings(String name) {
        return findMostSimilarSongs(name).stream().findFirst().orElseThrow();
    }
    @Override
    public DTOSongFeelings searchByName(String song, String user) {
        List<SongFeelings> songFeelings = songFeelingRepository.findByUser_UsernameAndSong_Name(song,user);
        return DTOSongFeelings.builder().name(song)
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
        List<String> songsName = songFeelingRepository.findByUser_Username(user).stream().map(Song::getName).toList();

        List<DTOVectorSong> allSongs = getFeelingsForSongListSimilarity(user,songsName).stream()
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
        //Get the Songs from song service, I need to change this using redis instead to improve performance
        List<List<Double>> data = songsName.stream().map(s -> getDoubles(user,s)).toList();
        List<Double> res = vectorUtils.average(data);
        redisService.setCurrentFeeling(user,res);
        return res;
    }
    @Override
    public List<DTOSong> searchByUsername(String name) {
        return Convert.convertSongList(songFeelingRepository.findByUser_Username(name));
    }


    private List<DTOVectorSong> getFeelingsForSongListSimilarity(String username, List<String> songNames) {

        List<Double> userVector = getCurrentFeelingsByUser(username);
        List<DTOVectorSong> result = new ArrayList<>();

        for (String songName : songNames) {
            List<Double> songVector = redisService.getUserSongVector(username, songName);

            if (songVector.isEmpty()) {
                songVector = getDoubles(username, songName);
                String raw = songVector.stream().map(String::valueOf).collect(Collectors.joining(","));
                String key = String.format("feelings:%s:%s", username, songName);
                redisService.saveUserSongVectorToRedis(key, raw);
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
        List<Double> songVector;
        songVector = songFeelingRepository.findByUser_UsernameAndSong_Name(songName, username)
                .stream()
                .map(SongFeelings::getValue)
                .toList();
        songVector = vectorUtils.normalize(songVector);
        return songVector;
    }


}

