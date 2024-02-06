# Citadels Project - Team C

**[Lassauniere Théo](https://github.com/theoLassauniere), [Galli Evan](https://github.com/06Games),
[Lubrat Jilian](https://github.com/LubratJilian), [Michelozzi Antoine-Marie](https://github.com/mantoniu)**  
Polytech Nice-Sophia - SI3 PS5

**Goals :** Recreate the board game Citadels and simulate a game with 4 or more robots.

------------------------

## Introducing :

### The citadel game

Citadels is a board game made up of character cards, each with a different ability,
and districts of different colours (red, yellow, blue, green and purple).
Like the characters, the purple cards have their own abilities.

A turn proceeds as described below:

* Each player in turn chooses their character, starting with the player with the crown.
* Each character is then called up in a set order.
* The person who has chosen the character can then perform various actions:
  * draw 2 cards or take 2 gold coins
  * build a district: to build a district, you must have enough coins to pay the cost shown on the card.
  * perform the special action associated with your character or one of your already-built purple districts.

The game ends when a player has built 8 districts (or 10 if there are 3 players), and each player's points are counted,
which is equivalent to the sum of the costs of each of the districts, except in special cases.

The aim is to implement this game and create parties made up entirely of robots.

The first step is to make:

- Character cards
- The neighbourhood cards
- The game system
- Implement the first robot

## Launching with Maven:

> **Note**  
> The terminal must use UTF8 encoding for emoji to be displayed correctly.  
> On Windows, you may need to run the following PowerShell command before launching the program
> ```pwsh
> [Console]::InputEncoding = [Console]::OutputEncoding = New-Object System.Text.UTF8Encoding
> ```

- ### The main program

  ```
  mvn clean compile exec:java
  ```

- ### The tests

  ```
  mvn clean test
  ```

- ### Generate a jar and launch it:

  ```
  mvn clean package
  java -cp ./target/citadels-1.0.jar fr.univ_cotedazur.polytech.si3.team_c.citadels.Game
  ```