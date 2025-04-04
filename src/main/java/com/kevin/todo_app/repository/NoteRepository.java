package com.kevin.todo_app.repository;

import com.kevin.todo_app.documents.note.Note;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public interface NoteRepository extends ReactiveMongoRepository<Note, String> {
    Mono<Note> findByIdAndUser(String id, String user);
    Mono<Note> findByTitleAndUser(String title, String user);
    Mono<Boolean> existsByTitleAndUser(String title, String user);
}
