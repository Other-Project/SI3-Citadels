# Citadels Project - Team C

**[Lassauniere Théo](https://github.com/theoLassauniere), [Galli Evan](https://github.com/06Games),
[Lubrat Jilian](https://github.com/LubratJilian), [Michelozzi Antoine-Marie](https://github.com/mantoniu)**  
Polytech Nice-Sophia - SI3 PS5

**Goals :** Recreate the Citadels board game and simulate a game with 3 or more bots.

------------------------

## Introducing :

### Citadels game

Citadels is a board game made up of character cards, each with a different ability,
and districts of different colours (red, yellow, blue, green and purple).
Like the characters, the purple cards have their own abilities.

A turn proceeds as described below:

* Each player chooses a character in turn, starting with the player with the crown.
* Each character is then called up in a set order.
* The person who has chosen the character can then perform various actions:
  * Draw 2 cards or take 2 gold coins
  * Build a district: to build a district, the person must have enough coins to pay the cost of the card.
  * Perform the special action associated with the chosen character or one of your already-built purple districts.

The game ends at the end of the round in which a player has built 8 districts (or 10 if there are 3 players).

The score is the sum of the following elements:

- Total construction cost of the build districts.
- 3 points if the city includes districts of five different colours.
- 4 points for the first player to build his eighth district
- 2 points for the other players with eight districts.

The aim is to implement this game and create parties made up entirely of bots.

The first step is to make:

- Character cards
- The district cards
- The game system
- Implement the first bot

We then chose to implement different types of bots with different characteristics:

- The discreet bot, whose aim is to be as discreet as possible in order to avoid
  frightening other players so that it can progress without being slowed down by the actions of other players.
- The aggressive bot, whose aim is to slow down other players' progress as much as possible.
- The fearful bot, which is afraid of all the other players and plays it safe as soon as it is potentially in danger
- The random bot, which makes random choices, gives an idea of the effectiveness of other bots.

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
    Launch the demo of a single game with all the logs.

  - #### Statistics mode
      ```
      mvn clean compile exec:java -Dexec.args="--2thousands"
      ```
    Simulates 2x1000 games :
    - 1000 games of our best bot against the second-best bot (with other bots to complete the game)
    - 1000 games of our best bot against itself (or as many clones of itself as there are players)

    Next, game statistics will be displayed as follows:
    number and percentage of games won/lost/drawn, and the average score for each bot.

  - #### CSV mode
      ```
      mvn clean compile exec:java -Dexec.args="--csv"
      ```
    Writes the statistics collected during the simulation to the CSV file located in `stats/gamestats.csv`.
    If it exists, the data is added to the pre-existing data.

- ### Testing
  To execute the tests, you need to use this command :

  ```
  mvn clean test
  ```

- ### Generate a jar and launch it:
  To generate the `.jar` package and run the program :

  ```
  mvn clean package
  java -cp ./target/citadels-1.0.jar fr.univ_cotedazur.polytech.si3.team_c.citadels.Main
  ```