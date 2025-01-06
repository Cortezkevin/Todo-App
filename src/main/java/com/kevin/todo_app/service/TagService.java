package com.kevin.todo_app.service;

import com.kevin.todo_app.documents.Note;
import com.kevin.todo_app.documents.Tag;
import com.kevin.todo_app.dto.tag.CreateTagDTO;
import com.kevin.todo_app.dto.tag.TagDTO;
import com.kevin.todo_app.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public Flux<TagDTO> findAllOrCreateByName(List<String> nameList){
        return tagRepository.findAllByName(nameList)
                .collectList()
                .flatMapMany(existingTags -> {
                    Set<String> existingNames = existingTags.stream()
                            .map(Tag::getName)
                            .collect(Collectors.toSet());

                    List<String> notFoundNames = nameList.stream()
                            .filter(name -> !existingNames.contains(name))
                            .toList();

                    Flux<Tag> newTags = tagRepository.saveAll(
                            notFoundNames.stream()
                                    .map(newName -> Tag.builder().name(newName).build())
                                    .collect(Collectors.toList())
                    );

                    return Flux.concat(
                            Flux.fromIterable(existingTags),
                            newTags
                    ).map(TagDTO::toDTO);
                });
    }
}
