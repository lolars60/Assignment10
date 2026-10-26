import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Read-only API for querying the roster database created by CreateNBADB.
 */
public class NBAAPI implements AutoCloseable {
    private final Connection connection;

    /**
     * Opens the default nba.db database.
     *
     * @throws SQLException if the database connection cannot be opened
     */
    public NBAAPI() throws SQLException {
        this("jdbc:sqlite:nba.db");
    }

    /**
     * Opens a database using the supplied JDBC URL.
     *
     * @param databaseUrl JDBC URL for the NBA database
     * @throws SQLException if the database connection cannot be opened
     */
    public NBAAPI(String databaseUrl) throws SQLException {
        connection = DriverManager.getConnection(databaseUrl);
        if (!hasPlayerTable()) {
            connection.close();
            throw new SQLException("The database does not contain the Player table. "
                    + "Run CreateNBADB first, then start NBADriver again.");
        }
    }

    private boolean hasPlayerTable() throws SQLException {
        String sql = "SELECT name FROM sqlite_master "
                + "WHERE type = 'table' AND name = 'Player'";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rows = statement.executeQuery()) {
            return rows.next();
        }
    }

    /**
     * Gets every player in the database, ordered by team and name.
     *
     * @return all players in the database
     * @throws SQLException if the database cannot be queried
     */
    public List<Player> allPlayers() throws SQLException {
        return queryPlayers("SELECT player_id, name, team, height, position, "
                + "number, nationality, season FROM Player ORDER BY team, name");
    }

    /**
     * Finds players whose names contain the supplied text.
     *
     * @param name all or part of a player's name
     * @return matching players
     * @throws SQLException if the database cannot be queried
     * @throws IllegalArgumentException if name is blank
     */
    public List<Player> findPlayers(String name) throws SQLException {
        return queryPlayers("SELECT player_id, name, team, height, position, "
                + "number, nationality, season FROM Player "
                + "WHERE name LIKE ? COLLATE NOCASE ORDER BY name",
                "%" + requireText(name, "name") + "%");
    }

    /**
     * Finds all players on a team.
     *
     * @param team all or part of a team name
     * @return players on matching teams
     * @throws SQLException if the database cannot be queried
     * @throws IllegalArgumentException if team is blank
     */
    public List<Player> playersOnTeam(String team) throws SQLException {
        return queryPlayers("SELECT player_id, name, team, height, position, "
                + "number, nationality, season FROM Player "
                + "WHERE team LIKE ? COLLATE NOCASE ORDER BY name",
                "%" + requireText(team, "team") + "%");
    }

    /**
     * Finds players whose position contains the supplied text.
     *
     * @param position a position such as Guard or Forward
     * @return players at matching positions
     * @throws SQLException if the database cannot be queried
     * @throws IllegalArgumentException if position is blank
     */
    public List<Player> playersAtPosition(String position) throws SQLException {
        return queryPlayers("SELECT player_id, name, team, height, position, "
                + "number, nationality, season FROM Player "
                + "WHERE position LIKE ? COLLATE NOCASE ORDER BY name",
                "%" + requireText(position, "position") + "%");
    }

    /**
     * Finds players whose nationality contains the supplied text.
     *
     * @param nationality a country or nationality
     * @return players from matching nationalities
     * @throws SQLException if the database cannot be queried
     * @throws IllegalArgumentException if nationality is blank
     */
    public List<Player> playersFromNationality(String nationality) throws SQLException {
        return queryPlayers("SELECT player_id, name, team, height, position, "
                + "number, nationality, season FROM Player "
                + "WHERE nationality LIKE ? COLLATE NOCASE ORDER BY name",
                "%" + requireText(nationality, "nationality") + "%");
    }

    /**
     * Gets all players ordered by height.
     *
     * @param tallestFirst true for tallest-to-shortest, false for shortest-to-tallest
     * @return players ordered by height
     * @throws SQLException if the database cannot be queried
     */
    public List<Player> playersByHeight(boolean tallestFirst) throws SQLException {
        String direction = tallestFirst ? "DESC" : "ASC";
        String sql = "SELECT player_id, name, team, height, position, number, "
                + "nationality, season FROM Player "
                + "ORDER BY CAST(substr(height, 1, instr(height, '-') - 1) AS INTEGER) "
                + direction + ", CAST(substr(height, instr(height, '-') + 1) AS INTEGER) "
                + direction + ", name";
        return queryPlayers(sql);
    }

    /**
     * Gets all players ordered by jersey number.
     *
     * @param ascending true for lowest-to-highest jersey number
     * @return players ordered by jersey number
     * @throws SQLException if the database cannot be queried
     */
    public List<Player> playersByNumber(boolean ascending) throws SQLException {
        String direction = ascending ? "ASC" : "DESC";
        return queryPlayers("SELECT player_id, name, team, height, position, "
                + "number, nationality, season FROM Player "
                + "ORDER BY CAST(number AS INTEGER) " + direction + ", name");
    }

    /**
     * Gets the number of players on every team.
     *
     * @return team names and their player counts
     * @throws SQLException if the database cannot be queried
     */
    public List<TeamCount> teamCounts() throws SQLException {
        List<TeamCount> results = new ArrayList<>();
        String sql = "SELECT team, COUNT(*) AS player_count FROM Player "
                + "GROUP BY team ORDER BY team";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rows = statement.executeQuery()) {
            while (rows.next()) {
                results.add(new TeamCount(rows.getString("team"),
                        rows.getInt("player_count")));
            }
        }
        return results;
    }

    /**
     * Counts all players in the database.
     *
     * @return the number of players
     * @throws SQLException if the database cannot be queried
     */
    public int playerCount() throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM Player");
             ResultSet rows = statement.executeQuery()) {
            rows.next();
            return rows.getInt(1);
        }
    }

    private List<Player> queryPlayers(String sql, String... parameters)
            throws SQLException {
        List<Player> players = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.length; i++) {
                statement.setString(i + 1, parameters[i]);
            }
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    players.add(new Player(
                            rows.getInt("player_id"),
                            rows.getString("name"),
                            rows.getString("team"),
                            rows.getString("height"),
                            rows.getString("position"),
                            rows.getString("number"),
                            rows.getString("nationality"),
                            rows.getString("season")));
                }
            }
        }
        return players;
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " cannot be empty");
        }
        return value.trim();
    }

    /**
     * Closes the database connection managed by this API.
     *
     * @throws SQLException if the connection cannot be closed
     */
    @Override
    public void close() throws SQLException {
        connection.close();
    }

    /**
     * Represents one player roster record.
     *
     * @param id NBA player identifier
     * @param name player's display name
     * @param team player's team
     * @param height player's height in feet-inches format
     * @param position player's roster position
     * @param number player's jersey number
     * @param nationality player's listed nationality
     * @param season roster season
     */
    public record Player(int id, String name, String team, String height,
                         String position, String number, String nationality,
                         String season) {
        @Override
        public String toString() {
            return name + " | " + team + " | " + height + " | " + position
                    + " | #" + number + " | " + nationality
                    + " | " + season;
        }
    }

    /**
     * Represents a team and the number of players listed for it.
     *
     * @param team team name
     * @param playerCount number of players on the team
     */
    public record TeamCount(String team, int playerCount) {
    }
}
