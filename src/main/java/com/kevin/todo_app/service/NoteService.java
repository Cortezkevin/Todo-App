package com.kevin.todo_app.service;

import com.kevin.todo_app.documents.Note;
import com.kevin.todo_app.dto.note.CreateNoteDTO;
import com.kevin.todo_app.dto.note.DetailedNoteDTO;
import com.kevin.todo_app.dto.note.MinimalNoteDTO;
import com.kevin.todo_app.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;

    public Flux<MinimalNoteDTO> findAll(){
        return noteRepository.findAll()
                .map(MinimalNoteDTO::toDTO);
    }

    public Mono<DetailedNoteDTO> findById(String id){
        return noteRepository.findById(id)
                .map(DetailedNoteDTO::toDTO)
                .switchIfEmpty(Mono.error(new RuntimeException("Note not found.")));
    }

    public Mono<DetailedNoteDTO> create(CreateNoteDTO createNoteDTO){
        return noteRepository.findByTitle(createNoteDTO.title())
                .flatMap(note -> Mono.error(new RuntimeException("Note already exists with this title.")))
                .switchIfEmpty(noteRepository.save(
                        Note.builder()
                                .title(createNoteDTO.title())
                                .content(createNoteDTO.content())
                                .createdAt(LocalDateTime.now())
                                .build()
                )).map(note -> DetailedNoteDTO.toDTO((Note) note));
    }

}
