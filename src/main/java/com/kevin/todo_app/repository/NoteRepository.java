package com.kevin.todo_app.repository;

import com.kevin.todo_app.documents.Note;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface NoteRepository extends ReactiveMongoRepository<Note, String> {
    Mono<Note> findByTitle(String title);
    Mono<Boolean> existsByTitle(String title);
}
