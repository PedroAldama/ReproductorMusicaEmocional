package com.reproductor.music.services;

import com.reproductor.music.dto.DTOGroup;
import com.reproductor.music.dto.request.RequestGroup;
import com.reproductor.music.entities.Group;
import com.reproductor.music.exceptions.SongException;
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
                .orElseThrow(()->new SongException.ArtistNotFoundException(name + " not found"));
    }

    @Override
    @Transactional
    public void save(RequestGroup group) {
        Group newGroup = Group.builder().name(group.getName()).album(new ArrayList<>()).build();
        groupRepository.save(newGroup);
    }

}
