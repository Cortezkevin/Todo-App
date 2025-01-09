package com.kevin.todo_app.service;

import com.kevin.todo_app.documents.User;
import com.kevin.todo_app.dto.user.CreateUserDTO;
import com.kevin.todo_app.dto.user.JwtDTO;
import com.kevin.todo_app.dto.user.LoginUserDTO;
import com.kevin.todo_app.dto.user.UserDTO;
import com.kevin.todo_app.enums.RolName;
import com.kevin.todo_app.repository.AuthRepository;
import com.kevin.todo_app.security.jwt.JwtProvider;
import com.kevin.todo_app.security.model.MainUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public Mono<UserDTO> findById(String id){
        return authRepository.findById(id)
                .map(UserDTO::toDTO)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")));
    }

    public Mono<JwtDTO> create(CreateUserDTO createUserDTO){
        List<RolName> newUserRoles = new ArrayList<>();
        return authRepository.existsByEmail( createUserDTO.email() ).flatMap( existsEmail -> {
            if(existsEmail){
                return Mono.error(new RuntimeException("This email already in use"));
            }else {
                if(!createUserDTO.password().equals(createUserDTO.confirmPassword())){
                    return Mono.error(new RuntimeException("The passwords not matches"));
                }
                if( createUserDTO.isAdmin() ){
                    newUserRoles.add(RolName.ROLE_ADMIN);
                }
                newUserRoles.add(RolName.ROLE_USER);
                User newUser = User.builder()
                        .email(createUserDTO.email())
                        .password(passwordEncoder.encode(createUserDTO.password()))
                        .roles(newUserRoles).build();
                return authRepository.save( newUser )
                        .map(userCreated ->
                                new JwtDTO(
                                        jwtProvider.generateToken(MainUser.build(userCreated)),
                                        UserDTO.toDTO(userCreated)
                                )
                        );
            }
        });
    }

    public Mono<JwtDTO> login(LoginUserDTO loginUserDTO){
        return authRepository.findByEmail(loginUserDTO.email()).flatMap( user -> {
                    if( !passwordEncoder.matches(loginUserDTO.password(), user.getPassword())) return Mono.error(new RuntimeException("Invalid Credentials"));
                    String token = jwtProvider.generateToken( MainUser.build( user ) );
                    return Mono.just(new JwtDTO( token, UserDTO.toDTO(user) ));
                })
                .switchIfEmpty( Mono.error(new RuntimeException("Invalid Credentials")) );
    }
}
