package com.reproductor.music.services;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.reproductor.music.dto.Convert.getAllSongsWithFeelings;

@RequiredArgsConstructor
@Service
public class FeelingsServiceImpl implements FeelingsService {

    private static final String FEELING_KEY = "feeling";
    private final SongService songService;
    private final FeelingsRepository repository;
    private final SongFeelingRepository songFeelingRepository;
    private final UserRepository userRepository;
    private final RedisServiceImp redisService;

    @Override
    public void addFeelings(FeelingsRequest feelings) {
        Song song = songService.getSongByName(feelings.getSongName());
        if (song == null) {
            throw new SongException.SongNotFoundException(feelings.getSongName());
        }
        int i = 1;
        for(int n: feelings.getFeelings()) {
             SongFeelings newSongFeeling = new SongFeelings();
             newSongFeeling.setSong(song);
             newSongFeeling.setFeeling(repository.findById((long) i++).orElseThrow());
             newSongFeeling.setValue(n);
             songFeelingRepository.save(newSongFeeling);
        }
    }

    @Override
    public void updateFeelings(FeelingsRequest feelings) {

    }

    @Override
    public DTOVectorSong searchSongBySimilarFeelings(String name) {
       List<DTOVectorSong> songs = getAllSongsWithFeelings(songService.findAllWithFeeling());
        return findMostSimilarSong(getCurrentFeelingsByUser(name),songs, name);
    }

    @Override
    public List<DTOVectorSong> getFeelingsByUser(String user, List<String> songs) {
        Users userDb = userRepository.findByUsername(user).orElseThrow();
        return songs.stream()
                .map(song ->{
                    Song songDb = songService.getSongByName(song);
                    List<Double> feelings = songFeelingRepository.findBySongAndUser(songDb,userDb)
                            .stream()
                            .sorted(Comparator.comparing(sf->sf.getFeeling().getId()))
                            .map(SongFeelings::getValue)
                            .map(v->(double)v)
                            .toList();
                    return DTOVectorSong.builder()
                            .title(song)
                            .feelings(feelings)
                            .build();
                }).toList();
    }

    @Override
    public DTOSongFeelings searchByName(String name) {
        Song searchSong = songService.getSongByName(name);
        if(searchSong == null) {
            throw new SongException.SongNotFoundException(name);
        }
        List<SongFeelings> songFeelings = songFeelingRepository.findBySong(searchSong);

        return DTOSongFeelings.builder().name(searchSong.getName())
            .feelings(getFeelingsMap(songFeelings)).build();
    }

    @Override
    public List<Double> getCurrentFeelingsByUser(String user) {
        String feelings = redisService.getCurrentFeeling(user);

        if(feelings != null && !feelings.isEmpty()){
            return Arrays.stream(feelings.split(",")).map(Double::valueOf).toList();
        }

        return createCurrentFeeling(user);
    }

    public List<Double> createCurrentFeeling(String user){
        List<String> songs = redisService.getListOf(user,2);
        List<double[]> data = getFeelingsByUser(user,songs)
                .stream()
                .map(DTOVectorSong::getFeelings)
                .map(list-> list.stream().mapToDouble(i->i).toArray())
                .map(FeelingsServiceImpl::normalize)
                .toList();
        List<Double> res = average(data);
        redisService.setCurrentFeeling(user,res);
        return res;
    }

    private double cosineSimilarity(List<Double> v1, List<Double> v2) {
        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for(int i= 0; i < v1.size(); i++){
            dot+=v1.get(i)*v2.get(i);
            normA+= Math.pow(v2.get(i),2);
            normB+= Math.pow(v1.get(i),2);
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private DTOVectorSong findMostSimilarSong(List<Double> userVector, List<DTOVectorSong> allSongs, String name) {
        return allSongs.stream()
                .filter(song -> !song.getFeelings().isEmpty() && !song.getTitle().equals(name))
                .max(Comparator.comparingDouble(song ->
                        cosineSimilarity(userVector, song.getFeelings())))
                .orElse(null);
    }

    public static double[] normalize(double[] vector) {
        return Arrays.stream(vector)
                .map(val -> val / 10.0)
                .toArray();
    }
    public static List<Double> average(List<double[]> vector) {
        int size = vector.getFirst().length;
        double[] avg = new double[size];
        List<Double> data = new ArrayList<>();
        for(double[] vec: vector){
            for(int i = 0; i < size; i++){
                avg[i] += vec[i];
            }
        }
        for(int i = 0; i < size; i++){
            data.add(avg[i] / size);
        }
        return data;
    }
    private Map<String,Double> getFeelingsMap(List<SongFeelings> songFeelings){
        Map<String,Double> data = new HashMap<>();
        String[] feeling = {"Alegria", "Tristeza", "Calma", "Euforia", "Melancolia", "Amor", "Ira",
                "Ansiedad", "Misterio", "Empoderamiento"};
        for (int i = 0; i < songFeelings.size(); i++) {
            data.put(feeling[i], songFeelings.get(i).getValue());
        }
        return data;
    }
}

 