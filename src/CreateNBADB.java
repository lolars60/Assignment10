import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates nba.db from the local nba_players.csv file.
 */
public class CreateNBADB {
    private static final String CSV_FILE = "nba_players.csv";

    /**
     * Recreates the Player table and imports every CSV row.
     *
     * @param args optional first argument is the CSV file path
     */
    public static void main(String[] args) {
        Path csvPath = Path.of(args.length == 0 ? CSV_FILE : args[0]);
        List<Player> players;

        try {
            players = readPlayers(csvPath);
            if (players.isEmpty()) {
                throw new IOException("The CSV file contains no player records");
            }
            importPlayers(players);
            System.out.println("Created nba.db with " + players.size() + " players.");
        } catch (IOException | SQLException e) {
            System.err.println("Could not create the NBA database: " + e.getMessage());
        }
    }

    private static List<Player> readPlayers(Path csvPath) throws IOException {
        if (!Files.exists(csvPath)) {
            throw new IOException("CSV file not found: " + csvPath);
        }

        List<Player> players = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(csvPath)) {
            String header = reader.readLine();
            if (!"player_id,name,team,height,position,number,nationality,season"
                    .equals(header)) {
                throw new IOException("CSV header must be: "
                        + "player_id,name,team,height,position,number,nationality,season");
            }

            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                List<String> values = parseCsvLine(line);
                if (values.size() != 8) {
                    throw new IOException("Line " + lineNumber
                            + " must contain exactly 8 comma-separated values");
                }
                try {
                    players.add(new Player(Integer.parseInt(values.get(0)),
                            required(values.get(1), "name", lineNumber),
                            required(values.get(2), "team", lineNumber),
                            values.get(3), values.get(4), values.get(5),
                            values.get(6), required(values.get(7), "season", lineNumber)));
                } catch (NumberFormatException e) {
                    throw new IOException("Invalid player_id on line " + lineNumber, e);
                }
            }
        }
        return players;
    }

    private static void importPlayers(List<Player> players) throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:nba.db")) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("PRAGMA busy_timeout = 30000");
                statement.executeUpdate("DROP TABLE IF EXISTS Player");
                statement.executeUpdate("CREATE TABLE Player ("
                        + "player_id INTEGER PRIMARY KEY, name TEXT NOT NULL, "
                        + "team TEXT NOT NULL, height TEXT, position TEXT, "
                        + "number TEXT, nationality TEXT, season TEXT NOT NULL)");
            }
            connection.setAutoCommit(false);
            String sql = "INSERT INTO Player "
                    + "(player_id, name, team, height, position, number, nationality, season) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement insert = connection.prepareStatement(sql)) {
                for (Player player : players) {
                    insert.setInt(1, player.id);
                    insert.setString(2, player.name);
                    insert.setString(3, player.team);
                    insert.setString(4, player.height);
                    insert.setString(5, player.position);
                    insert.setString(6, player.number);
                    insert.setString(7, player.nationality);
                    insert.setString(8, player.season);
                    insert.addBatch();
                }
                insert.executeBatch();
            }
            connection.commit();
        }
    }

    private static String required(String value, String field, int line)
            throws IOException {
        if (value == null || value.isBlank()) {
            throw new IOException(field + " cannot be blank on line " + line);
        }
        return value.trim();
    }

    private static List<String> parseCsvLine(String line) throws IOException {
        List<String> values = new ArrayList<>();
        StringBuilder value = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (character == '"') {
                quoted = !quoted;
            } else if (character == ',' && !quoted) {
                values.add(value.toString().trim());
                value.setLength(0);
            } else {
                value.append(character);
            }
        }
        if (quoted) {
            throw new IOException("Unclosed quote in CSV line");
        }
        values.add(value.toString().trim());
        return values;
    }

    private record Player(int id, String name, String team, String height,
                          String position, String number, String nationality,
                          String season) {
    }
}
