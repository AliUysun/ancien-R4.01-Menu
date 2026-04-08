package com.yourcompany.menus.adapter.in.rest;

public class CreateMenuRequest {
    private String nom;
    private Integer createurId;

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
}

