package com.kevin.todo_app.repository;

import com.kevin.todo_app.documents.user.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AuthRepository extends ReactiveMongoRepository<User, String> {
    Mono<User> findByEmail(String email);
    Mono<Boolean> existsByEmail(String email);
    Mono<User> findByTokenPassword(String tokenPassword);
}
