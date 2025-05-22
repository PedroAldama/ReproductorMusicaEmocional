package com.reproductor.music.services.spotify;

import com.reproductor.music.dto.response.DTOSong;
import com.reproductor.music.entities.Album;
import com.reproductor.music.entities.Group;
import com.reproductor.music.entities.Song;
import com.reproductor.music.repositories.SongRepository;
import com.reproductor.music.services.album.AlbumService;
import com.reproductor.music.services.group.GroupService;
import com.reproductor.music.services.song.SongService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.reproductor.music.utils.Convert.convertSongList;

@Service@AllArgsConstructor
public class MusicServiceImpl {

    private final GroupService groupService;
    private final AlbumService albumService;
    private final SongService songService;
    private final SongRepository songRepository;

    public List<DTOSong> saveSongs(List<Map<String, Object>> items) {
        Set<String> allSongsRegister = songService.findAllSrc();
        Set<String> allGroupRegister = groupService.getAllGroups();

        Map<String, Group> groupCache = new HashMap<>();
        Map<String, Album> albumCache = new HashMap<>();

        List<Song> songsToSave = new ArrayList<>();

        for (Map<String, Object> item : items) {
            Map<String, Object> track = getMap(item, "track");
            String src = (String) track.get("uri");

            if (allSongsRegister.contains(src)) continue;

            String songName = (String) track.get("name");
            double duration = Math.round(((Integer) track.get("duration_ms") / 60000.0) * 100.0) / 100.0;

            String albumTitle = getAlbumTitle(track);
            int releaseYear = getAlbumReleaseYear(track);
            String groupName = getGroupName(track);

            Group group = groupCache.computeIfAbsent(groupName, name -> {
                if (!allGroupRegister.contains(name)) {
                    groupService.save(name);
                    allGroupRegister.add(name);
                }
                return groupService.searchByName(name).orElse(null);
            });

            Album album = albumCache.get(albumTitle);
            if (album == null) {
                Optional<Album> albumOpt = albumService.searchAlbumByName(albumTitle);
                if (albumOpt.isPresent()) {
                    album = albumOpt.get();
                } else {
                    album = new Album();
                    album.setTitle(albumTitle);
                    album.setReleaseDate(releaseYear);
                    album.setGroup(group);
                    albumService.save(album);
                }
                albumCache.put(albumTitle, album);
            }



            Song song = new Song();
            song.setName(songName);
            song.setDuration(duration);
            song.setSrc(src);
            song.setAlbum(album);

            songsToSave.add(song);
            allSongsRegister.add(src);
        }

        if (!songsToSave.isEmpty()) {
            songRepository.saveAll(songsToSave);
        }

        return convertSongList(songsToSave);
    }

    // Helper methods
    private Map<String, Object> getMap(Map<String, Object> parent, String key) {
        return (Map<String, Object>) parent.get(key);
    }

    private String getAlbumTitle(Map<String, Object> track) {
        Map<String, Object> album = getMap(track, "album");
        return album.isEmpty() ? "Unknown" : (String) album.get("name");
    }

    private int getAlbumReleaseYear(Map<String, Object> track) {
        Map<String, Object> album = getMap(track, "album");
        String dateStr = (String) album.get("release_date");
        return (dateStr != null && dateStr.length() >= 4) ? Integer.parseInt(dateStr.substring(0, 4)) : 0;
    }

    private String getGroupName(Map<String, Object> track) {
        List<Map<String, Object>> artists = (List<Map<String, Object>>) track.get("artists");
        return (String) artists.getFirst().get("name");
    }
}
