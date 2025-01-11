package com.kevin.todo_app.handler;

import com.kevin.todo_app.dto.response.ResponseDTO;
import com.kevin.todo_app.dto.user.*;
import com.kevin.todo_app.helpers.ObjectValidator;
import com.kevin.todo_app.service.AuthService;
import com.kevin.todo_app.service.EmailService;
import com.kevin.todo_app.utils.ResponseConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    public Mono<ServerResponse> findById(ServerRequest request){
        String id = request.pathVariable("id");
        return ServerResponse.ok()
           .contentType(MediaType.APPLICATION_JSON)
           .body(
              authService.findById(id)
                 .map(userDTO ->
                    new ResponseDTO<>(
                       ResponseConstants.SUCCESS,
                       HttpStatus.OK,
                       userDTO
                    )
                 ),
              ResponseDTO.class
           );
    }

    public Mono<ServerResponse> create(ServerRequest req) {
        Mono<CreateUserDTO> createUserDTOMono = req.bodyToMono(CreateUserDTO.class).doOnNext(validator::validate);
        return createUserDTOMono.flatMap( createUserDTO ->
           ServerResponse.ok()
              .contentType(MediaType.APPLICATION_JSON)
              .body(
                 authService.create(createUserDTO)
                    .map(jwtDTO ->
                       new ResponseDTO<>(
                          ResponseConstants.USER_CREATED,
                          HttpStatus.CREATED,
                          jwtDTO
                       )
                    ),
                 ResponseDTO.class
              )
        );
    }

    public Mono<ServerResponse> login( ServerRequest req ){
        Mono<LoginUserDTO> loginUserDTOMono = req.bodyToMono(LoginUserDTO.class).doOnNext(validator::validate);
        return loginUserDTOMono.flatMap( loginUserDTO ->
           ServerResponse.ok()
              .contentType(MediaType.APPLICATION_JSON)
              .body(
                 authService.login( loginUserDTO )
                    .map(jwtDTO ->
                       new ResponseDTO<>(
                          ResponseConstants.SUCCESS_LOGIN,
                          HttpStatus.OK,
                          jwtDTO
                       )
                    ),
                 ResponseDTO.class
              )
        );
    }

    public Mono<ServerResponse> sendEmail(ServerRequest request){
        String to = request.pathVariable("to");
        return emailService.sendHtmlTemplateEmail( to )
           .flatMap( message ->
              ServerResponse.ok()
                 .contentType(MediaType.APPLICATION_JSON)
                 .body(
                    Mono.just(
                       new ResponseDTO<>(
                          ResponseConstants.EMAIL_SENT,
                          HttpStatus.OK,
                          message
                       )
                    ),
                    ResponseDTO.class
                 )
           );
    }

    public Mono<ServerResponse> changePassword( ServerRequest request){
        Mono<ChangePasswordDTO> changePasswordDTOMono = request.bodyToMono(ChangePasswordDTO.class).doOnNext(validator::validate);
        return changePasswordDTOMono.flatMap( dto ->
           ServerResponse.ok()
              .contentType(MediaType.APPLICATION_JSON)
              .body(
                 authService.changePassword( dto )
                    .map(msg ->
                       new ResponseDTO<>(
                          ResponseConstants.PASSWORD_CHANGED,
                          HttpStatus.OK,
                          msg
                       )
                    ),
                 ResponseDTO.class
              )
        );
    }
}
