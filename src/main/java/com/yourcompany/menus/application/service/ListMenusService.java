package com.yourcompany.menus.application.service;

import com.yourcompany.menus.adapter.out.persistence.MenuRepositoryType;
import com.yourcompany.menus.application.port.in.IListMenusUseCase;
import com.yourcompany.menus.application.port.out.IMenuRepository;
import com.yourcompany.menus.domain.entity.Menu;
import com.yourcompany.menus.domain.service.IMenuMetier;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ListMenusService implements IListMenusUseCase {

    private final IMenuRepository menuRepository;
    private final IMenuMetier menuMetier;

    @Inject
    public ListMenusService(@MenuRepositoryType IMenuRepository menuRepository, IMenuMetier menuMetier) {
        this.menuRepository = menuRepository;
        this.menuMetier = menuMetier;
    }

    @Override
    public List<Menu> listMenus() {
        List<Menu> menus = menuRepository.findAll();
        for (Menu menu : menus) {
            menu.setPrixTotal(menuMetier.calculerPrixTotal(menu));
        }
        return menus;
    }
}
