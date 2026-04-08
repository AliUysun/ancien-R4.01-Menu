package com.yourcompany.menus.adapter.out.service;

import com.yourcompany.menus.application.port.out.IPlatService;
import com.yourcompany.menus.domain.entity.Plat;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class PlatServiceRestClient implements IPlatService {

    private final Map<Integer, Plat> catalogue = new HashMap<>();

    public PlatServiceRestClient() {
        catalogue.put(1, new Plat(1, "Salade nicoise", new BigDecimal("8.50")));
        catalogue.put(2, new Plat(2, "Aioli provencal", new BigDecimal("12.00")));
        catalogue.put(3, new Plat(3, "Gratin dauphinois", new BigDecimal("9.00")));
        catalogue.put(4, new Plat(4, "Bouillabaisse", new BigDecimal("15.50")));
        catalogue.put(5, new Plat(5, "Tian de legumes", new BigDecimal("7.50")));
        catalogue.put(6, new Plat(6, "Poulet roti", new BigDecimal("11.00")));
        catalogue.put(7, new Plat(7, "Mousse au chocolat", new BigDecimal("5.00")));
        catalogue.put(8, new Plat(8, "Tarte tropezienne", new BigDecimal("5.50")));
    }

    @Override
    public Plat getPlatById(Integer id) {
        Plat plat = catalogue.get(id);
        if (plat == null) {
            throw new IllegalArgumentException("Plat inconnu: " + id);
        }
        return plat;
    }
}

