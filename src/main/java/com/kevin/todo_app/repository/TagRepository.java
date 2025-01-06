package com.kevin.todo_app.repository;

import com.kevin.todo_app.documents.Tag;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface TagRepository extends ReactiveMongoRepository<Tag, String> {
    Flux<Tag> findAllByName(Iterable<String> nameList);
}
