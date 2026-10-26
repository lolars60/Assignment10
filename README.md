# Assignment 10: NBA Player Database

## 1. Project Description

My database is about NBA players from the 2026-27 season. It stores each
player's name, team, height, position, jersey number, nationality, and season.

The purpose of the program is to let a user search and organize NBA player
information. The program can answer questions such as:

- What team does a player play for?
- What position does a player play?
- Who plays for a certain team?
- Who plays a certain position?
- Who are the tallest or shortest players?
- How many players are in the database?

The user interacts with the database through a menu in `NBADriver`. The menu
uses methods from `NBAAPI` to perform the searches.

## 2. Example Test Run

This is an example of running the program, searching for LeBron James,
displaying players by position, and then exiting.

```text
NBA Player Database
1. Find a player by name
2. Display players on a team
3. Display players by position
4. List players tallest to shortest
5. List players shortest to tallest
6. List players by jersey number, low to high
7. List players by jersey number, high to low
8. Display player count
9. Exit
Enter your choice: 1
Enter player name: LeBron James

Name                     Team                     Height   Position           Number   Nationality     Season
-------------------------------------------------------------------------------------------------------------------
LeBron James             Philadelphia 76ers       6-9      Forward            #23      Unknown         2026-27
Results: 1

NBA Player Database
1. Find a player by name
2. Display players on a team
3. Display players by position
4. List players tallest to shortest
5. List players shortest to tallest
6. List players by jersey number, low to high
7. List players by jersey number, high to low
8. Display player count
9. Exit
Enter your choice: 3
Enter position: Center

Name                     Team                     Height   Position           Number   Nationality     Season
-------------------------------------------------------------------------------------------------------------------
Nikola Jokic             Denver Nuggets           6-11     Center             #15      Unknown         2026-27
Results: 1

NBA Player Database
1. Find a player by name
2. Display players on a team
3. Display players by position
4. List players tallest to shortest
5. List players shortest to tallest
6. List players by jersey number, low to high
7. List players by jersey number, high to low
8. Display player count
9. Exit
Enter your choice: 9
Goodbye.
```

## 3. AI Usage Statement

I used GitHub Copilot as an AI tool while completing this assignment. I used
it to help create sample database data, write SQL queries, develop the
`NBAAPI` methods, create the menu in `NBADriver`, debug SQLite errors, format
the console output, and write documentation.

I reviewed and tested the generated code before including it in the project.
I changed the database source from a live NBA website to a local CSV file
because the website connection was unreliable. I also tested the program by
compiling it, creating the database, searching for players, filtering by
position, sorting players, and exiting through the menu.
