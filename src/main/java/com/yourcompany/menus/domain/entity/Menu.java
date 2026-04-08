package com.yourcompany.menus.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Menu {
    private Integer id;
    private String nom;
    private Integer createurId;
    private String createurNom;
    private LocalDate dateCreation;
    private LocalDate dateMiseAJour;
    private List<Plat> plats = new ArrayList<>();
    private BigDecimal prixTotal = BigDecimal.ZERO;

    public Menu() {
    }

    public Menu(Integer id, String nom, Integer createurId, String createurNom, LocalDate dateCreation, LocalDate dateMiseAJour, List<Plat> plats) {
        this.id = id;
        this.nom = nom;
        this.createurId = createurId;
        this.createurNom = createurNom;
        this.dateCreation = dateCreation;
        this.dateMiseAJour = dateMiseAJour;
        setPlats(plats);
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

    public List<Plat> getPlats() {
        return new ArrayList<>(plats);
    }

    public void setPlats(List<Plat> plats) {
        this.plats = plats == null ? new ArrayList<>() : new ArrayList<>(plats);
    }

    public BigDecimal getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(BigDecimal prixTotal) {
        this.prixTotal = prixTotal == null ? BigDecimal.ZERO : prixTotal;
    }
}

