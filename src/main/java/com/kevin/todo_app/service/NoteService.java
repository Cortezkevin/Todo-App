package com.kevin.todo_app.service;

import com.kevin.todo_app.documents.Element;
import com.kevin.todo_app.documents.Note;
import com.kevin.todo_app.dto.note.CreateNoteDTO;
import com.kevin.todo_app.dto.note.DetailedNoteDTO;
import com.kevin.todo_app.dto.note.MinimalNoteDTO;
import com.kevin.todo_app.dto.note.UpdateNoteDTO;
import com.kevin.todo_app.dto.tag.TagDTO;
import com.kevin.todo_app.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final TagService tagService;
    private final ReactiveMongoTemplate mongoTemplate;

    public Flux<MinimalNoteDTO> findAll(int page, int size){
        Pageable pageable = PageRequest.of(page, size);

        Query query = new Query().with(pageable);
        query.with(
                Sort.by(Sort.Order.desc("fixed"))  // Primero las fijadas
                        .and(Sort.by(Sort.Order.desc("fixedAt"))) // Si está fijada, se ordena por 'fixedAt' (más reciente primero)
                        .and(Sort.by(Sort.Order.desc("createdAt")))  // Las no fijadas se ordenan por 'createdAt' (más antigua primero)
        );

        return mongoTemplate.find(query, Note.class)
                .map(MinimalNoteDTO::toDTO);
    }

    public Mono<DetailedNoteDTO> findById(String id){
        return noteRepository.findById(id)
                .map(DetailedNoteDTO::toDTO)
                .switchIfEmpty(Mono.error(new RuntimeException("Note not found.")));
    }

    public Flux<MinimalNoteDTO> search(int page, int size, String title, List<String> tags) {
        Pageable pageable = PageRequest.of(page, size);

        Query query = new Query();
        query.with(pageable).with(Sort.by(Sort.Direction.ASC, "createdAt"));

        if( title != null){
            query.addCriteria(Criteria.where("title").regex(title, "i"));
        }
        if (tags != null && !tags.isEmpty()) {
            query.addCriteria(Criteria.where("tags").in(tags));
        }

        return mongoTemplate.find(query, Note.class)
                .map(MinimalNoteDTO::toDTO);
    }

    public Mono<DetailedNoteDTO> toggleFixNote(String id){
        return noteRepository.findById(id)
                .flatMap(note -> {
                    if(note.isFixed()){
                        note.setFixed(false);
                        note.setFixedAt(null);
                    }else {
                        note.setFixed(true);
                        note.setFixedAt(LocalDateTime.now());
                    }
                    return noteRepository.save(note);
                }).map(DetailedNoteDTO::toDTO)
                .switchIfEmpty(Mono.error(new RuntimeException("Note not found.")));
    }

    public Mono<DetailedNoteDTO> create(CreateNoteDTO createNoteDTO){
        return noteRepository.findByTitle(createNoteDTO.title())
                .flatMap(note -> Mono.error(new RuntimeException("Note already exists with this title.")))
                .switchIfEmpty(
                    tagService.findAllOrCreateByName(createNoteDTO.tags()).collectList()
                        .flatMap(tags ->
                            noteRepository.save(
                                Note.builder()
                                    .title(createNoteDTO.title())
                                    .content(createNoteDTO.content())
                                    .createdAt(LocalDateTime.now())
                                    .color(createNoteDTO.color())
                                    .tags(tags.stream().map(TagDTO::name).toList())
                                    .build()
                            )
                        )
                ).map(note -> DetailedNoteDTO.toDTO((Note) note));
    }

    public Mono<DetailedNoteDTO> update(UpdateNoteDTO updateNoteDTO){
        return noteRepository.findById(updateNoteDTO.id())
                .flatMap(note ->
                    noteRepository.existsByTitle(updateNoteDTO.title())
                        .flatMap(exists -> {
                            if(exists)
                                return Mono.error(new RuntimeException("This title already in use."));

                            // Obtener los elementos previos
                            List<Element> prevElements = note.getContent();
                            List<Element> updatedElements = new ArrayList<>();

                            // Crear un mapa de los índices de los elementos previos
                            Map<Integer, Element> prevElementsMap = prevElements.stream()
                                    .collect(Collectors.toMap(Element::getIndex, e -> e));

                            // Agregar los elementos actualizados
                            for (Element updatedElement : updateNoteDTO.content()) {
                                // Si el elemento está marcado para eliminar, no lo agregamos
                                if (updatedElement.isDelete()) {
                                    prevElementsMap.remove(updatedElement.getIndex()); // Eliminamos de prevElements
                                    continue; // No lo agregamos a updatedElements
                                }

                                // Si el índice existe en prevElementsMap, actualizamos el elemento
                                if (prevElementsMap.containsKey(updatedElement.getIndex())) {
                                    updatedElements.add(updatedElement);
                                    prevElementsMap.remove(updatedElement.getIndex()); // Eliminar el elemento reemplazado
                                } else {
                                    updatedElements.add(updatedElement); // Si no está, agregamos el nuevo elemento
                                }
                            }

                            // Los elementos que no fueron reemplazados se agregan
                            updatedElements.addAll(prevElementsMap.values());
                            updatedElements.sort(Comparator.comparing(Element::getIndex));

                            // Reasignar los índices de los elementos restantes
                            for (int i = 0; i < updatedElements.size(); i++) {
                                Element element = updatedElements.get(i);
                                element.setIndex(Integer.valueOf(String.valueOf(i)));  // Reasignar el índice al valor de su nueva posición
                            }
                            
                            note.setTitle(updateNoteDTO.title());
                            note.setUpdatedAt(LocalDateTime.now());
                            note.setContent(updatedElements);
                            note.setColor(updateNoteDTO.color());

                            return tagService.findAllOrCreateByName(updateNoteDTO.tags())
                                    .collectList()
                                    .flatMap(updatedTags -> {
                                        note.setTags(updatedTags.stream().map(TagDTO::name).toList());
                                        return noteRepository.save(note);
                                    });
                        })
                )
                .map(DetailedNoteDTO::toDTO)
                .switchIfEmpty(Mono.error(new RuntimeException("Note not found.")));
    }

    public Flux<DetailedNoteDTO> logicalDeleteByIds(List<String> ids){
        return noteRepository.findAllById(ids)
                .map(note -> {
                    note.setDeleted(true);
                    note.setDeletedAt(LocalDateTime.now());
                    log.info("Note {}", note);
                    return note;
                })
                .collectList()
                .doOnNext(notes -> {
                    log.info("Notes {}", notes);
                })
                .flatMapMany(noteRepository::saveAll)
                .map(DetailedNoteDTO::toDTO);
    }

    public Mono<DetailedNoteDTO> logicalDeleteById(String id){
        return noteRepository.findById(id)
                .flatMap(note -> {
                    note.setDeleted(true);
                    note.setDeletedAt(LocalDateTime.now());
                    return noteRepository.save(note);
                }).map(DetailedNoteDTO::toDTO)
                .switchIfEmpty(Mono.error(new RuntimeException("Note not found.")));
    }

    public Mono<String> physicalDeleteByIds(List<String> ids){
        return noteRepository.findAllById(ids)
                .filter(Note::isDeleted)
                .collectList()
                .flatMap(noteRepository::deleteAll)
                .then(Mono.just("Notas eliminadas totalmente."));
    }

    public Mono<String> physicalDeleteById(String id){
        return noteRepository.findById(id)
                .flatMap(note -> {
                    if( note.isDeleted() ){
                        return noteRepository.deleteById(id);
                    }else {
                        return Mono.error(new RuntimeException("La nota debe estar en la papelera."));
                    }
                })
                .then(Mono.just("Nota eliminada totalmente."))
                .switchIfEmpty(Mono.error(new RuntimeException("Note not found.")));
    }
}
