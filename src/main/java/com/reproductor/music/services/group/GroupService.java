package com.reproductor.music.services.group;
import com.reproductor.music.dto.response.DTOGroup;
import com.reproductor.music.dto.request.RequestGroup;
import com.reproductor.music.entities.Group;

import java.util.*;
public interface GroupService {
     List<DTOGroup> findAll();
     Group findByName(String name);
     void save(String group);
}
