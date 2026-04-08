package com.yourcompany.menus.adapter.in.rest;

import com.yourcompany.menus.application.port.in.ICreateMenuUseCase;
import com.yourcompany.menus.application.port.in.IListMenusUseCase;
import com.yourcompany.menus.application.port.in.IManageMenusUseCase;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/menus")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class MenuController {

    @Inject
    ICreateMenuUseCase createMenuUseCase;

    @Inject
    IListMenusUseCase listMenusUseCase;

    @Inject
    IManageMenusUseCase manageMenusUseCase;

    @GET
    public List<MenuDto> listMenus() {
        return listMenusUseCase.listMenus().stream()
                .map(MenuDto::fromDomain)
                .toList();
    }

    @POST
    public Response createMenu(CreateMenuRequest request) {
        try {
            MenuDto created = MenuDto.fromDomain(
                    createMenuUseCase.createMenu(request.getNom(), request.getCreateurId())
            );
            return Response.status(Response.Status.CREATED)
                    .header("Location", "/menus/" + created.getId())
                    .entity(created)
                    .build();
        } catch (IllegalArgumentException e) {
            throw toHttpException(e);
        }
    }

    @GET
    @Path("/{id}")
    public MenuDto getMenuById(@PathParam("id") Integer id) {
        try {
            return MenuDto.fromDomain(manageMenusUseCase.getMenuById(id));
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @PUT
    @Path("/{id}")
    public MenuDto updateMenu(@PathParam("id") Integer id, CreateMenuRequest request) {
        try {
            return MenuDto.fromDomain(manageMenusUseCase.updateMenu(id, request.getNom(), request.getCreateurId()));
        } catch (IllegalArgumentException e) {
            throw toHttpException(e);
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteMenu(@PathParam("id") Integer id) {
        try {
            manageMenusUseCase.deleteMenu(id);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @PUT
    @Path("/{id}/plats/{platId}")
    public MenuDto addPlatToMenu(@PathParam("id") Integer id, @PathParam("platId") Integer platId) {
        try {
            return MenuDto.fromDomain(manageMenusUseCase.addPlatToMenu(id, platId));
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(e.getMessage());
        } catch (IllegalStateException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.CONFLICT);
        }
    }

    @DELETE
    @Path("/{id}/plats/{platId}")
    public MenuDto removePlatFromMenu(@PathParam("id") Integer id, @PathParam("platId") Integer platId) {
        try {
            return MenuDto.fromDomain(manageMenusUseCase.removePlatFromMenu(id, platId));
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    private WebApplicationException toHttpException(IllegalArgumentException e) {
        String message = e.getMessage() == null ? "Requete invalide" : e.getMessage();
        if (message.contains("introuvable")) {
            return new NotFoundException(message);
        }
        return new WebApplicationException(message, Response.Status.BAD_REQUEST);
    }
}

