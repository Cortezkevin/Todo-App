package com.kevin.todo_app.service;

import com.kevin.todo_app.documents.note.Element;
import com.kevin.todo_app.documents.note.Note;
import com.kevin.todo_app.dto.note.CreateNoteDTO;
import com.kevin.todo_app.dto.note.DetailedNoteDTO;
import com.kevin.todo_app.dto.note.MinimalNoteDTO;
import com.kevin.todo_app.dto.note.UpdateNoteDTO;
import com.kevin.todo_app.dto.tag.TagDTO;
import com.kevin.todo_app.exception.custom.AlreadyExistsResourceWithFieldException;
import com.kevin.todo_app.exception.custom.NotInTheTrashException;
import com.kevin.todo_app.exception.custom.ResourceNotFoundException;
import com.kevin.todo_app.helpers.AuthHelpers;
import com.kevin.todo_app.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteService {
   private final NoteRepository noteRepository;
   private final TagService tagService;
   private final ReactiveMongoTemplate mongoTemplate;

   private Mono<Note> findNoteById(String id) {
      return noteRepository.findById(id)
         .switchIfEmpty(Mono.error(new ResourceNotFoundException("Note", "Id", id)));
   }


   public Flux<MinimalNoteDTO> findAll(int page, int size) {
      Pageable pageable = PageRequest.of(page, size);

      Query query = new Query().with(pageable);
      query.addCriteria(Criteria.where("deleted").is(false));
      query.with(
         Sort.by(Sort.Order.desc("fixed"))
            .and(Sort.by(Sort.Order.desc("fixedAt")))
            .and(Sort.by(Sort.Order.desc("createdAt")))
      );

      return AuthHelpers.getCurrentUser()
         .map(currentUser -> query.addCriteria(Criteria.where("user").is(currentUser)))
         .flatMapMany(q -> mongoTemplate.find(q, Note.class))
         .map(MinimalNoteDTO::toDTO);
   }

   public Mono<DetailedNoteDTO> findById(String id) {
      return this.findNoteById(id)
         .map(DetailedNoteDTO::toDTO);
   }

   public Flux<MinimalNoteDTO> search(int page, int size, String title, List<String> tags) {
      Pageable pageable = PageRequest.of(page, size);

      Query query = new Query();
      query.with(pageable).with(Sort.by(Sort.Direction.ASC, "createdAt"));
      query.addCriteria(Criteria.where("deleted").is(false));

      if (title != null) {
         query.addCriteria(Criteria.where("title").regex(title, "i"));
      }
      if (tags != null && !tags.isEmpty()) {
         query.addCriteria(Criteria.where("tags").in(tags));
      }

      return AuthHelpers.getCurrentUser()
         .map(currentUser -> query.addCriteria(Criteria.where("user").is(currentUser)))
         .flatMapMany(q -> mongoTemplate.find(q, Note.class))
         .map(MinimalNoteDTO::toDTO);
   }

   public Mono<DetailedNoteDTO> toggleFixNote(String id) {
      return this.findNoteById(id)
         .flatMap(note -> {
            if (note.isFixed()) {
               note.setFixed(false);
               note.setFixedAt(null);
            } else {
               note.setFixed(true);
               note.setFixedAt(LocalDateTime.now());
            }
            return noteRepository.save(note);
         }).map(DetailedNoteDTO::toDTO);
   }

   public Mono<DetailedNoteDTO> create(CreateNoteDTO createNoteDTO) {
      return AuthHelpers.getCurrentUser()
         .flatMap(user ->
            noteRepository.findByTitleAndUser(createNoteDTO.title(), user)
               .flatMap(note -> Mono.error(new AlreadyExistsResourceWithFieldException("Note", "Title", createNoteDTO.title())))
               .switchIfEmpty(
                  tagService.findAllOrCreateByName(createNoteDTO.tags())
                     .collectList()
                     .flatMap(tags ->
                        noteRepository.save(
                           Note.builder()
                              .title(createNoteDTO.title())
                              .content(createNoteDTO.content())
                              .createdAt(LocalDateTime.now())
                              .color(createNoteDTO.color())
                              .tags(tags.stream().map(TagDTO::name).toList())
                              .user(user)
                              .build()
                        )
                     )
               )
         )
         .map(note -> DetailedNoteDTO.toDTO((Note) note));
   }

   public Mono<DetailedNoteDTO> update(UpdateNoteDTO updateNoteDTO) {
      return AuthHelpers.getCurrentUser()
         .flatMap(user ->
            this.findNoteById(updateNoteDTO.id())
               .flatMap(note ->
                  noteRepository.existsByTitleAndUser(updateNoteDTO.title(), user)
                     .flatMap(existsTitle -> {
                        if (existsTitle && !note.getTitle().equals(updateNoteDTO))
                           return Mono.error(new AlreadyExistsResourceWithFieldException("Note", "Title", updateNoteDTO.title()));

                        List<Element> prevElements = note.getContent();
                        List<Element> updatedElements = new ArrayList<>();

                        Map<Integer, Element> prevElementsMap = prevElements.stream()
                           .collect(Collectors.toMap(Element::getIndex, e -> e));

                        for (Element updatedElement : updateNoteDTO.content()) {
                           if (updatedElement.isDelete()) {
                              prevElementsMap.remove(updatedElement.getIndex());
                              continue;
                           }

                           if (prevElementsMap.containsKey(updatedElement.getIndex())) {
                              updatedElements.add(updatedElement);
                              prevElementsMap.remove(updatedElement.getIndex());
                           } else {
                              updatedElements.add(updatedElement);
                           }
                        }

                        updatedElements.addAll(prevElementsMap.values());
                        updatedElements.sort(Comparator.comparing(Element::getIndex));

                        for (int i = 0; i < updatedElements.size(); i++) {
                           Element element = updatedElements.get(i);
                           element.setIndex(Integer.valueOf(String.valueOf(i)));
                        }

                        note.setTitle(updateNoteDTO.title());
                        note.setUpdatedAt(LocalDateTime.now());
                        note.setContent(updatedElements);
                        note.setColor(updateNoteDTO.color());

                        return tagService.findAllOrCreateByName(updateNoteDTO.tags())
                           .collectList()
                           .flatMap(updatedTags -> {
                              note.setTags(updatedTags.stream().map(TagDTO::name).toList());
                              return noteRepository.save(note);
                           });
                     })
               )
         )
         .map(DetailedNoteDTO::toDTO);
   }

   public Mono<DetailedNoteDTO> toggleFavorite(String id) {
      return this.findNoteById(id)
         .flatMap(note -> {
            note.setFavorite(!note.isFavorite());
            return noteRepository.save(note);
         })
         .map(DetailedNoteDTO::toDTO);
   }

   public Flux<DetailedNoteDTO> toggleFavorite(List<String> ids) {
      return noteRepository.findAllById(ids)
         .map(note -> {
            note.setFavorite(true);
            return note;
         })
         .collectList()
         .flatMapMany(noteRepository::saveAll)
         .map(DetailedNoteDTO::toDTO);
   }

   public Mono<DetailedNoteDTO> restoreById(String id) {
      return this.findNoteById(id)
         .flatMap(note -> {
            note.setDeleted(false);
            note.setDeletedAt(null);
            return noteRepository.save(note);
         })
         .map(DetailedNoteDTO::toDTO);
   }

   public Flux<DetailedNoteDTO> restoreByIds(List<String> ids) {
      return noteRepository.findAllById(ids)
         .map(note -> {
            note.setDeleted(false);
            note.setDeletedAt(null);
            return note;
         })
         .collectList()
         .flatMapMany(noteRepository::saveAll)
         .map(DetailedNoteDTO::toDTO);
   }

   public Flux<DetailedNoteDTO> logicalDeleteByIds(List<String> ids) {
      return noteRepository.findAllById(ids)
         .map(note -> {
            note.setDeleted(true);
            note.setDeletedAt(LocalDateTime.now());
            return note;
         })
         .collectList()
         .flatMapMany(noteRepository::saveAll)
         .map(DetailedNoteDTO::toDTO);
   }

   public Mono<DetailedNoteDTO> logicalDeleteById(String id) {
      return this.findNoteById(id)
         .flatMap(note -> {
            note.setDeleted(true);
            note.setDeletedAt(LocalDateTime.now());
            return noteRepository.save(note);
         })
         .map(DetailedNoteDTO::toDTO);
   }

   public Mono<String> physicalDeleteByIds(List<String> ids) {
      return noteRepository.findAllById(ids)
         .filter(Note::isDeleted)
         .collectList()
         .flatMap(noteRepository::deleteAll)
         .then(Mono.just("Notas eliminadas totalmente."));
   }

   public Mono<String> physicalDeleteById(String id) {
      return this.findNoteById(id)
         .flatMap(note -> {
            if (note.isDeleted()) {
               return noteRepository.deleteById(id);
            } else {
               return Mono.error(new NotInTheTrashException());
            }
         })
         .then(Mono.just("Nota eliminada totalmente."));
   }

   public Flux<MinimalNoteDTO> findAllFavorites(int page, int size) {
      Pageable pageable = PageRequest.of(page, size);

      Query query = new Query().with(pageable);
      query.addCriteria(Criteria.where("favorite").is(true));
      query.with(Sort.by(Sort.Order.desc("createdAt")));

      return AuthHelpers.getCurrentUser()
         .map(currentUser -> query.addCriteria(Criteria.where("user").is(currentUser)))
         .flatMapMany(q -> mongoTemplate.find(q, Note.class))
         .map(MinimalNoteDTO::toDTO);
   }

   public Flux<MinimalNoteDTO> findAllDeleted(int page, int size) {
      Pageable pageable = PageRequest.of(page, size);

      Query query = new Query().with(pageable);
      query.addCriteria(Criteria.where("deleted").is(true));
      query.with(Sort.by(Sort.Order.desc("createdAt")));

      return AuthHelpers.getCurrentUser()
         .map(currentUser -> query.addCriteria(Criteria.where("user").is(currentUser)))
         .flatMapMany(q -> mongoTemplate.find(q, Note.class))
         .map(MinimalNoteDTO::toDTO);
   }

}
