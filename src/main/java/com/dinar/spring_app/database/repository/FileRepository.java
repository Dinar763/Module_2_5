package com.dinar.spring_app.database.repository;

import com.dinar.spring_app.database.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository <File, Long> {

    Optional<File> findByLocation(String location);

    Optional<File> findByName(String fileName);

    boolean existsByName(String fileName);

    void deleteByName(String fileName);

    List<File> findAllByUserId(Long userId);
}
