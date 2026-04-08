package com.yourcompany.menus.application.port.in;

import com.yourcompany.menus.domain.entity.Menu;

public interface ICreateMenuUseCase {
    Menu createMenu(String nom, Integer createurId);
}

