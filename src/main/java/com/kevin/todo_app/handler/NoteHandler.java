package com.kevin.todo_app.handler;

import com.kevin.todo_app.dto.note.CreateNoteDTO;
import com.kevin.todo_app.dto.note.DetailedNoteDTO;
import com.kevin.todo_app.dto.note.MinimalNoteDTO;
import com.kevin.todo_app.dto.note.UpdateNoteDTO;
import com.kevin.todo_app.dto.response.ResponseDTO;
import com.kevin.todo_app.helpers.ObjectValidator;
import com.kevin.todo_app.service.NoteService;
import com.kevin.todo_app.utils.ResponseConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
           .body(
              noteService.findAll(page - 1, size).collectList()
                 .map(minimalNoteDTOS ->
                    new ResponseDTO<>(
                       ResponseConstants.SUCCESS,
                       HttpStatus.OK,
                       minimalNoteDTOS
                    )
                 ),
              ResponseDTO.class
           );
    }

    public Mono<ServerResponse> findAllDeleted(ServerRequest request){
        MultiValueMap<String, String> params = request.queryParams();
        int page = params.getFirst("page") == null ? 1 : Integer.parseInt(params.getFirst("page"));
        int size = params.getFirst("size") == null ? 5 : Integer.parseInt(params.getFirst("size"));
        return ServerResponse.ok()
           .contentType(MediaType.APPLICATION_JSON)
           .body(
              noteService.findAllDeleted(page - 1, size).collectList()
                 .map(minimalNoteDTOS ->
                    new ResponseDTO<>(
                       ResponseConstants.SUCCESS,
                       HttpStatus.OK,
                       minimalNoteDTOS
                    )
                 ),
              ResponseDTO.class
           );
    }

    public Mono<ServerResponse> findAllFavorite(ServerRequest request){
        MultiValueMap<String, String> params = request.queryParams();
        int page = params.getFirst("page") == null ? 1 : Integer.parseInt(params.getFirst("page"));
        int size = params.getFirst("size") == null ? 5 : Integer.parseInt(params.getFirst("size"));
        return ServerResponse.ok()
           .contentType(MediaType.APPLICATION_JSON)
           .body(
              noteService.findAllFavorites(page - 1, size).collectList()
                 .map(minimalNoteDTOS ->
                    new ResponseDTO<>(
                       ResponseConstants.SUCCESS,
                       HttpStatus.OK,
                       minimalNoteDTOS
                    )
                 ),
              ResponseDTO.class
           );
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
           .body(
              noteService.search(page - 1, size, title, tags)
                 .collectList().map(minimalNoteDTOS ->
                    new ResponseDTO<>(
                       ResponseConstants.SUCCESS,
                       HttpStatus.OK,
                       minimalNoteDTOS
                    )
                 ),
              ResponseDTO.class
           );
    }

    public Mono<ServerResponse> toggleFixNote(ServerRequest request){
        String id = request.pathVariable("id");
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
           .body(
              noteService.toggleFixNote(id)
                 .map(detailedNoteDTO ->
                    new ResponseDTO<>(
                       ResponseConstants.SUCCESS,
                       HttpStatus.OK,
                       detailedNoteDTO
                    )
                 ),
              ResponseDTO.class
           );
    }

    public Mono<ServerResponse> findById(ServerRequest request){
        String id = request.pathVariable("id");
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
           .body(
              noteService.findById(id)
                 .map(detailedNoteDTO ->
                    new ResponseDTO<>(
                       ResponseConstants.SUCCESS,
                       HttpStatus.OK,
                       detailedNoteDTO
                    )
                 ),
              ResponseDTO.class
           );
    }

    public Mono<ServerResponse> create(ServerRequest request){
        Mono<CreateNoteDTO> createNoteDTOMono = request.bodyToMono(CreateNoteDTO.class).doOnNext(validator::validate);
        return createNoteDTOMono.flatMap(createNoteDTO ->
           ServerResponse.created(URI.create("/api/note"))
              .contentType(MediaType.APPLICATION_JSON)
              .body(
                 noteService.create(createNoteDTO)
                    .map(detailedNoteDTO ->
                       new ResponseDTO<>(
                          ResponseConstants.NOTE_CREATED,
                          HttpStatus.CREATED,
                          detailedNoteDTO
                       )
                    ),
                 ResponseDTO.class
              )
        );
    }

    public Mono<ServerResponse> update(ServerRequest request){
        Mono<UpdateNoteDTO> updateNoteDTOMono = request.bodyToMono(UpdateNoteDTO.class).doOnNext(validator::validate);
        return updateNoteDTOMono.flatMap(updateNoteDTO ->
           ServerResponse.ok()
              .contentType(MediaType.APPLICATION_JSON)
              .body(
                 noteService.update(updateNoteDTO)
                    .map(detailedNoteDTO ->
                       new ResponseDTO<>(
                          ResponseConstants.NOTE_UPDATED,
                          HttpStatus.OK,
                          detailedNoteDTO
                       )
                    ),
                 ResponseDTO.class
              )
        );
    }

    public Mono<ServerResponse> toggleFavoriteById(ServerRequest request){
        String id = request.pathVariable("id");
        return ServerResponse.ok()
           .contentType(MediaType.APPLICATION_JSON)
           .body(
              noteService.toggleFavorite(id)
                 .map(detailedNoteDTO ->
                    new ResponseDTO<>(
                       detailedNoteDTO.favorite()
                          ? ResponseConstants.NOTE_FAVORITE_ADDED
                          : ResponseConstants.NOTE_FAVORITE_REMOVED,
                       HttpStatus.CREATED,
                       detailedNoteDTO
                    )
                 ),
              ResponseDTO.class
           );
    }

    public Mono<ServerResponse> toggleFavoriteByIds(ServerRequest request){
        List<String> ids = Arrays.asList(request.queryParams().getFirst("ids").split(","));
        return ServerResponse.ok()
           .contentType(MediaType.APPLICATION_JSON)
           .body(
              noteService.toggleFavorite(ids)
                 .map(detailedNoteDTO ->
                    new ResponseDTO<>(
                       ResponseConstants.NOTES_FAVORITE_UPDATED,
                       HttpStatus.OK,
                       detailedNoteDTO
                    )
                 ),
              ResponseDTO.class
           );
    }

    public Mono<ServerResponse> logicalDeleteByIds(ServerRequest request){
        List<String> ids = Arrays.asList(request.queryParams().getFirst("ids").split(","));
        return ServerResponse.ok()
           .contentType(MediaType.APPLICATION_JSON)
           .body(
              noteService.logicalDeleteByIds(ids)
                 .collectList().map(detailedNoteDTOS ->
                    new ResponseDTO<>(
                       ResponseConstants.NOTES_TRASH_ADDED,
                       HttpStatus.OK,
                       detailedNoteDTOS
                    )
                 ),
              ResponseDTO.class
           );
    }

    public Mono<ServerResponse> logicalDeleteById(ServerRequest request){
        String id = request.pathVariable("id");
        return ServerResponse.ok()
           .contentType(MediaType.APPLICATION_JSON)
           .body(
              noteService.logicalDeleteById(id)
                 .map(detailedNoteDTO ->
                    new ResponseDTO<>(
                       ResponseConstants.NOTE_TRASH_ADDED,
                       HttpStatus.OK,
                       detailedNoteDTO
                    )
                 ),
              ResponseDTO.class
           );
    }

    public Mono<ServerResponse> physicalDeleteByIds(ServerRequest request){
        List<String> ids = Arrays.asList(request.queryParams().getFirst("ids").split(","));
        return ServerResponse.ok()
           .contentType(MediaType.APPLICATION_JSON)
           .body(
              noteService.physicalDeleteByIds(ids)
                 .map(msg ->
                    new ResponseDTO<>(
                       ResponseConstants.NOTES_DELETED,
                       HttpStatus.OK,
                       msg
                    )
                 ),
              ResponseDTO.class
           );
    }

    public Mono<ServerResponse> physicalDeleteById(ServerRequest request){
        String id = request.pathVariable("id");
        return ServerResponse.ok()
           .contentType(MediaType.APPLICATION_JSON)
           .body(
              noteService.physicalDeleteById(id).map(msg ->
                 new ResponseDTO<>(
                    ResponseConstants.NOTE_DELETED,
                    HttpStatus.OK,
                    msg
                 )
              ),
              ResponseDTO.class
           );
    }

    public Mono<ServerResponse> restoreById(ServerRequest request){
        String id = request.pathVariable("id");
        return ServerResponse.ok()
           .contentType(MediaType.APPLICATION_JSON)
           .body(
              noteService.restoreById(id)
                 .map(detailedNoteDTO ->
                    new ResponseDTO<>(
                       ResponseConstants.NOTE_RESTORED,
                       HttpStatus.OK,
                       detailedNoteDTO
                    )
                 ),
              ResponseDTO.class
           );
    }

    public Mono<ServerResponse> restoreByIds(ServerRequest request){
        List<String> ids = Arrays.asList(request.queryParams().getFirst("ids").split(","));
        return ServerResponse.ok()
           .contentType(MediaType.APPLICATION_JSON)
           .body(
              noteService.restoreByIds(ids)
                 .collectList().map(detailedNoteDTOS ->
                    new ResponseDTO<>(
                       ResponseConstants.NOTES_RESTORED,
                       HttpStatus.OK,
                       detailedNoteDTOS
                    )
                 ),
              ResponseDTO.class
           );
    }
}
