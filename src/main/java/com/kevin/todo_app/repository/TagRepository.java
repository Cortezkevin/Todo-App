package com.kevin.todo_app.repository;

import com.kevin.todo_app.documents.Tag;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface TagRepository extends ReactiveMongoRepository<Tag, String> {
    @Query("{ 'name' : { $in: ?0 } }") // Búsqueda por lista de nombres
    Flux<Tag> findAllByName(List<String> nameList);
    //Flux<Tag> findAllByName(Iterable<String> nameList);
}
