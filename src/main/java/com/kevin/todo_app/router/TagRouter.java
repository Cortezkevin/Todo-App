package com.kevin.todo_app.router;

import com.kevin.todo_app.handler.TagHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class TagRouter {

    private final static String PATH = "/api/tag";

    @Bean
    public RouterFunction<ServerResponse> tagRoute(TagHandler handler){
        return RouterFunctions.route()
                .GET(PATH, handler::findAll)
                .POST(PATH, handler::create)
                .build();
    }

}
