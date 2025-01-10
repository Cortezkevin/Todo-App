package com.kevin.todo_app.handler;

import com.kevin.todo_app.dto.user.ChangePasswordDTO;
import com.kevin.todo_app.dto.user.CreateUserDTO;
import com.kevin.todo_app.dto.user.JwtDTO;
import com.kevin.todo_app.dto.user.LoginUserDTO;
import com.kevin.todo_app.helpers.ObjectValidator;
import com.kevin.todo_app.service.AuthService;
import com.kevin.todo_app.service.EmailService;
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
    private final EmailService emailService;
    private final ObjectValidator validator;

    public Mono<ServerResponse> create(ServerRequest req) {
        Mono<CreateUserDTO> createUserDTOMono = req.bodyToMono(CreateUserDTO.class).doOnNext(validator::validate);
        return  createUserDTOMono.flatMap( createUserDTO -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body( authService.create(createUserDTO), JwtDTO.class ) );
    }

    public Mono<ServerResponse> login( ServerRequest req ){
        Mono<LoginUserDTO> loginUserDTOMono = req.bodyToMono(LoginUserDTO.class).doOnNext(validator::validate);
        return loginUserDTOMono.flatMap( loginUserDTO -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body( authService.login( loginUserDTO ), JwtDTO.class ) );
    }

    public Mono<ServerResponse> sendEmail(ServerRequest request){
        String to = request.pathVariable("to");
        return emailService.sendHtmlTemplateEmail( to )
                .flatMap( message ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(Mono.just(message), String.class)
                );
    }

    public Mono<ServerResponse> changePassword( ServerRequest request){
        Mono<ChangePasswordDTO> changePasswordDTOMono = request.bodyToMono(ChangePasswordDTO.class).doOnNext(validator::validate);
        return changePasswordDTOMono.flatMap( dto ->  org.springframework.web.reactive.function.server.ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body( authService.changePassword( dto ), String.class ));
    }
}
