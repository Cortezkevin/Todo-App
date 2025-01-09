package com.kevin.todo_app.handler;

import com.kevin.todo_app.dto.user.CreateUserDTO;
import com.kevin.todo_app.dto.user.JwtDTO;
import com.kevin.todo_app.dto.user.LoginUserDTO;
import com.kevin.todo_app.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthHandler {
    private final AuthService authService;

    public Mono<ServerResponse> create(ServerRequest req){
        Mono<CreateUserDTO> createUserDTOMono = req.bodyToMono(CreateUserDTO.class);
        return  createUserDTOMono.flatMap( createUserDTO -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body( authService.create(createUserDTO), JwtDTO.class ) );
    }

    public Mono<ServerResponse> login( ServerRequest req ){
        Mono<LoginUserDTO> loginUserDTOMono = req.bodyToMono(LoginUserDTO.class);
        return loginUserDTOMono.flatMap( loginUserDTO -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body( authService.login( loginUserDTO ), JwtDTO.class ) );
    }
}
