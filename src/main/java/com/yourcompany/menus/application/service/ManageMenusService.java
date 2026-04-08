package com.yourcompany.menus.application.service;

import com.yourcompany.menus.adapter.out.persistence.MenuRepositoryType;
import com.yourcompany.menus.application.port.in.IManageMenusUseCase;
import com.yourcompany.menus.application.port.out.IMenuRepository;
import com.yourcompany.menus.application.port.out.IPlatClient;
import com.yourcompany.menus.domain.entity.Menu;
import com.yourcompany.menus.domain.entity.Plat;
import com.yourcompany.menus.domain.service.IMenuMetier;
import com.yourcompany.menus.domain.service.MenuMetier;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

@ApplicationScoped
public class ManageMenusService implements IManageMenusUseCase {

    private static final Map<Integer, String> CREATEURS = Map.of(
            1, "Dupont",
            2, "Martin",
            3, "Bernard"
    );

    private final IMenuRepository menuRepository;
    private final IPlatClient platClient;
    private final IMenuMetier menuMetier;

    @Inject
    public ManageMenusService(@MenuRepositoryType IMenuRepository menuRepository, IPlatClient platClient, IMenuMetier menuMetier) {
        this.menuRepository = menuRepository;
        this.platClient = platClient;
        this.menuMetier = menuMetier;
    }

    public ManageMenusService(IMenuRepository menuRepository, IPlatClient platClient) {
        this(menuRepository, platClient, new MenuMetier());
    }

    @Override
    public Menu getMenuById(Integer id) {
        return menuRepository.findById(id)
                .map(this::refreshPrixTotal)
                .orElseThrow(() -> new IllegalArgumentException("Menu introuvable: " + id));
    }

    @Override
    public Menu updateMenu(Integer id, String nom, Integer createurId) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu introuvable: " + id));
        menu.setNom(nom);
        menu.setCreateurId(createurId);
        menu.setCreateurNom(resolveCreateurNom(createurId));
        menu.setDateMiseAJour(LocalDate.now());
        menuMetier.validerMenu(menu);
        refreshPrixTotal(menu);
        return menuRepository.save(menu);
    }

    @Override
    public void deleteMenu(Integer id) {
        if (menuRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Menu introuvable: " + id);
        }
        menuRepository.deleteById(id);
    }

    @Override
    public Menu addPlatToMenu(Integer id, Integer platId) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu introuvable: " + id));

        boolean dejaPresent = menu.getPlats().stream().anyMatch(plat -> plat.getId().equals(platId));
        if (dejaPresent) {
            throw new IllegalStateException("Plat deja present dans ce menu: " + platId);
        }

        Plat plat = platClient.getPlatById(platId);
        var plats = new ArrayList<>(menu.getPlats());
        plats.add(plat);
        menu.setPlats(plats);
        menu.setDateMiseAJour(LocalDate.now());
        return menuRepository.save(refreshPrixTotal(menu));
    }

    @Override
    public Menu removePlatFromMenu(Integer id, Integer platId) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu introuvable: " + id));

        var plats = new ArrayList<>(menu.getPlats());
        boolean removed = plats.removeIf(plat -> plat.getId().equals(platId));
        if (!removed) {
            throw new IllegalArgumentException("Plat absent du menu: " + platId);
        }

        menu.setPlats(plats);
        menu.setDateMiseAJour(LocalDate.now());
        return menuRepository.save(refreshPrixTotal(menu));
    }

    private String resolveCreateurNom(Integer createurId) {
        if (createurId == null) {
            throw new IllegalArgumentException("Le createurId est obligatoire");
        }
        String createurNom = CREATEURS.get(createurId);
        if (createurNom == null) {
            throw new IllegalArgumentException("Createur introuvable: " + createurId);
        }
        return createurNom;
    }

    private Menu refreshPrixTotal(Menu menu) {
        menu.setPrixTotal(menuMetier.calculerPrixTotal(menu));
        return menu;
    }
}
