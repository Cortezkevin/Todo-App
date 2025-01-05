package com.kevin.todo_app.repository;

import com.kevin.todo_app.documents.Tag;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TagRepository extends ReactiveMongoRepository<Tag, String> {
}
