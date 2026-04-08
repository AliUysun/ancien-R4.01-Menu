package com.yourcompany.menus.domain.service;

import com.yourcompany.menus.domain.entity.Menu;
import com.yourcompany.menus.domain.entity.Plat;

import java.math.BigDecimal;

public class MenuMetier {

    public void validerMenu(Menu menu) {
        if (menu == null) {
            throw new IllegalArgumentException("Le menu est obligatoire");
        }
        if (menu.getNom() == null || menu.getNom().isBlank()) {
            throw new IllegalArgumentException("Le nom du menu est obligatoire");
        }
        for (Plat plat : menu.getPlats()) {
            if (plat.getPrix() == null || plat.getPrix().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Le prix d'un plat est invalide");
            }
        }
    }

    public BigDecimal calculerPrixTotal(Menu menu) {
        return menu.getPlats().stream()
                .map(Plat::getPrix)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

