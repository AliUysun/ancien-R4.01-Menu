package com.yourcompany.menus.adapter.in.rest;

import com.yourcompany.menus.domain.entity.Menu;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class MenuDto {
    private Integer id;
    private String nom;
    private Integer createurId;
    private String createurNom;
    private LocalDate dateCreation;
    private LocalDate dateMiseAJour;
    private List<PlatDto> plats;
    private BigDecimal prixTotal;

    public static MenuDto fromDomain(Menu menu) {
        MenuDto dto = new MenuDto();
        dto.setId(menu.getId());
        dto.setNom(menu.getNom());
        dto.setCreateurId(menu.getCreateurId());
        dto.setCreateurNom(menu.getCreateurNom());
        dto.setDateCreation(menu.getDateCreation());
        dto.setDateMiseAJour(menu.getDateMiseAJour());
        dto.setPlats(menu.getPlats().stream().map(PlatDto::fromDomain).toList());
        dto.setPrixTotal(menu.getPrixTotal());
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

    public Integer getCreateurId() {
        return createurId;
    }

    public void setCreateurId(Integer createurId) {
        this.createurId = createurId;
    }

    public String getCreateurNom() {
        return createurNom;
    }

    public void setCreateurNom(String createurNom) {
        this.createurNom = createurNom;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDate getDateMiseAJour() {
        return dateMiseAJour;
    }

    public void setDateMiseAJour(LocalDate dateMiseAJour) {
        this.dateMiseAJour = dateMiseAJour;
    }

    public List<PlatDto> getPlats() {
        return plats;
    }

    public void setPlats(List<PlatDto> plats) {
        this.plats = plats;
    }

    public BigDecimal getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(BigDecimal prixTotal) {
        this.prixTotal = prixTotal;
    }
}

