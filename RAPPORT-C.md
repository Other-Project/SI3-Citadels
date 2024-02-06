# Projet Citadels - Team C

**[Lassauniere Théo](https://github.com/theoLassauniere), [Galli Evan](https://github.com/06Games),
[Lubrat Jilian](https://github.com/LubratJilian), [Michelozzi Antoine-Marie](https://github.com/mantoniu)**  
Polytech Nice-Sophia - SI3 PS5

------------------------

## Le Jeu :

### Les règles du jeu Citadelles implémentées

Citadelles est un jeu de société composé de cartes personnage ayant toutes une capacité différente
et de cartes quartier de couleurs différentes (rouge, jaune, bleu, vert et violet),
les cartes violettes ont, comme les personnages, des capacités propres.

Un tour se déroule de la façon suivante :

* Chaque joueur en partant de celui qui a la couronne, choisit à tour de rôle son personnage.
* Puis chaque personnage est appelé dans un ordre défini.
* La personne ayant choisi le personnage peut alors réaliser différentes actions :
    * piocher 2 cartes ou prendre 2 pièces d'or
    * construire un quartier : pour construire un quartier, il faut avoir assez de pièces pour pouvoir payer le coût
      indiqué sur la carte.
    * réaliser l'action spéciale associée à son personnage ou à un de ses quartiers violets déjà construit.

La partie se termine quand une personne a construit 8 quartiers (pour une partie à 4,5,6 ou 7 joueurs) ou 10 districts (
pour une partie à 3 joueurs) et que le tour de jeu est terminé. Puis, on décompte pour chaque joueur son nombre de
points,
qui est équivalent à la somme des points donnés par chacun des quartiers et des différents bonus.

Les bonus possibles sont :

* +4 points pour le premier joueur ayant construit le dernier quartier nécessaire pour finir la partie
* +3 points si le joueurs à construit au moins un quartier de chaque couleurs
* +2 points pour tous les autres joueurs qui ont construit le nombre de district nécessaire pour terminer la partie

Lors de la phase de choix des parssonages d'un tour de jeu, les règles varient en fonction du nombre de joueurs:

* A 3 joueurs : 2 cartes personnages sont écartées faces visibles et deux autres face cachée, l'assasin est exclus des
  personnages
* A 4 joueurs : 2 cartes personnages sont écartées faces visibles et deux autres face cachée
* A 5 joueurs : 1 cartes personnages sont écartées faces visibles et deux autres face cachée
* A 6 et 7 joueurs : 0 cartes personnages sont écartées faces visibles et deux autres face cachée

(Dans tout les cas le roi ne peut pas être parmis les cartes placées faces visibles, mais peut être face cachée)

### Les subtilitées

* Si personne ne prends le Roi, alors la ne tourne pas entre les joueurs
* Il n'y a pas de maximum de pièces, il est possible que chacun des joueurs ai une infinité de pièces
* Un joueur qui a construit le nombre de quartiers nécessaire pour finir la partie peut, si il en a la capacité,
  construire des quartiers jusqu'à la fin de son tour. (Par exemple avec l'Architecte)

## Les Bots :

Différent type de bots ont été implémenter, il y en a 5 et ils représentent chacun une stratégie différente.

### Bot

&ensp; &nbsp; &nbsp; &nbsp; C'est l'implémentation du bot de base, sur lequel se base da'autres types de bot, il va
chercher à prendre le personnage qu'il pense être le meilleur par de nombreux facteurs mathématiques. De même pour toute
les décisions, il va essayé de prendre ce qui lui parait être le mieux en le déterminant à l'aide de calculs
mathématiques.

### Discreet Bot

&ensp; &nbsp; &nbsp; &nbsp; Basé sur le Bot, ce bot à pour but de ne pas se faire repérer des autres et essaye de rester
discret afin de gagner. Dès qu'il est en capacité de terminer la partie il prends l'opportunité.

## Lancement avec Maven:

> **Note**  
> The terminal must use UTF8 encoding for emoji to be displayed correctly.  
> On Windows, you may need to run the following PowerShell command before launching the program
> ```pwsh
> [Console]::InputEncoding = [Console]::OutputEncoding = New-Object System.Text.UTF8Encoding
> ```

- ### Le programme principal

  ```
  mvn clean compile exec:java
  ```

- ### Les tests

  ```
  mvn clean test
  ```

- ### Générer un jar puis le lancer:

  ```
  mvn clean package
  java -cp ./target/citadels-1.0.jar fr.univ_cotedazur.polytech.si3.team_c.citadels.Game
  ```





