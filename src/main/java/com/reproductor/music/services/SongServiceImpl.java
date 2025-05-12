package com.reproductor.music.services;

import com.reproductor.music.dto.DTOSong;
import com.reproductor.music.dto.request.RequestSong;
import com.reproductor.music.entities.Song;
import com.reproductor.music.exceptions.SongException;
import com.reproductor.music.repositories.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


import static com.reproductor.music.dto.Convert.convertSongList;
import static com.reproductor.music.dto.Convert.convertSongToDto;

@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private static final String BASE_FOLDER = "C:/Users/Pedro/Desktop/songs/MusicFile/";
    private final SongRepository songRepository;

    @Override
    public List<DTOSong> getAllSongsResponse() {
        return convertSongList(songRepository.findAll());
    }

    @Override
    public Song getSongByName(String name) {
        return songRepository.findByName(name)
                .orElseThrow(() -> new SongException.SongNotFoundException(name + " Not found"));
    }

    @Override
    public DTOSong getSongByNameResponse(String name) {
        return convertSongToDto(songRepository.findByName(name)
                .orElseThrow(() -> new SongException.SongNotFoundException(name + " Not found")));
    }

    @Override
    @Transactional
    public DTOSong addSong(RequestSong song, MultipartFile file) throws IOException {

        File directory = new File(BASE_FOLDER);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(file, originalFilename);

        String fileName = song + extension;
        Path path = Paths.get(BASE_FOLDER + fileName);

        file.transferTo(path.toFile());

        Song newSong = Song.builder()
                .name(song.getTitle())
                .duration(song.getDuration())
                .src("MusicFile/" + song + extension)
                .build();
        songRepository.save(newSong);
        return getSongByNameResponse(newSong.getName());
    }

    private static String getExtension(MultipartFile file, String originalFilename) {
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        } else {
            // Como respaldo, obtenemos la extensión del tipo MIME
            String mimeType = file.getContentType();
            if (mimeType != null) {
                extension = switch (mimeType) {
                    case "audio/mpeg" -> ".mp3";
                    case "audio/wav" -> ".wav";
                    case "audio/ogg" -> ".ogg";
                    default -> "";
                };
            }
        }
        return extension;
    }

    @Override
    @Transactional
    public void save(Song song) {
        songRepository.save(song);
    }

    @Override
    public List<Song> findAllWithFeeling() {
        return songRepository.findAllWithFeelings();
    }


}
