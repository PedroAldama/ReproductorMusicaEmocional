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
        /*Users user = getUser(name);
        List<Song> songsByUser = songFeelingRepository.findByUser(user).stream().map(SongFeelings::getSong).toList();
        List<DTOVectorSong> songs = getAllSongsWithFeelings(songsByUser);
        return findMostSimilarSong(getCurrentFeelingsByUser(name),songs, name);*/
        return null;
    }

    @Override
    public List<DTOVectorSong> getFeelingsByUser(String user, List<Song> songs) {
        Users userDb = getUser(user);
        return songs.stream()
                .map(song ->{
                    List<Double> feelings = songFeelingRepository.findBySongAndUser(song,userDb)
                            .stream()
                            .sorted(Comparator.comparing(sf->sf.getFeeling().getId()))
                            .map(SongFeelings::getValue)
                            .map(v->(double)v)
                            .toList();
                    return DTOVectorSong.builder()
                            .title(song.getName())
                            .feelings(feelings)
                            .build();
                }).toList();
    }

    @Override
    public DTOSongFeelings searchByName(String name, String user) {
        Song searchSong = songService.getSongByName(name);
        Users userDb = getUser(user);
        if(searchSong == null) {
            throw new SongException.SongNotFoundException(name);
        }
        List<SongFeelings> songFeelings = songFeelingRepository.findBySongAndUser(searchSong,userDb);

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
    @Override
    public List<DTOVectorSong> findMostSimilarSongs(String user) {

        List<Double> userVector = getCurrentFeelingsByUser(user);

        List<String> songsOmitted = redisService.getListOf(user,2);

        List<Song> songsName = songFeelingRepository.findByUser(getUser(user))
                .stream().map(SongFeelings::getSong)
                .toList();

        List<DTOVectorSong> allSongs = getFeelingsByUser(user,songsName);

        // Comparator with reversed cosine similarity
        Comparator<DTOVectorSong> similarityComparator = Comparator.comparingDouble(song ->
                cosineSimilarity(userVector, normalizeList(song.getFeelings()))
        );
        return allSongs.stream()
                .filter(song-> !song.getFeelings().isEmpty() && !songsOmitted.contains(song.getTitle()))
                .sorted(similarityComparator.reversed())
                .distinct()
                .limit(3)
                .toList();
    }

    public List<Double> createCurrentFeeling(String user){
        List<String> songsName = redisService.getListOf(user,2);
        List<Song> songs = songsName.stream().map(songService::getSongByName).toList();
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


    public static double[] normalize(double[] vector) {
        return Arrays.stream(vector)
                .map(val -> val / 10.0)
                .toArray();
    }
    public static List<Double> normalizeList(List<Double> vector) {
        return  vector.stream()
                .map(val -> val / 10.0)
                .toList();
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
    private Users getUser(String name){
        return userRepository.findByUsername(name).orElseThrow();
    }

}

