package com.yourcompany.menus.application.port.in;

import com.yourcompany.menus.domain.entity.Menu;

public interface IManageMenusUseCase {
    Menu getMenuById(Integer id);

    Menu updateMenu(Integer id, String nom, Integer createurId);

    void deleteMenu(Integer id);

    Menu addPlatToMenu(Integer id, Integer platId);

    Menu removePlatFromMenu(Integer id, Integer platId);
}

