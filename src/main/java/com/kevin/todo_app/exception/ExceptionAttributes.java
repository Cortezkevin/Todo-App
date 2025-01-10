package com.kevin.todo_app.exception;

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
        if(throwable instanceof ResourceNotFoundException resourceNotFoundException){
            errorAttributes.put("status", HttpStatus.NOT_FOUND);
            errorAttributes.put("message", resourceNotFoundException.getMessage());
        }
        if ( throwable instanceof ConstraintsFieldException constraintsFieldException ){
            errorAttributes.put("fields", constraintsFieldException.getFieldsErrors());
            errorAttributes.put("status", HttpStatus.BAD_REQUEST);
            errorAttributes.put("message", constraintsFieldException.getMessage());
        }
        if( throwable instanceof MalformedJwtException malformedJwtException){
            errorAttributes.put("message", malformedJwtException.getMessage());
            errorAttributes.put("status", HttpStatus.BAD_REQUEST);
        }
        if( throwable instanceof SignatureException signatureException){
            errorAttributes.put("message", "The token is invalid");
            errorAttributes.put("detailError", signatureException.getMessage());
            errorAttributes.put("status", HttpStatus.BAD_REQUEST);
        }
        if( throwable instanceof MalformedJwtException malformedJwtException){
            errorAttributes.put("message", "The token is malformed");
            errorAttributes.put("detailError", malformedJwtException.getMessage());
            errorAttributes.put("status", HttpStatus.BAD_REQUEST);
        }
        if( throwable instanceof ExpiredJwtException expiredJwtException){
            errorAttributes.put("message", "The session has expired");
            errorAttributes.put("detailError", expiredJwtException.getMessage());
            errorAttributes.put("status", HttpStatus.BAD_REQUEST);
        }else {
            errorAttributes.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
            errorAttributes.put("message", "An unexpected error occurred.");
            errorAttributes.put("detailError", throwable.getMessage());
            errorAttributes.put("errorType", "UnknownException");
        }
        return errorAttributes;
    }
}
