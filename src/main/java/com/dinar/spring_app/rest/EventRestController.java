package com.dinar.spring_app.rest;

import com.dinar.spring_app.database.entity.Event;
import com.dinar.spring_app.security.annotation.IsAdmin;
import com.dinar.spring_app.security.annotation.IsModerator;
import com.dinar.spring_app.security.annotation.IsUser;
import com.dinar.spring_app.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.dinar.spring_app.utill.SecurityUtils.getCurrentUserFromSecurity;

@RestController
@RequestMapping("/api/v1/events")
public class EventRestController {

    private final EventService eventService;

    @Autowired
    public EventRestController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/my-events")
    @IsUser
    public ResponseEntity<List<Event>> getMyEvents() {
        var currentUser = getCurrentUserFromSecurity();
        var events = eventService.findAllByUserId(currentUser.getId());
        return events.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(events);
    }

    @GetMapping(value = "id/{id}")
    @IsModerator
    public ResponseEntity<Event> getEventById(@PathVariable("id") Long eventId) {
        var event = eventService.findById(eventId)
                              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(event);
    }

    @GetMapping
    @IsModerator
    public ResponseEntity<List<Event>> getAll() {
        var events = eventService.findAll();
        return events.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(events);
    }

    @GetMapping("/users/{userId}")
    @IsModerator
    public ResponseEntity<List<Event>> getEventByUserId(@PathVariable Long userId) {
        var eventsById = eventService.findAllByUserId(userId);
        return eventsById.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(eventsById);
    }

    @GetMapping("/files/{fileId}")
    @IsModerator
    public ResponseEntity<List<Event>> getEventByFileId(@PathVariable Long fileId) {
        var eventsById = eventService.findAllByFileId(fileId);
        return eventsById.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(eventsById);
    }

    @DeleteMapping(value = "id/{id}")
    @IsModerator
    public ResponseEntity<Void> deleteEventById(@PathVariable("id") Long eventId) {
        eventService.deleteById(eventId);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    @IsAdmin
    public ResponseEntity<Event> save(@RequestBody Event event) {
        var event1 = eventService.create(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(event1);
    }
}
