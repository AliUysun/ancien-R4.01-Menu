# menus-service

Architecture hexagonale (domain / application / adapter / config) pour exposer un service REST de menus.

## Structure

- `com.yourcompany.menus.domain`: entites et logique metier
- `com.yourcompany.menus.application`: ports et use cases
- `com.yourcompany.menus.adapter`: REST entrant + persistance/service sortants
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

## Build / test

```bash
./mvnw clean test
./mvnw clean package
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

Exemple:

```bash
cp .env.example .env
```

Optionnel: pour pointer vers un autre fichier `.env`, definir `MENUS_DOTENV_PATH`.

