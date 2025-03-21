package com.reproductor.music.services;

import com.reproductor.music.dto.Convert;
import com.reproductor.music.dto.DTOLIST;
import com.reproductor.music.dto.request.ListRequest;
import com.reproductor.music.dto.request.RemoveSongRequest;
import com.reproductor.music.dto.request.SongToListRequest;
import com.reproductor.music.entities.Lista;
import com.reproductor.music.exceptions.ListException;
import com.reproductor.music.repositories.ListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListServiceImpl implements ListService {

    private final ListRepository listRepo;
    private final SongService songService;

    @Override
    @Transactional
    public DTOLIST createList(ListRequest list) {
        Lista lista = Lista.builder()
                .name(list.getName())
                .user(list.getUsername())
                .creation(new Date())
                .lastModification(new Date())
                .songs(new ArrayList<>())
                .principalEmotion("empty")
                .build();
        listRepo.save(lista);
        return Convert.convertListToDTO(lista);
    }

    @Override
    public DTOLIST getListByName(String name) {
        return Convert.convertListToDTO(getList(name));
    }

    @Override
    public List<DTOLIST> getListByDate(Date start, Date end) {
        return Convert.convertListDtoList(listRepo.findByCreationBetween(start, end));
    }

    @Override
    public String addToList(SongToListRequest request) {
        List<String> song = request.getSong().stream().map(s->songService.getSongByName(s).getName()).toList();
        Lista lista = getList(request.getName());
        StringBuilder response = new StringBuilder();
        for(String s: song) {
            if (lista.getSongs().isEmpty() || !lista.getSongs().contains(s)) {
                lista.getSongs().add(s);
                lista.setLastModification(new Date());
                response.append(s);
                response.append(" ");
            }
        }
        listRepo.save(lista);
        return response.isEmpty() ?
                "The songs are already in the list" :
                response.append(" are includes in the list").toString();
    }

    @Override
    public String removeFromList(RemoveSongRequest request) {
        String song = songService.getSongByName(request.getSongName()).getName();
        Lista lista = getList(request.getName());
        if(!lista.getSongs().contains(song))
            return song + " is not in the list";

        lista.getSongs().remove(song);
        lista.setLastModification(new Date());
        listRepo.save(lista);
        return song + " removed from the list";
    }

    @Override
    public List<DTOLIST> getAllList() {
        return Convert.convertListDtoList(listRepo.findAll());
    }

    private Lista getList(String name) {
        return listRepo.findByName(name).orElseThrow(()-> new ListException.ListNotFoundException(name + " List not found "));
    }

}
