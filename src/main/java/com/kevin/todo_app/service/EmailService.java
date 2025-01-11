package com.kevin.todo_app.service;

import com.kevin.todo_app.documents.user.User;
import com.kevin.todo_app.exception.custom.ResourceNotFoundException;
import com.kevin.todo_app.repository.AuthRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailService {

   private final JavaMailSender javaMailSender;
   private final TemplateEngine templateEngine;
   private final AuthRepository userRepository;

   public Mono<String> sendHtmlTemplateEmail(String to) {
      Mono<User> user = userRepository.findByEmail(to);
      return user.flatMap(userFound ->
         Mono.fromCallable(() -> {
            try {
               MimeMessage mimeMessage = javaMailSender.createMimeMessage();
               MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
               Context context = new Context();

               UUID token = UUID.randomUUID();
               userFound.setTokenPassword(token.toString());

               userRepository.save(userFound).subscribe();

               Map<String, Object> model = new HashMap<>();
               model.put("username", userFound.getEmail());
               model.put("url", "http://localhost:4200/changePassword/" + token);
               context.setVariables(model);

               String htmlText = templateEngine.process("email_template", context);
               helper.setFrom("cortezkevinq@gmail.com");
               helper.setTo(to);
               helper.setSubject("Prueba envio email");
               helper.setText(htmlText, true);

               javaMailSender.send(mimeMessage);
               return "Email de confirmación enviado";
            } catch (MessagingException e) {
               return "Ocurrió un error al enviar el email";
            }
         }))
      .switchIfEmpty(Mono.error(new ResourceNotFoundException("User", "Email", to)));
   }

}

