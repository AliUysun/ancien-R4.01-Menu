# menus-service

Architecture simple en couches (domain / application / adapter / config) pour exposer une API REST de menus en WAR.

## Structure

- `com.yourcompany.menus.domain`: entites et logique metier (`IMenuMetier`, `MenuMetier`)
- `com.yourcompany.menus.application`: ports et services applicatifs
- `com.yourcompany.menus.adapter`: REST entrant et adaptateurs sortants (`MenuRepository`, `PlatClient`)
- `com.yourcompany.menus.config`: configuration JAX-RS

## Endpoints

Base URL (GlassFish):

- `http://localhost:8080/menus/api`

Routes:

- `GET /menus`
- `POST /menus`
- `GET /menus/{id}`
- `PUT /menus/{id}`
- `DELETE /menus/{id}`
- `PUT /menus/{id}/plats/{platId}`
- `DELETE /menus/{id}/plats/{platId}`

Exemple de body `POST /menus`:

```json
{
  "nom": "Menu du jour",
  "createurId": 2
}
```

## Configuration BDD MySQL

Le repository lit d'abord le fichier `.env` (non versionne), puis les variables d'environnement systeme.

Variables attendues:

- `MYSQL_HOST`
- `MYSQL_PORT` (optionnel, defaut `3306`)
- `MYSQL_DATABASE`
- `MYSQL_USER`
- `MYSQL_PASSWORD`
- `MYSQL_URL` (optionnel, prioritaire si defini)

Le template est dans `.env.example`.

Optionnel: pour pointer vers un autre fichier `.env`, definir `MENUS_DOTENV_PATH`.

## Scripts SQL

- Schema: `src/main/resources/db/schema.sql`
- Donnees initiales: `src/main/resources/db/seed.sql`

## Notes de migration

- Nouveau nom principal du repository: `MenuRepository`
- Nouveau nom principal du client plats: `PlatClient`
- Alias temporaires conserves pour compatibilite: `MenuRepositoryJpa`, `PlatServiceRestClient`, `IPlatService`
