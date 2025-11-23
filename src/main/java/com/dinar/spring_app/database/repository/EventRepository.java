package com.dinar.spring_app.database.repository;

import com.dinar.spring_app.database.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByUserId(Long userId);

    List<Event> findAllByFileId(Long fileId);
}
