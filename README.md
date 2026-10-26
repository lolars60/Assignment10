## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).

## NBA roster database

`CreateNBADB` creates `nba.db` from the local `nba_players.csv` file. The
`Player` table includes `name`, `team`, `height`, `position`, `number`,
`nationality`, and `season`. Running the program again replaces the table.
Add or edit CSV rows to expand or update the roster without changing the Java
code.

Compile and run it from the project directory:

```bash
javac -cp "lib/sqlite-jdbc-3.53.4.0.jar" -d bin src/CreateNBADB.java
java -cp "bin:lib/sqlite-jdbc-3.53.4.0.jar" CreateNBADB
```

You can also provide a different CSV path:

```bash
java -cp "bin:lib/sqlite-jdbc-3.53.4.0.jar" CreateNBADB path/to/players.csv
```

The CSV contains the 2026-27 roster records supplied in the local roster
source file. Its required columns are:
`player_id,name,team,height,position,number,nationality,season`.

## NBA query API

`NBAAPI` provides read-only Java methods for searching the generated database:
find players by name, team, position, or nationality; sort players by height or
jersey number; count players; and count players on each team. It uses prepared
statements for all user-provided search values.

`NBADriver` is the interactive console program for the assignment. It prompts
the user with a menu and supports player-name searches, team searches, position
searches, height ordering, and jersey-number ordering.

Example commands:

```bash
javac -cp "lib/sqlite-jdbc-3.53.4.0.jar" -d bin src/*.java
java -cp "bin:lib/sqlite-jdbc-3.53.4.0.jar" NBADriver
```

The API expects `nba.db` to exist, so run `CreateNBADB` first. Generate the
Javadoc documentation with:

```bash
javadoc -d doc src/NBAAPI.java src/NBADriver.java
```
