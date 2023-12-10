# Projet Citadels - Team C

**[Lassauniere Théo](https://github.com/theoLassauniere), [Galli Evan](https://github.com/06Games),
[Lubrat Jilian](https://github.com/LubratJilian), [Michelozzi Antoine-Marie](https://github.com/mantoniu)**  
Polytech Nice-Sophia - SI3 PS5

**Objectifs :** Créer le jeu de société Citadels et simuler une partie aevc 4 robots ou plus.

------------------------

## Présentation :

### Le jeu Citadels

Le jeu Citadels est un jeu de société composé de cartes personnage toutes ayant une capacité différente
et de cartes quartier toutes de couleurs différentes (rouge, jaune,bleue, vert et violet)
, les cartes violetes ont comme les personnages des capacitées propres. 

Un tour se déroule de la façon suivante, chaque joueur en partant de celui qui a la couronne choisit à tour 
de rôle un personnage. Puis chaque personnage est appelé à tour de rôle dans un ordre défini. Chaque
personnes peut réaliser au moins 2 actions différentes par tour, piocher 2 cartes ou prendre 2 pièces d'or
et construire un quartier. Pour construire un quartier il faut avoir assez de pièces pour pouvoir payer
le coût indiquer sur la  carte. De plus le joueur peut réaliser l'action spéciale asociée à son 
personnage ou à un quartiers violets déjà construit si ces dernières procurent une autre actions possible. 
Enfin la partie se termine quand une personne a consrtruit 8 quartier, et on décompte pour chaque joueur sont nombre
de points, qui est équivalent à la somme des coûts de chacun des quartiers sauf cas particuliers pour certains quartiers.

Le but est d'implémenter ce jeu afin de pouvoir réaliser des entre des robots.

Il faut ainsi dans un premier temps réaliser:

- Les cartes personnages 
- Les cartes quartiers 
- Le système de jeu
- L'implémentation d'un premier robot

## Lancement avec Maven:

- ### Le programme principale

  mvn clean compile exec:java

- ### Les tests 

  mvn clean test

- ###  Générer un jar puis le lancer:

  mvn clean package
  java -cp .\citadels-1.0.jar fr.univ_cotedazur.polytech.si3.team_c.citadels.Game





