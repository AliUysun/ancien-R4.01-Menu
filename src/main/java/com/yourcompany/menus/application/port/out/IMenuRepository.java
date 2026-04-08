package com.yourcompany.menus.application.port.out;

import com.yourcompany.menus.domain.entity.Menu;

import java.util.List;
import java.util.Optional;

public interface IMenuRepository {
    Integer nextId();

    Menu save(Menu menu);

    List<Menu> findAll();

    Optional<Menu> findById(Integer id);

    void deleteById(Integer id);
}

