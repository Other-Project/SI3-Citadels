# Citadels Project - Team C

**[Lassauniere Théo](https://github.com/theoLassauniere), [Galli Evan](https://github.com/06Games),
[Lubrat Jilian](https://github.com/LubratJilian), [Michelozzi Antoine-Marie](https://github.com/mantoniu)**  
Polytech Nice-Sophia - SI3 PS5

**Goals :** Recreate the board game Citadels and simulate a game with 4 or more robots.

------------------------

## Introducing :

### Citadels game

Citadels is a board game made up of character cards, each with a different ability,
and districts of different colours (red, yellow, blue, green and purple).
Like the characters, the purple cards have their own abilities.

A turn proceeds as described below:

* Each player in turn chooses their character, starting with the player with the crown.
* Each character is then called up in a set order.
* The person who has chosen the character can then perform various actions:
  * Draw 2 cards or take 2 gold coins
  * Build a district: to build a district, you must have enough coins to pay the cost shown on the card.
  * Perform the special action associated with your character or one of your already-built purple districts.

The game ends when a player has built 8 districts (or 10 if there are 3 players), and each player's points are counted,
which is equivalent to the sum of the costs of each of the districts, except in special cases.

The aim is to implement this game and create parties made up entirely of robots.

The first step is to make:

- Character cards
- The districts cards
- The game system
- Implement the first bot

We then chose to implement different types of bots with different characteristics:

- The discreet bot whose principle is to be as discreet as possible so as not to be affected by
  the actions of other players.
- The aggressive bot, whose aim is to take as many actions as possible on the other players.
- The fearful bot, which is afraid of all the other players and plays it safe as soon as it is potentially in danger

At the same time, we've corrected a number of bugs detected in the tests and refactored the `Game`
class by creating the `Action` and `CharacterManager` classes, which respectively
manage the actions and the data needed for the character selection turn.

Finally, we implemented the features requested during the rush week:

- [The Statistics mode](#statistics-mode)
- [The CSV mode](#csv-mode)
- The Richard's bot which uses
  the [strategy given by Richard](https://forum.trictrac.net/t/citadelles-charte-citadelles-de-base/509)

## Launching with Maven:

> **Note**  
> The terminal must use UTF8 encoding for emoji to be displayed correctly.  
> On Windows, you may need to run the following PowerShell command before launching the program
> ```pwsh
> [Console]::InputEncoding = [Console]::OutputEncoding = New-Object System.Text.UTF8Encoding
> ```

- ### The main program
  - #### Demo mode
      ```
      mvn clean compile exec:java -Dexec.args="--demo"
      ```
    Launch the demo of a single game with the full log.

  - #### Statistics mode
      ```
      mvn clean compile exec:java -Dexec.args="--2thousands"
      ```
    Launch 2 x 1000 games and print statistics.

  - #### CSV mode
      ```
      mvn clean compile exec:java -Dexec.args="--2thousands"
      ```
    Launch a simulation of several games (not necessarily 1000) with
    rereading of "stats/gamestats.csv" if it exists and addition of new statistics.

- ### Testing
  To execute the tests, you need to use this command :

  ```
  mvn clean test
  ```

- ### Generate a jar and launch it:
  To generate the `.jar` package and run the program :

  ```
  mvn clean package
  java -cp ./target/citadels-1.0.jar fr.univ_cotedazur.polytech.si3.team_c.citadels.Game
  ```