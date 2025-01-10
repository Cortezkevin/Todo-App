package com.kevin.todo_app.service;

import com.kevin.todo_app.documents.Tag;
import com.kevin.todo_app.dto.tag.CreateTagDTO;
import com.kevin.todo_app.dto.tag.TagDTO;
import com.kevin.todo_app.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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
        log.info("nameList -> {}", nameList);
        return tagRepository.findAllByName(nameList)
                .collectList()
                .flatMapMany(existingTags -> {
                    List<String> existingNames = existingTags.stream()
                            .map(Tag::getName)
                            .toList();

                    log.info("existingTags -> {}", existingNames);

                    List<String> notFoundNames = nameList.stream()
                            .filter(name -> !existingNames.contains(name))
                            .toList();

                    log.info("notFoundNames -> {}", notFoundNames);


                    Flux<Tag> newTags = tagRepository.saveAll(
                            notFoundNames.stream()
                                    .map(newName -> Tag.builder().name(newName).build())
                                    .collect(Collectors.toList())
                    );

                    log.info("newTags -> {}", newTags);

                    return Flux.concat(
                            Flux.fromIterable(existingTags),
                            newTags
                    ).map(TagDTO::toDTO);
                });
    }
}
