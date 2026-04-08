package com.yourcompany.menus.adapter.in.rest;

import com.yourcompany.menus.domain.entity.Plat;

import java.math.BigDecimal;

public class PlatDto {
    private Integer id;
    private String nom;
    private BigDecimal prix;

    public static PlatDto fromDomain(Plat plat) {
        PlatDto dto = new PlatDto();
        dto.setId(plat.getId());
        dto.setNom(plat.getNom());
        dto.setPrix(plat.getPrix());
        return dto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }
}

