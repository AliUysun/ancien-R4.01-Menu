package com.yourcompany.menus.application.port.in;

import com.yourcompany.menus.domain.entity.Menu;

import java.util.List;

public interface IListMenusUseCase {
    List<Menu> listMenus();
}

