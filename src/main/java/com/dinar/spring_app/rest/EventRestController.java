package com.dinar.spring_app.rest;

import com.dinar.spring_app.database.entity.Event;
import com.dinar.spring_app.database.entity.User;
import com.dinar.spring_app.dto.EventDto;
import com.dinar.spring_app.security.annotation.IsAdmin;
import com.dinar.spring_app.security.annotation.IsModerator;
import com.dinar.spring_app.security.annotation.IsUser;
import com.dinar.spring_app.service.EventService;
import com.dinar.spring_app.service.UserService;
import com.dinar.spring_app.utill.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@Tag(name = "Events API", description = "Управление событиями файлов")
public class EventRestController {

    private final EventService eventService;
    private final UserService userService;

    public EventRestController(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    @GetMapping("/my-events")
    @IsUser
    @Operation(summary = "Получить мои события")
    public ResponseEntity<List<EventDto>> getMyEvents() {
        String username = SecurityUtils.getCurrentUsername();
        User currentUser = userService.findByUserName(username)
                                      .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var events = eventService.findAllByUserId(currentUser.getId());
        return events.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(events.stream()
                                              .map(EventDto::from)
                                              .toList());
    }

    @GetMapping(value = "/id/{id}")
    @IsModerator
    @Operation(summary = "Получить события по Id события")
    public ResponseEntity<EventDto> getEventById(@PathVariable("id") Long eventId) {
        var event = eventService.findById(eventId)
                              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(EventDto.from(event));
    }

    @GetMapping
    @IsModerator
    @Operation(summary = "Получить все события")
    public ResponseEntity<List<EventDto>> getAll() {
        var events = eventService.findAll();
        return events.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(events.stream()
                                              .map(EventDto::from)
                                              .toList());
    }

    @GetMapping("/users/{userId}")
    @IsModerator
    @Operation(summary = "Получить события по Id пользователя")
    public ResponseEntity<List<EventDto>> getEventByUserId(@PathVariable Long userId) {
        var events = eventService.findAllByUserId(userId);
        return events.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(events.stream()
                                              .map(EventDto::from)
                                              .toList());
    }

    @GetMapping("/files/{fileId}")
    @IsModerator
    @Operation(summary = "Получить события по Id файла")
    public ResponseEntity<List<EventDto>> getEventByFileId(@PathVariable Long fileId) {
        var events = eventService.findAllByFileId(fileId);
        return events.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(events.stream()
                                              .map(EventDto::from)
                                              .toList());
    }

    @DeleteMapping(value = "/id/{id}")
    @IsAdmin
    @Operation(summary = "Удалить событие по Id")
    public ResponseEntity<Void> deleteEventById(@PathVariable("id") Long eventId) {
        eventService.deleteById(eventId);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    @IsAdmin
    @Operation(summary = "Сохранить событие")
    public ResponseEntity<EventDto> save(@RequestBody Event event) {
        var event1 = eventService.create(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(EventDto.from(event1));
    }
}
