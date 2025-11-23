package com.dinar.spring_app.service.impl;

import com.dinar.spring_app.database.entity.Event;
import com.dinar.spring_app.database.entity.File;
import com.dinar.spring_app.database.repository.EventRepository;
import com.dinar.spring_app.database.repository.FileRepository;
import com.dinar.spring_app.database.repository.UserRepository;
import com.dinar.spring_app.exception.ServiceException;
import com.dinar.spring_app.service.EventService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, FileRepository fileRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Event> findAllByUserId(Long userId) {
        return eventRepository.findAllByUserId(userId);
    }

    @Override
    public List<Event> findAllByFileId(Long fileId) {
        return eventRepository.findAllByFileId(fileId);
    }

    @Override
    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }

    @Override
    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        eventRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Event create(Event event) {
        if (!userRepository.existsById(event.getUser().getId())) {
            throw new ServiceException("User not found with id: " + event.getUser().getId());
        }

        if (!fileRepository.existsById(event.getFile().getId())) {
            throw new ServiceException("File not found with id: " + event.getFile().getId());
        }

        File file = fileRepository.findById(event.getFile().getId())
                                  .orElseThrow(() -> new ServiceException("File not found"));

        if (!file.getUser().getId().equals(event.getUser().getId())) {
            throw new ServiceException("File does not belong to user");
        }

        return eventRepository.save(event);
    }

    @Override
    public boolean existById(Long id) {
        return eventRepository.existsById(id);
    }
}
