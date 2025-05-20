package com.reproductor.music.services.group;

import com.reproductor.music.dto.response.DTOGroup;
import com.reproductor.music.entities.Group;
import com.reproductor.music.exceptions.SongExceptions;
import com.reproductor.music.repositories.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.reproductor.music.utils.Convert.convertGroupDtoList;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService{

    private final GroupRepository groupRepository;

    @Override
    public List<DTOGroup> findAll() {
        return convertGroupDtoList(groupRepository.findAll());
    }

    @Override
    public Group findByName(String name) {
        return groupRepository
                .findByName(name)
                .orElseThrow(()->new SongExceptions.ArtistNotFoundException(name + " not found"));
    }

    @Override
    @Transactional
    public void save(String group) {
        Group newGroup = Group.builder().name(group).album(new ArrayList<>()).build();
        groupRepository.save(newGroup);
    }

}
