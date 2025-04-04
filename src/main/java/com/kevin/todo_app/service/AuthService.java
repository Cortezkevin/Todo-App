package com.kevin.todo_app.service;

import com.kevin.todo_app.documents.user.User;
import com.kevin.todo_app.dto.user.*;
import com.kevin.todo_app.enums.RolName;
import com.kevin.todo_app.exception.custom.AlreadyExistsResourceWithFieldException;
import com.kevin.todo_app.exception.custom.InvalidCredentialsException;
import com.kevin.todo_app.exception.custom.PasswordsNotMatchException;
import com.kevin.todo_app.exception.custom.ResourceNotFoundException;
import com.kevin.todo_app.repository.AuthRepository;
import com.kevin.todo_app.security.jwt.JwtProvider;
import com.kevin.todo_app.security.model.MainUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
   private final AuthRepository authRepository;
   private final PasswordEncoder passwordEncoder;
   private final JwtProvider jwtProvider;

   private Mono<User> findUserByEmail(String email){
      return authRepository.findByEmail(email)
         .switchIfEmpty(Mono.error(new ResourceNotFoundException("User", "Email", email)));
   }

   private Mono<User> findUserById(String id) {
      return authRepository.findById(id)
         .switchIfEmpty(Mono.error(new ResourceNotFoundException("User", "Id", id)));
   }

   public Mono<UserDTO> findById(String id) {
      return this.findUserById(id)
         .map(UserDTO::toDTO);
   }

   public Mono<JwtDTO> create(CreateUserDTO createUserDTO) {
      List<RolName> newUserRoles = new ArrayList<>();
      return authRepository.existsByEmail(createUserDTO.email())
         .flatMap(existsEmail -> {
            if (existsEmail) {
               return Mono.error(new AlreadyExistsResourceWithFieldException("User", "Email", createUserDTO.email()));
            } else {
            if (!createUserDTO.password().equals(createUserDTO.confirmPassword())) {
               return Mono.error(new PasswordsNotMatchException());
            }
            if (createUserDTO.isAdmin()) {
               newUserRoles.add(RolName.ROLE_ADMIN);
            }

            newUserRoles.add(RolName.ROLE_USER);
            User newUser = User.builder()
               .email(createUserDTO.email())
               .password(passwordEncoder.encode(createUserDTO.password()))
               .roles(newUserRoles).build();

            return authRepository.save(newUser)
               .map(userCreated ->
                  new JwtDTO(
                     jwtProvider.generateToken(MainUser.build(userCreated)),
                     UserDTO.toDTO(userCreated)
                  )
               );
         }
      });
   }

   public Mono<JwtDTO> login(LoginUserDTO loginUserDTO) {
      return authRepository.findByEmail(loginUserDTO.email())
         .flatMap(user -> {
            if (!passwordEncoder.matches(loginUserDTO.password(), user.getPassword()))
               return Mono.error(new InvalidCredentialsException());

            String token = jwtProvider.generateToken(MainUser.build(user));
            return Mono.just(new JwtDTO(token, UserDTO.toDTO(user)));
         })
         .switchIfEmpty(Mono.error(new InvalidCredentialsException()));
   }

   public Mono<String> changePassword(ChangePasswordDTO dto) {
      return authRepository.findByTokenPassword(dto.tokenPassword())
         .flatMap(user -> {
            if (dto.password().equals(dto.confirmPassword())) {
               user.setPassword(passwordEncoder.encode(dto.password()));
               user.setTokenPassword(null);

               return authRepository.save(user).flatMap(userUpdated -> Mono.just("Password updated"));
            } else {
               return Mono.error(new PasswordsNotMatchException());
            }
         })
         .switchIfEmpty(Mono.error(new ResourceNotFoundException("User", "Token Password", dto.tokenPassword())));
   }

   public Mono<JwtDTO> validateToken(String token) {
      String userFromToken = null;
      if(jwtProvider.validateToken(token)){
         userFromToken = jwtProvider.getUserNameFromToken(token);
      }
      return this.findUserByEmail(userFromToken)
         .map( user -> {
            String newToken = jwtProvider.generateToken(MainUser.build(user));
            return new JwtDTO(newToken, UserDTO.toDTO(user));
         });
   }
}
