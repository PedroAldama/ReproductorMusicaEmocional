package com.reproductor.music.services.list;

import com.reproductor.music.dto.DTOLIST;
import com.reproductor.music.dto.request.ListRequest;
import com.reproductor.music.dto.request.RemoveSongRequest;
import com.reproductor.music.dto.request.SongToListRequest;

import java.util.Date;
import java.util.List;

public interface ListService {
    DTOLIST createList(ListRequest listRequest);
    DTOLIST getListByName(String name);
    List<DTOLIST> getListByDate(Date start, Date end);
    String addToList(SongToListRequest request);
    String removeFromList(RemoveSongRequest request);
    List<DTOLIST> getAllList();
}
