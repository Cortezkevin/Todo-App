package com.kevin.todo_app.handler;

import com.kevin.todo_app.dto.note.CreateNoteDTO;
import com.kevin.todo_app.dto.note.DetailedNoteDTO;
import com.kevin.todo_app.dto.note.MinimalNoteDTO;
import com.kevin.todo_app.dto.note.UpdateNoteDTO;
import com.kevin.todo_app.exception.ConstraintsFieldException;
import com.kevin.todo_app.helpers.ObjectValidator;
import com.kevin.todo_app.service.NoteService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NoteHandler {

    private final NoteService noteService;
    private final ObjectValidator validator;

    public Mono<ServerResponse> findAll(ServerRequest request){
        MultiValueMap<String, String> params = request.queryParams();
        int page = params.getFirst("page") == null ? 1 : Integer.parseInt(params.getFirst("page"));
        int size = params.getFirst("size") == null ? 5 : Integer.parseInt(params.getFirst("size"));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(noteService.findAll(page - 1, size), MinimalNoteDTO.class);
    }

    public Mono<ServerResponse> findAllDeleted(ServerRequest request){
        MultiValueMap<String, String> params = request.queryParams();
        int page = params.getFirst("page") == null ? 1 : Integer.parseInt(params.getFirst("page"));
        int size = params.getFirst("size") == null ? 5 : Integer.parseInt(params.getFirst("size"));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(noteService.findAllDeleted(page - 1, size), MinimalNoteDTO.class);
    }

    public Mono<ServerResponse> findAllFavorite(ServerRequest request){
        MultiValueMap<String, String> params = request.queryParams();
        int page = params.getFirst("page") == null ? 1 : Integer.parseInt(params.getFirst("page"));
        int size = params.getFirst("size") == null ? 5 : Integer.parseInt(params.getFirst("size"));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(noteService.findAllFavorites(page - 1, size), MinimalNoteDTO.class);
    }

    public Mono<ServerResponse> search(ServerRequest request){
        MultiValueMap<String, String> params = request.queryParams();
        int page = params.getFirst("page") == null ? 1 : Integer.parseInt(params.getFirst("page"));
        int size = params.getFirst("size") == null ? 5 : Integer.parseInt(params.getFirst("size"));
        String title = params.getFirst("title");

        String tagsParam = params.getFirst("tags");
        List<String> tags = tagsParam != null ? Arrays.asList(tagsParam.split(",")) : new ArrayList<>();
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(noteService.search(page - 1, size, title, tags), MinimalNoteDTO.class);
    }

    public Mono<ServerResponse> toggleFixNote(ServerRequest request){
        String id = request.pathVariable("id");
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(noteService.toggleFixNote(id), DetailedNoteDTO.class);
    }

    public Mono<ServerResponse> findById(ServerRequest request){
        String id = request.pathVariable("id");
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(noteService.findById(id), DetailedNoteDTO.class);
    }

    public Mono<ServerResponse> create(ServerRequest request){
        Mono<CreateNoteDTO> createNoteDTOMono = request.bodyToMono(CreateNoteDTO.class).doOnNext(validator::validate);
        return createNoteDTOMono.flatMap(createNoteDTO ->
                    ServerResponse.created(URI.create("/api/note"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(noteService.create(createNoteDTO), DetailedNoteDTO.class)
                );
    }

    public Mono<ServerResponse> update(ServerRequest request){
        Mono<UpdateNoteDTO> updateNoteDTOMono = request.bodyToMono(UpdateNoteDTO.class).doOnNext(validator::validate);
        return updateNoteDTOMono.flatMap(updateNoteDTO ->
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(noteService.update(updateNoteDTO), DetailedNoteDTO.class)
        );
    }

    public Mono<ServerResponse> toggleFavoriteById(ServerRequest request){
        String id = request.pathVariable("id");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(noteService.toggleFavorite(id), DetailedNoteDTO.class);
    }

    public Mono<ServerResponse> toggleFavoriteByIds(ServerRequest request){
        List<String> ids = Arrays.asList(request.queryParams().getFirst("ids").split(","));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(noteService.toggleFavorite(ids), DetailedNoteDTO.class);
    }

    public Mono<ServerResponse> logicalDeleteByIds(ServerRequest request){
        List<String> ids = Arrays.asList(request.queryParams().getFirst("ids").split(","));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(noteService.logicalDeleteByIds(ids), DetailedNoteDTO.class);
    }

    public Mono<ServerResponse> logicalDeleteById(ServerRequest request){
        String id = request.pathVariable("id");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(noteService.logicalDeleteById(id), DetailedNoteDTO.class);
    }

    public Mono<ServerResponse> physicalDeleteByIds(ServerRequest request){
        List<String> ids = Arrays.asList(request.queryParams().getFirst("ids").split(","));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(noteService.physicalDeleteByIds(ids), String.class);
    }

    public Mono<ServerResponse> physicalDeleteById(ServerRequest request){
        String id = request.pathVariable("id");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(noteService.physicalDeleteById(id), String.class);
    }

    public Mono<ServerResponse> restoreById(ServerRequest request){
        String id = request.pathVariable("id");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(noteService.restoreById(id), DetailedNoteDTO.class);
    }

    public Mono<ServerResponse> restoreByIds(ServerRequest request){
        List<String> ids = Arrays.asList(request.queryParams().getFirst("ids").split(","));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(noteService.restoreByIds(ids), DetailedNoteDTO.class);
    }
}
