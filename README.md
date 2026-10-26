# Assignment 10: NBA Player Database API

This project creates and queries a SQLite database containing NBA player
roster information for the 2026-27 season.

The project uses the roster data in
[`nba_players.csv`](./nba_players.csv). The CSV file includes:

- Player ID
- Player name
- Team
- Height
- Position
- Jersey number
- Nationality
- Season

## Project files

- [`src/CreateNBADB.java`](./src/CreateNBADB.java) reads the CSV file and
  creates `nba.db`.
- [`src/NBAAPI.java`](./src/NBAAPI.java) provides the Java database API.
- [`src/NBADriver.java`](./src/NBADriver.java) provides the interactive
  console menu.
- [`nba_players.csv`](./nba_players.csv) contains the roster data.
- [`nba.db`](./nba.db) is the generated SQLite database.
- [`doc/`](./doc/) contains the generated Javadoc documentation.
- [`lib/sqlite-jdbc-3.53.4.0.jar`](./lib/sqlite-jdbc-3.53.4.0.jar) is the
  SQLite JDBC dependency.

## Part 1: Create the database

`CreateNBADB` recreates the `Player` table and imports every record from the
CSV file. It validates the CSV header, checks required values, and uses a
transaction when inserting the data.

Compile the project from the project directory:

```bash
javac -cp "lib/sqlite-jdbc-3.53.4.0.jar" -d bin src/*.java
```

Create or refresh the database:

```bash
java -cp "bin:lib/sqlite-jdbc-3.53.4.0.jar" CreateNBADB
```

The default input file is `nba_players.csv`. A different CSV file can be
provided as the first argument:

```bash
java -cp "bin:lib/sqlite-jdbc-3.53.4.0.jar" CreateNBADB path/to/players.csv
```

## Part 2: Java API

`NBAAPI` manages the connection to `nba.db` and provides read-only methods
that retrieve useful information:

- `allPlayers()` returns every player ordered by team and name.
- `findPlayers(String name)` searches for a player by name.
- `playersOnTeam(String team)` returns players on a team.
- `playersAtPosition(String position)` returns players at a position.
- `playersFromNationality(String nationality)` returns players from a
  nationality.
- `playersByHeight(boolean tallestFirst)` sorts players by height.
- `playersByNumber(boolean ascending)` sorts players by jersey number.
- `teamCounts()` returns each team and its player count.
- `playerCount()` returns the total number of players.

The API uses prepared statements for search values and implements
`AutoCloseable` so the database connection can be closed safely.

## Part 3: Javadoc

Public classes, methods, records, and record parameters in the API are
documented with Javadoc comments.

Generate the documentation in the required `doc/` directory:

```bash
rm -rf doc
javadoc -d doc src/NBAAPI.java src/NBADriver.java
```

Open [`doc/index.html`](./doc/index.html) in a browser to view the generated
documentation.

## Part 4: Driver program

`NBADriver` is a console-based program that allows the user to interact with
the database through `NBAAPI`.

Run the driver:

```bash
java -cp "bin:lib/sqlite-jdbc-3.53.4.0.jar" NBADriver
```

The menu provides these options:

1. Find a player by name
2. Display players on a team
3. Display players by position
4. List players from tallest to shortest
5. List players from shortest to tallest
6. List players by jersey number, low to high
7. List players by jersey number, high to low
8. Display the number of players
9. Exit

Player results are displayed in aligned columns containing the player name,
team, height, position, jersey number, nationality, and season.

## Complete setup

Run the following commands in order after cloning the repository:

```bash
javac -cp "lib/sqlite-jdbc-3.53.4.0.jar" -d bin src/*.java
java -cp "bin:lib/sqlite-jdbc-3.53.4.0.jar" CreateNBADB
java -cp "bin:lib/sqlite-jdbc-3.53.4.0.jar" NBADriver
```

The project requires Java and the included SQLite JDBC JAR. No internet
connection is required because the database is created from the local CSV
file.
