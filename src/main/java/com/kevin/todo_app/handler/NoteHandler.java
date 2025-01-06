package com.kevin.todo_app.handler;

import com.kevin.todo_app.dto.note.CreateNoteDTO;
import com.kevin.todo_app.dto.note.DetailedNoteDTO;
import com.kevin.todo_app.dto.note.MinimalNoteDTO;
import com.kevin.todo_app.dto.note.UpdateNoteDTO;
import com.kevin.todo_app.service.NoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class NoteHandler {

    private final NoteService noteService;

    public Mono<ServerResponse> findAll(ServerRequest request){
        MultiValueMap<String, String> params = request.queryParams();
        int page = Integer.parseInt(Objects.requireNonNull(params.getFirst("page")));
        int size = Integer.parseInt(Objects.requireNonNull(params.getFirst("size")));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(noteService.findAll(page, size), MinimalNoteDTO.class);
    }

    public Mono<ServerResponse> search(ServerRequest request){
        MultiValueMap<String, String> params = request.queryParams();
        int page = params.getFirst("page") == null ? 1 : Integer.parseInt(params.getFirst("page"));
        int size = params.getFirst("page") == null ? 5 : Integer.parseInt(params.getFirst("size"));
        String title = params.getFirst("title");
        List<String> tags = params.get("tags");
        log.info("TAGS QUERY -> {}", tags);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(noteService.search(page, size, title, tags), MinimalNoteDTO.class);
    }

    public Mono<ServerResponse> findById(ServerRequest request){
        String id = request.pathVariable("id");
        return noteService.findById(id)
                .flatMap( detailedNoteDTO ->
                        ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                                .body(detailedNoteDTO, DetailedNoteDTO.class)
                );
    }

    public Mono<ServerResponse> create(ServerRequest request){
        Mono<CreateNoteDTO> createNoteDTOMono = request.bodyToMono(CreateNoteDTO.class);
        return createNoteDTOMono.flatMap(createNoteDTO ->
                        ServerResponse.created(URI.create("/api/note"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(noteService.create(createNoteDTO), DetailedNoteDTO.class)
                );
    }

    public Mono<ServerResponse> update(ServerRequest request){
        Mono<UpdateNoteDTO> updateNoteDTOMono = request.bodyToMono(UpdateNoteDTO.class);
        return updateNoteDTOMono.flatMap(updateNoteDTO ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(noteService.update(updateNoteDTO), DetailedNoteDTO.class)
        );
    }
}
