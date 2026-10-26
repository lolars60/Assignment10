import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Console driver that lets a user query the NBA player database.
 */
public class NBADriver {
    /**
     * Creates a console driver.
     */
    public NBADriver() {
    }

    /**
     * Starts the interactive menu.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        try (NBAAPI api = new NBAAPI(); Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                printMenu();
                String choice = scanner.nextLine().trim();
                try {
                    switch (choice) {
                        case "1" -> displayPlayers(api.findPlayers(
                                prompt(scanner, "Enter player name: ")));
                        case "2" -> displayPlayers(api.playersOnTeam(
                                prompt(scanner, "Enter team: ")));
                        case "3" -> displayPlayers(api.playersAtPosition(
                                prompt(scanner, "Enter position: ")));
                        case "4" -> displayPlayers(api.playersByHeight(true));
                        case "5" -> displayPlayers(api.playersByHeight(false));
                        case "6" -> displayPlayers(api.playersByNumber(true));
                        case "7" -> displayPlayers(api.playersByNumber(false));
                        case "8" -> {
                            System.out.println("Players in database: "
                                    + api.playerCount());
                        }
                        case "9" -> running = false;
                        default -> System.out.println("Please enter a number from 1 to 9.");
                    }
                } catch (IllegalArgumentException | SQLException e) {
                    System.out.println("Could not complete query: " + e.getMessage());
                }
            }
            System.out.println("Goodbye.");
        } catch (SQLException e) {
            System.err.println("Could not open nba.db: " + e.getMessage());
        }
    }

    private static void printMenu() {
        System.out.println("\nNBA Player Database");
        System.out.println("1. Find a player by name");
        System.out.println("2. Display players on a team");
        System.out.println("3. Display players by position");
        System.out.println("4. List players tallest to shortest");
        System.out.println("5. List players shortest to tallest");
        System.out.println("6. List players by jersey number, low to high");
        System.out.println("7. List players by jersey number, high to low");
        System.out.println("8. Display player count");
        System.out.println("9. Exit");
        System.out.print("Enter your choice: ");
    }

    private static String prompt(Scanner scanner, String message) {
        System.out.print(message);
        return scanner.nextLine();
    }

    private static void displayPlayers(List<NBAAPI.Player> players) {
        if (players.isEmpty()) {
            System.out.println("No players found.");
            return;
        }
        String format = "%-24s %-24s %-8s %-18s %-8s %-15s %-10s%n";
        System.out.printf(format, "Name", "Team", "Height", "Position",
                "Number", "Nationality", "Season");
        System.out.println("-".repeat(115));
        for (NBAAPI.Player player : players) {
            System.out.printf(format, player.name(), player.team(), player.height(),
                    player.position(), "#" + player.number(), player.nationality(),
                    player.season());
        }
        System.out.println("Results: " + players.size());
    }
}
