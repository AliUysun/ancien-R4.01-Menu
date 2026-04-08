package com.yourcompany.menus.application.service;

import com.yourcompany.menus.application.port.out.IMenuRepository;
import com.yourcompany.menus.domain.entity.Menu;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CreateMenuServiceTest {

    @Test
    void creeEtSauvegardeUnMenu() {
        IMenuRepository repository = new FakeMenuRepository();
        CreateMenuService service = new CreateMenuService(repository);

        Menu menu = service.createMenu("Menu test", 2);

        assertNotNull(menu.getId());
        assertEquals("Menu test", menu.getNom());
        assertEquals(Integer.valueOf(2), menu.getCreateurId());
        assertEquals("Martin", menu.getCreateurNom());
        assertEquals(BigDecimal.ZERO, menu.getPrixTotal());
        assertEquals(1, repository.findAll().size());
    }

    private static class FakeMenuRepository implements IMenuRepository {
        private final Map<Integer, Menu> db = new HashMap<>();
        private int seq = 0;

        @Override
        public Integer nextId() {
            seq += 1;
            return seq;
        }

        @Override
        public Menu save(Menu menu) {
            db.put(menu.getId(), menu);
            return menu;
        }

        @Override
        public List<Menu> findAll() {
            return new ArrayList<>(db.values());
        }

        @Override
        public Optional<Menu> findById(Integer id) {
            return Optional.ofNullable(db.get(id));
        }

        @Override
        public void deleteById(Integer id) {
            db.remove(id);
        }
    }
}

