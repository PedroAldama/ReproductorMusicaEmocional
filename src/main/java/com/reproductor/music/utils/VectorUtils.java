package com.reproductor.music.utils;

import com.reproductor.music.entities.SongFeelings;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VectorUtils {

    public double cosineSimilarity(List<Double> v1, List<Double> v2) {
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
    public  List<Double> normalize(List<Double> vector) {
        return vector.stream()
                .map(val -> val / 10.0)
                .toList();
    }
    public  List<Double> average(List<List<Double>> vector) {
        if(vector.isEmpty()) return List.of();

        int size = vector.getFirst().size();
        double[] avg = new double[size];
        List<Double> data = new ArrayList<>();
        for(List<Double> vec: vector){
            if(vec.isEmpty()) continue;
            for(int i = 0; i < size; i++){
                avg[i] += vec.get(i);
            }
        }
        for(int i = 0; i < size; i++){
            data.add(avg[i] / size);
        }
        return data;
    }
    public Map<String,Double> getFeelingsMap(List<SongFeelings> songFeelings){
        Map<String,Double> data = new HashMap<>();
        String[] feeling = {"Alegria", "Tristeza", "Calma", "Euforia", "Melancolia", "Amor", "Ira",
                "Ansiedad", "Misterio", "Empoderamiento"};
        for (int i = 0; i < songFeelings.size(); i++) {
            data.put(feeling[i], songFeelings.get(i).getValue());
        }
        return data;
    }
}
