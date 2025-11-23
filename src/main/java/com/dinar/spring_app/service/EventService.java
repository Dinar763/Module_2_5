package com.dinar.spring_app.service;

import com.dinar.spring_app.database.entity.Event;

import java.util.List;

public interface EventService extends GenericService <Event> {

    List<Event> findAllByUserId(Long userId);
    List<Event> findAllByFileId(Long fileId);
    Event create(Event event);
}
