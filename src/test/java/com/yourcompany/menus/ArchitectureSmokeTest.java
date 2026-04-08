package com.yourcompany.menus;

import com.yourcompany.menus.adapter.out.persistence.MenuRepositoryJpa;
import com.yourcompany.menus.application.service.CreateMenuService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ArchitectureSmokeTest {

    @Test
    void composantsPrincipauxSontInstanciables() {
        assertDoesNotThrow(() -> {
            MenuRepositoryJpa repository = new MenuRepositoryJpa();
            new CreateMenuService(repository);
        });
    }
}

