package com.reproductor.music.services.song;

import com.reproductor.music.dto.response.DTOSong;
import com.reproductor.music.entities.Song;
import com.reproductor.music.exceptions.SongExceptions;
import com.reproductor.music.repositories.SongRepository;
import com.reproductor.music.services.redis.RedisServiceImp;
import com.reproductor.music.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;


import static com.reproductor.music.utils.Convert.convertSongList;
import static com.reproductor.music.utils.Convert.convertSongToDto;

@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private static final String BASE_FOLDER = "C:/Users/Pedro/Desktop/songs/MusicFile/";
    private final SongRepository songRepository;
    private final RedisServiceImp redisService;
    private final UserUtils userUtils;

    @Override
    public List<DTOSong> getAllSongsResponse() {
        return convertSongList(songRepository.findAll());
    }

    @Override
    public Song getSongByName(String name) {
        return songRepository.findByName(name)
                .orElseThrow(() -> new SongExceptions.SongNotFoundException(name + " Not found"));
    }

    @Override
    public DTOSong getSongByNameResponse(String name) {
        String user = userUtils.getCurrentUserName();
        DTOSong songResponse =  convertSongToDto(songRepository.findByName(name)
                .orElseThrow(() -> new SongExceptions.SongNotFoundException(name + " Not found")));
        redisService.addSongToList(user,songResponse.getName());
        return songResponse;
    }

    @Override
    @Transactional
    public DTOSong addSong(String song,double duration, MultipartFile file) throws IOException {

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
                .name(song)
                .duration(duration)
                .src("MusicFile/" + song + extension)
                .build();
        songRepository.save(newSong);
        return convertSongToDto(newSong);
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
    public Set<String> findAllSrc() {
        return songRepository.findAllSrc();
    }

    @Override
    public String getSrc(String songName) {
        String src = songRepository.findSrcByName(songName);
        if(src.split(":track:").length > 1)
            return src.split(":track:")[1];

        return src;
    }

    @Override
    public String getOnlyName(String songName) {
        return songRepository.findOnlyName(songName);
    }


}
