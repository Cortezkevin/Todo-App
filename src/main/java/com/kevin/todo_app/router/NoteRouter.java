package com.kevin.todo_app.router;

import com.kevin.todo_app.handler.NoteHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class NoteRouter {

    private final static String PATH = "/api/note";

    @Bean
    public RouterFunction<ServerResponse> noteRoute(NoteHandler handler){
        return RouterFunctions.route()
                .GET(PATH, handler::findAll)
                .GET(PATH + "/find/{id}", handler::findById)
                .GET(PATH + "/search", handler::search)
                .GET(PATH + "/toggleFix/{id}", handler::toggleFixNote)
                .POST(PATH, handler::create)
                .PUT(PATH, handler::update)
                .DELETE(PATH + "/one/{id}", handler::deleteById)
                .DELETE(PATH + "/many", handler::deleteByIds)
                .build();
    }

}
