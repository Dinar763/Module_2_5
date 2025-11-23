package com.dinar.spring_app.service;

import java.util.List;
import java.util.Optional;

public interface GenericService <T> {

    Optional<T> findById(Long id);
    List<T> findAll();
    void deleteById(Long id);
    boolean existById(Long id);
}
