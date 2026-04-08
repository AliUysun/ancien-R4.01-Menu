package com.yourcompany.menus.adapter.out.persistence;

import com.yourcompany.menus.application.port.out.IMenuRepository;
import com.yourcompany.menus.domain.entity.Menu;
import com.yourcompany.menus.domain.entity.Plat;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
@MenuRepositoryType
public class MenuRepository implements IMenuRepository {

    private static final String SELECT_NEXT_ID = "SELECT COALESCE(MAX(id), 0) + 1 AS next_id FROM menus";
    private static final String SELECT_MENUS = "SELECT id, nom, createur_id, createur_nom, date_creation, date_mise_a_jour, prix_total FROM menus ORDER BY id";
    private static final String SELECT_MENU_BY_ID = "SELECT id, nom, createur_id, createur_nom, date_creation, date_mise_a_jour, prix_total FROM menus WHERE id = ?";
    private static final String SELECT_PLATS_BY_MENU = "SELECT plat_id, plat_nom, plat_prix FROM menu_plats WHERE menu_id = ? ORDER BY plat_id";
    private static final String UPSERT_MENU = "INSERT INTO menus (id, nom, createur_id, createur_nom, date_creation, date_mise_a_jour, prix_total) VALUES (?, ?, ?, ?, ?, ?, ?) "
            + "ON DUPLICATE KEY UPDATE nom = VALUES(nom), createur_id = VALUES(createur_id), createur_nom = VALUES(createur_nom), "
            + "date_creation = VALUES(date_creation), date_mise_a_jour = VALUES(date_mise_a_jour), prix_total = VALUES(prix_total)";
    private static final String DELETE_MENU_PLATS = "DELETE FROM menu_plats WHERE menu_id = ?";
    private static final String INSERT_MENU_PLAT = "INSERT INTO menu_plats (menu_id, plat_id, plat_nom, plat_prix) VALUES (?, ?, ?, ?)";
    private static final String DELETE_MENU = "DELETE FROM menus WHERE id = ?";

    private volatile DbSettings settings;

    @Override
    public Integer nextId() {
        try (Connection connection = openConnection();
             PreparedStatement stmt = connection.prepareStatement(SELECT_NEXT_ID);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("next_id");
            }
            return 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de calculer le prochain id de menu", e);
        }
    }

    @Override
    public Menu save(Menu menu) {
        try (Connection connection = openConnection()) {
            connection.setAutoCommit(false);
            try {
                saveMenu(connection, menu);
                replaceMenuPlats(connection, menu);
                connection.commit();
                return menu;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de sauvegarder le menu", e);
        }
    }

    @Override
    public List<Menu> findAll() {
        try (Connection connection = openConnection();
             PreparedStatement stmt = connection.prepareStatement(SELECT_MENUS);
             ResultSet rs = stmt.executeQuery()) {
            List<Menu> menus = new ArrayList<>();
            while (rs.next()) {
                Menu menu = mapMenu(rs);
                menu.setPlats(loadPlatsByMenu(connection, menu.getId()));
                menus.add(menu);
            }
            return menus;
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de recuperer les menus", e);
        }
    }

    @Override
    public Optional<Menu> findById(Integer id) {
        try (Connection connection = openConnection();
             PreparedStatement stmt = connection.prepareStatement(SELECT_MENU_BY_ID)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                Menu menu = mapMenu(rs);
                menu.setPlats(loadPlatsByMenu(connection, id));
                return Optional.of(menu);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de recuperer le menu " + id, e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        try (Connection connection = openConnection();
             PreparedStatement stmt = connection.prepareStatement(DELETE_MENU)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de supprimer le menu " + id, e);
        }
    }

    private void saveMenu(Connection connection, Menu menu) throws SQLException {
        LocalDate dateCreation = menu.getDateCreation() == null ? LocalDate.now() : menu.getDateCreation();
        LocalDate dateMiseAJour = menu.getDateMiseAJour() == null ? LocalDate.now() : menu.getDateMiseAJour();
        BigDecimal prixTotal = menu.getPrixTotal() == null ? BigDecimal.ZERO : menu.getPrixTotal();
        try (PreparedStatement stmt = connection.prepareStatement(UPSERT_MENU)) {
            stmt.setInt(1, menu.getId());
            stmt.setString(2, menu.getNom());
            stmt.setInt(3, menu.getCreateurId());
            stmt.setString(4, menu.getCreateurNom());
            stmt.setDate(5, Date.valueOf(dateCreation));
            stmt.setDate(6, Date.valueOf(dateMiseAJour));
            stmt.setBigDecimal(7, prixTotal);
            stmt.executeUpdate();
        }
    }

    private void replaceMenuPlats(Connection connection, Menu menu) throws SQLException {
        try (PreparedStatement deleteStmt = connection.prepareStatement(DELETE_MENU_PLATS)) {
            deleteStmt.setInt(1, menu.getId());
            deleteStmt.executeUpdate();
        }

        if (menu.getPlats().isEmpty()) {
            return;
        }

        try (PreparedStatement insertStmt = connection.prepareStatement(INSERT_MENU_PLAT)) {
            for (Plat plat : menu.getPlats()) {
                insertStmt.setInt(1, menu.getId());
                insertStmt.setInt(2, plat.getId());
                insertStmt.setString(3, plat.getNom());
                insertStmt.setBigDecimal(4, plat.getPrix());
                insertStmt.addBatch();
            }
            insertStmt.executeBatch();
        }
    }

    private List<Plat> loadPlatsByMenu(Connection connection, Integer menuId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_PLATS_BY_MENU)) {
            stmt.setInt(1, menuId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Plat> plats = new ArrayList<>();
                while (rs.next()) {
                    plats.add(new Plat(
                            rs.getInt("plat_id"),
                            rs.getString("plat_nom"),
                            rs.getBigDecimal("plat_prix")
                    ));
                }
                return plats;
            }
        }
    }

    private Menu mapMenu(ResultSet rs) throws SQLException {
        Menu menu = new Menu();
        menu.setId(rs.getInt("id"));
        menu.setNom(rs.getString("nom"));
        menu.setCreateurId(rs.getInt("createur_id"));
        menu.setCreateurNom(rs.getString("createur_nom"));
        menu.setDateCreation(toLocalDate(rs.getDate("date_creation")));
        menu.setDateMiseAJour(toLocalDate(rs.getDate("date_mise_a_jour")));
        menu.setPrixTotal(rs.getBigDecimal("prix_total"));
        return menu;
    }

    private LocalDate toLocalDate(Date sqlDate) {
        return sqlDate == null ? LocalDate.now() : sqlDate.toLocalDate();
    }

    private Connection openConnection() throws SQLException {
        DbSettings current = settings;
        if (current == null) {
            synchronized (this) {
                current = settings;
                if (current == null) {
                    current = DbSettings.load();
                    settings = current;
                }
            }
        }
        return DriverManager.getConnection(current.jdbcUrl(), current.username(), current.password());
    }

    private record DbSettings(String jdbcUrl, String username, String password) {
        private static final String DEFAULT_PORT = "3306";

        private static DbSettings load() {
            Map<String, String> values = new HashMap<>(loadDotEnv());
            values.putAll(System.getenv());

            String explicitUrl = values.get("MYSQL_URL");
            if (explicitUrl != null && !explicitUrl.isBlank()) {
                return new DbSettings(explicitUrl, require(values, "MYSQL_USER"), require(values, "MYSQL_PASSWORD"));
            }

            String host = require(values, "MYSQL_HOST");
            String port = values.getOrDefault("MYSQL_PORT", DEFAULT_PORT);
            String database = require(values, "MYSQL_DATABASE");
            String user = require(values, "MYSQL_USER");
            String password = require(values, "MYSQL_PASSWORD");
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC";
            return new DbSettings(url, user, password);
        }

        private static Map<String, String> loadDotEnv() {
            Map<String, String> env = new HashMap<>();
            Path path = resolveDotEnvPath();

            if (path != null && Files.exists(path)) {
                loadDotEnvFromPath(path, env);
                return env;
            }

            loadDotEnvFromResource(env);
            return env;
        }

        private static void loadDotEnvFromPath(Path path, Map<String, String> env) {
            try {
                for (String line : Files.readAllLines(path)) {
                    String trimmed = line.trim();
                    if (trimmed.startsWith("#") || !trimmed.contains("=")) {
                        continue;
                    }
                    int split = trimmed.indexOf('=');
                    String key = trimmed.substring(0, split).trim();
                    String value = trimmed.substring(split + 1).trim();
                    env.put(key, stripQuotes(value));
                }
            } catch (IOException ignored) {
                // Si le fichier n'est pas lisible, on se repose sur les variables d'environnement du systeme.
            }
        }

        private static void loadDotEnvFromResource(Map<String, String> env) {
            try (InputStream inputStream = MenuRepository.class.getResourceAsStream("/.env")) {
                if (inputStream == null) {
                    return;
                }
                try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                    StringBuilder content = new StringBuilder();
                    char[] buffer = new char[1024];
                    int read;
                    while ((read = reader.read(buffer)) != -1) {
                        content.append(buffer, 0, read);
                    }
                    for (String line : content.toString().split("\\R")) {
                        String trimmed = line.trim();
                        if (trimmed.startsWith("#") || !trimmed.contains("=")) {
                            continue;
                        }
                        int split = trimmed.indexOf('=');
                        String key = trimmed.substring(0, split).trim();
                        String value = trimmed.substring(split + 1).trim();
                        env.put(key, stripQuotes(value));
                    }
                }
            } catch (IOException ignored) {
                // Fallback silencieux: on utilisera les variables d'environnement du systeme.
            }
        }

        private static Path resolveDotEnvPath() {
            String customPath = firstNonBlank(System.getenv("MENUS_DOTENV_PATH"), System.getProperty("MENUS_DOTENV_PATH"));
            if (customPath == null || customPath.isBlank()) {
                customPath = firstExistingCandidate(
                        Path.of(".env"),
                        Path.of(System.getProperty("user.dir", ".")).resolve(".env"),
                        Path.of(System.getProperty("user.home", ".")).resolve(".env")
                );
            }
            if (customPath != null && !customPath.isBlank()) {
                return Path.of(customPath);
            }
            return null;
        }

        private static String firstNonBlank(String... values) {
            for (String value : values) {
                if (value != null && !value.isBlank()) {
                    return value;
                }
            }
            return null;
        }

        private static String firstExistingCandidate(Path... candidates) {
            for (Path candidate : candidates) {
                if (candidate != null && Files.exists(candidate)) {
                    return candidate.toString();
                }
            }
            return null;
        }

        private static String require(Map<String, String> values, String key) {
            String value = values.get(key);
            if (value == null || value.isBlank()) {
                throw new IllegalStateException("Configuration manquante: " + key + " (variable d'environnement, .env ou MENUS_DOTENV_PATH)");
            }
            return value;
        }

        private static String stripQuotes(String value) {
            if ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'"))) {
                return value.substring(1, value.length() - 1);
            }
            return value;
        }
    }
}

