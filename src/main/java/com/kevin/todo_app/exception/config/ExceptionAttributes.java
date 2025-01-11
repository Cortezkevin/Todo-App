package com.kevin.todo_app.exception.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kevin.todo_app.exception.custom.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.HashMap;
import java.util.Map;

@Component
public class ExceptionAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = new HashMap<>();
        Throwable throwable = super.getError(request);

        errorAttributes.put("detail", null);

        switch (throwable) {
            case ResourceNotFoundException resourceNotFoundException -> {
                errorAttributes.put("status", HttpStatus.NOT_FOUND);
                errorAttributes.put("message", resourceNotFoundException.getMessage());
            }
            case AlreadyExistsResourceWithFieldException alreadyExistsResourceWithFieldException -> {
                errorAttributes.put("status", HttpStatus.BAD_REQUEST);
                errorAttributes.put("message", alreadyExistsResourceWithFieldException.getMessage());
            }
            case PasswordsNotMatchException passwordsNotMatchException -> {
                errorAttributes.put("status", HttpStatus.BAD_REQUEST);
                errorAttributes.put("message", passwordsNotMatchException.getMessage());
            }
            case InvalidCredentialsException invalidCredentialsException -> {
                errorAttributes.put("status", HttpStatus.BAD_REQUEST);
                errorAttributes.put("message", invalidCredentialsException.getMessage());
            }
            case NotInTheTrashException notInTheTrashException -> {
                errorAttributes.put("status", HttpStatus.BAD_REQUEST);
                errorAttributes.put("message", notInTheTrashException.getMessage());
            }
            case ConstraintsFieldException constraintsFieldException -> {
                errorAttributes.put("fields", constraintsFieldException.getFieldsErrors());
                errorAttributes.put("status", HttpStatus.BAD_REQUEST);
                errorAttributes.put("message", constraintsFieldException.getMessage());
            }
            case MalformedJwtException malformedJwtException -> {
                errorAttributes.put("message", "The token is malformed");
                errorAttributes.put("detail", malformedJwtException.getMessage());
                errorAttributes.put("status", HttpStatus.BAD_REQUEST);
            }
            case SignatureException signatureException -> {
                errorAttributes.put("message", "The token is invalid");
                errorAttributes.put("detail", signatureException.getMessage());
                errorAttributes.put("status", HttpStatus.BAD_REQUEST);
            }
            case ExpiredJwtException expiredJwtException -> {
                errorAttributes.put("message", "The session has expired");
                errorAttributes.put("detail", expiredJwtException.getMessage());
                errorAttributes.put("status", HttpStatus.BAD_REQUEST);
            }
            case null, default -> {
                errorAttributes.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
                errorAttributes.put("message", "An unexpected error occurred.");
                errorAttributes.put("detail", throwable.getMessage());
                errorAttributes.put("errorType", "UnknownException");
            }
        }
        return errorAttributes;
    }

    private Map<?,?> toMap(Object o){
        ObjectMapper mapper = new ObjectMapper();
        return  mapper.convertValue(o, Map.class);
    }
}
