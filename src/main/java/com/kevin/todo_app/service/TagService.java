package com.kevin.todo_app.service;

import com.kevin.todo_app.documents.Tag;
import com.kevin.todo_app.dto.tag.CreateTagDTO;
import com.kevin.todo_app.dto.tag.TagDTO;
import com.kevin.todo_app.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public Flux<TagDTO> findAll(){
        return tagRepository.findAll()
                .map(TagDTO::toDTO);
    }

    public Mono<TagDTO> create(CreateTagDTO createTagDTO){
        return tagRepository.save(
                Tag.builder()
                    .name(createTagDTO.name())
                    .build()
        ).map(TagDTO::toDTO);
    }

}
