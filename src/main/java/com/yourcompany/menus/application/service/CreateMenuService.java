package com.yourcompany.menus.application.service;

import com.yourcompany.menus.application.port.in.ICreateMenuUseCase;
import com.yourcompany.menus.application.port.out.IMenuRepository;
import com.yourcompany.menus.domain.entity.Menu;
import com.yourcompany.menus.domain.service.MenuMetier;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.Map;

@ApplicationScoped
public class CreateMenuService implements ICreateMenuUseCase {

    private static final Map<Integer, String> CREATEURS = Map.of(
            1, "Dupont",
            2, "Martin",
            3, "Bernard"
    );

    private final IMenuRepository menuRepository;
    private final MenuMetier menuMetier;

    @Inject
    public CreateMenuService(IMenuRepository menuRepository) {
        this.menuRepository = menuRepository;
        this.menuMetier = new MenuMetier();
    }

    @Override
    public Menu createMenu(String nom, Integer createurId) {
        String createurNom = resolveCreateurNom(createurId);
        LocalDate now = LocalDate.now();
        Menu menu = new Menu(menuRepository.nextId(), nom, createurId, createurNom, now, now, null);
        menuMetier.validerMenu(menu);
        menu.setPrixTotal(menuMetier.calculerPrixTotal(menu));
        return menuRepository.save(menu);
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
}

