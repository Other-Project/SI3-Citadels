# Projet Citadels - Team C

**[Lassauniere Théo](https://github.com/theoLassauniere), [Galli Evan](https://github.com/06Games),
[Lubrat Jilian](https://github.com/LubratJilian), [Michelozzi Antoine-Marie](https://github.com/mantoniu)**  
Polytech Nice-Sophia - SI3 PS5

------------------------

# 1. Avancement :
## Le Jeu :

### Les règles du jeu Citadelles implémentées

Citadelles est un jeu de société composé de cartes personnage ayant toute une capacité différente
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
* +3 points si le joueur construit au moins un quartier de chaque couleur
* +2 points pour tous les autres joueurs qui ont construit le nombre de districts nécessaire pour terminer la partie

Lors de la phase de choix des personages d'un tour de jeu, les règles varient en fonction du nombre de joueurs :

* A 3 joueurs : 2 cartes personnages sont écartées faces visibles et deux autres face cachée, l'assassin est exclus des
  personnages
* A 4 joueurs : 2 cartes personnages sont écartées faces visibles et deux autres face cachée
* A 5 joueurs : 1 carte personnages sont écartées faces visibles et deux autres face cachée
* A 6 et 7 joueurs : 0 cartes personnages sont écartées faces visibles et deux autres face cachée

(Dans tous les cas le roi ne peut pas être parmi les cartes placées faces visibles, mais elle peut mise face cachée)

### Les subtilités

* Si personne ne prend le Roi, alors la couronne ne tourne pas entre les joueurs
* Il n'y a pas de maximum de pièces, il est possible que chacun des joueurs aient une infinité de pièces
* Un joueur qui a construit le nombre de quartiers nécessaire pour finir la partie peut, s'il en a la capacité,
  construire des quartiers jusqu'à la fin de son tour. (Par exemple avec l'Architecte)

## Les Bots :

Différents types de bots ont été implémenter, il y en a 5 et ils représentent chacun une stratégie différente.

### Bot

&nbsp; &nbsp; &nbsp; C'est l'implémentation du bot de base, sur lequel se base d'autres types de bot, il va
chercher à prendre le personnage qu'il pense être le meilleur par de nombreux facteurs mathématiques. De même pour
toutes
les décisions, il va essayer de prendre ce qui lui parait être le mieux en le déterminant à l'aide de calculs
mathématiques.

### Discreet Bot

&nbsp; &nbsp; &nbsp; Basé sur `Bot`, ce bot a pour but de ne pas se faire repérer des autres et essaye de rester
discret afin de gagner. Dès qu'il est en capacité de terminer la partie, il prend l'opportunité. Il construit ni des
quartiers rapportant trop de points pour pas se faire attaque par les autres joueurs, ni ceux n'en rapportant que peu
pour quand même essayer de marque un maximum de points.

### FearFul Bot

&nbsp; &nbsp; &nbsp; Basé sur `Bot`, ce bot ne construit que des quartiers rapportant au moins 3 points. Et s'il se sent
en danger, de destruction d'un de ses quartiers, d'être volé, d'être tué ou qu'on lui échange ses cartes tente de se
protéger.
À chaque tour, il va donc étudier son jeu et celui des autres joueurs pour choisir un personnage lui permettant de se
protéger s'il se sent en danger, avec un ordre de priorité sur les dangers, en premier celui de la destruction d'un
district, en second celui d'avoir ses cartes échanger, en troisième celui d'être volé et en dernier celui d'être tué.

### Agressive Bot

&nbsp; &nbsp; &nbsp; Basé sur `Bot`, ce bot a pour but d'être plus agressif que `Bot` sans pour autant l'être tout le
temps sans quoi il n'aurait qu'un extrême chance de gagner ne serais-ce qu'une partie.

### Richard Bot

&nbsp; &nbsp; &nbsp; Basé sur `Bot`, ce bot a pour but d'implémenter la stratégie de Richard poster sur
ce [forum](https://forum.trictrac.net/t/citadelles-charte-citadelles-de-base/509)

### Statistiques et analyse sur les bots :

## Choix d'implémentation :

### Logs

Pour les logs le choix a été fait d'avoir un `Level.INFO` pour tout ce qui est statistique sur un nombre de n partie
entre k bots de différents types et un `Level.FINE` pour toutes les actions de la partie
Cela permet de changer le level en fonction de quel paramètre est passé en lançant le programme et au besoin par exemple
avec l'ajout de nouvelle fonctionnalité, rajouter des Level de logger entre ces deux levels, inférieur au deux ou
supérieur au deux sans avoir à tout modifier.

### CSV contenant les statistiques

Le format du fichier CSV est le suivant :

* Chaque ligne donne les statistiques d'un bot pour 1000 parties jouées avec d'autres bots.
* Chaque colones donne dans l'ordre, le nombre de parties jouées par les bots, le nom des bots (ce qui permet aussi d'en
  déduire le type de bot). Les colonnes suivantes donnent les statistiques, de victoires, d'égalités et de défaite avec
  pour chacune 2 colones, d'abord le nombre de parties gagner puis le pourcentage que cela représente.

# 2. Architecture et qualité

## Lancement :

> **Note**  
> The terminal must use UTF8 encoding for emoji to be displayed correctly.  
> On Windows, you may need to run the following PowerShell command before launching the program
> ```pwsh
> [Console]::InputEncoding = [Console]::OutputEncoding = New-Object System.Text.UTF8Encoding
> ```

### Lancement avec Maven :

- #### Le programme principal

  ```
  mvn clean compile exec:java
  ```

- #### Les tests

  ```
  mvn clean test
  ```

- #### Générer un jar puis le lancer :

  ```
  mvn clean package
  java -cp ./target/citadels-1.0.jar fr.univ_cotedazur.polytech.si3.team_c.citadels.Game
  ```

------------------------

# 3. Processus




