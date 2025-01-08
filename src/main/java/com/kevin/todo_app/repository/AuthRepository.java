package com.kevin.todo_app.repository;

import com.kevin.todo_app.documents.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AuthRepository extends ReactiveMongoRepository<User, String> {
    Mono<User> findByUsername(String username);
}
