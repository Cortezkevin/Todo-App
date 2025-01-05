package com.kevin.todo_app.service;

import com.kevin.todo_app.documents.Element;
import com.kevin.todo_app.documents.Note;
import com.kevin.todo_app.dto.note.CreateNoteDTO;
import com.kevin.todo_app.dto.note.DetailedNoteDTO;
import com.kevin.todo_app.dto.note.MinimalNoteDTO;
import com.kevin.todo_app.dto.note.UpdateNoteDTO;
import com.kevin.todo_app.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final ReactiveMongoTemplate mongoTemplate;

    public Flux<MinimalNoteDTO> findAll(int page, int size){
        // Crea un Pageable con el número de página y tamaño
        Pageable pageable = PageRequest.of(page, size);

        // Crea la consulta utilizando el Pageable
        Query query = new Query().with(pageable).with(Sort.by(Sort.Direction.ASC, "title"));

        // Realiza la consulta reactiva
        return mongoTemplate.find(query, Note.class)
                .map(MinimalNoteDTO::toDTO);  // Mapear a DTO
    }

//    public Flux<MinimalNoteDTO> findAll(){
//        return noteRepository.findAll()
//                .map(MinimalNoteDTO::toDTO);
//    }

    public Mono<DetailedNoteDTO> findById(String id){
        return noteRepository.findById(id)
                .map(DetailedNoteDTO::toDTO)
                .switchIfEmpty(Mono.error(new RuntimeException("Note not found.")));
    }

    public Mono<MinimalNoteDTO> search(){

    }

    public Mono<DetailedNoteDTO> create(CreateNoteDTO createNoteDTO){
        return noteRepository.findByTitle(createNoteDTO.title())
                .flatMap(note -> Mono.error(new RuntimeException("Note already exists with this title.")))
                .switchIfEmpty(noteRepository.save(
                        Note.builder()
                                .title(createNoteDTO.title())
                                .content(createNoteDTO.content())
                                .createdAt(LocalDateTime.now())
                                .color(createNoteDTO.color())
                                .build()
                )).map(note -> DetailedNoteDTO.toDTO((Note) note));
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
                            return noteRepository.save(note);
                        })
                )
                .map(DetailedNoteDTO::toDTO)
                .switchIfEmpty(Mono.error(new RuntimeException("Note not found.")));
    }

    public Mono<DetailedNoteDTO> deleteById(String id){
        return noteRepository.findById(id)
                .flatMap(note ->
                    noteRepository.deleteById(id)
                        .then(Mono.just(note))
                ).map(DetailedNoteDTO::toDTO)
                .switchIfEmpty(Mono.error(new RuntimeException("Note not found.")));
    }
}
