package com.kevin.todo_app.handler;

import com.kevin.todo_app.dto.response.ResponseDTO;
import com.kevin.todo_app.dto.tag.CreateTagDTO;
import com.kevin.todo_app.dto.tag.TagDTO;
import com.kevin.todo_app.service.TagService;
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
public class TagHandler {

    private final TagService tagService;

    public Mono<ServerResponse> findAll(ServerRequest request){
        return ServerResponse.ok()
           .contentType(MediaType.APPLICATION_JSON)
           .body(
              tagService.findAll()
                 .collectList().map(tagsDTO ->
                    new ResponseDTO<>(
                       ResponseConstants.SUCCESS,
                       HttpStatus.OK,
                       tagsDTO
                    )
                 ),
              ResponseDTO.class
           );
    }

    public Mono<ServerResponse> create(ServerRequest request){
        Mono<CreateTagDTO> createTagDTOMono = request.bodyToMono(CreateTagDTO.class);
        return createTagDTOMono.flatMap(createTagDTO ->
           ServerResponse.ok()
              .contentType(MediaType.APPLICATION_JSON)
              .body(
                 tagService.create(createTagDTO)
                    .map(tagDTO ->
                       new ResponseDTO<>(
                          ResponseConstants.TAG_CREATED,
                          HttpStatus.OK,
                          tagDTO
                       )
                    ),
                 ResponseDTO.class
              )
        );
    }

}
