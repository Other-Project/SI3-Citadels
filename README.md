# Projet Citadels - Team C

**[Lassauniere Théo](https://github.com/theoLassauniere), [Galli Evan](https://github.com/06Games),
[Lubrat Jilian](https://github.com/LubratJilian), [Michelozzi Antoine-Marie](https://github.com/mantoniu)**  
Polytech Nice-Sophia - SI3 PS5

**Objectifs :** Recréer le jeu de société Citadelles et simuler une partie avec 4 robots ou plus.

------------------------

## Présentation :

### Le jeu Citadelles

Citadelles est un jeu de société composé de cartes personnage ayant toutes une capacité différente 
et de cartes quartier de couleurs différentes (rouge, jaune, bleu, vert et violet), 
les cartes violettes ont, comme les personnages, des capacités propres. 

Un tour se déroule de la façon suivante :
* Chaque joueur en partant de celui qui a la couronne, choisit à tour de rôle son personnage. 
* Puis chaque personnage est appelé dans un ordre défini. 
* La personne ayant choisi le personnage peut alors réaliser différentes actions :
  * piocher 2 cartes ou prendre 2 pièces d'or
  * construire un quartier : pour construire un quartier, il faut avoir assez de pièces pour pouvoir payer le coût indiqué sur la carte. 
  * réaliser l'action spéciale associée à son personnage ou à un de ses quartiers violets déjà construit. 

La partie se termine quand une personne a construit 8 quartiers, et on décompte pour chaque joueur son nombre de points, 
qui est équivalent à la somme des coûts de chacun des quartiers, sauf cas particuliers.

L'objectif est d'implémenter ce jeu et de réaliser des parties composées uniquement de robots.

Il faut ainsi dans un premier temps réaliser:

- Les cartes personnages 
- Les cartes quartiers 
- Le système de jeu
- L'implémentation d'un premier robot

## Lancement avec Maven:

- ### Le programme principal

  ```
  mvn clean compile exec:java
  ```

- ### Les tests 

  ```
  mvn clean test
  ```

- ###  Générer un jar puis le lancer:

  ```
  mvn clean package
  java -cp ./target/citadels-1.0.jar fr.univ_cotedazur.polytech.si3.team_c.citadels.Game
  ```





