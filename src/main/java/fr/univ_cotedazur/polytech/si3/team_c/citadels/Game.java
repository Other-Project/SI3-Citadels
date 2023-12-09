package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.characters.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Game {
    private static final Logger LOGGER = Logger.getGlobal();
    private List<Player> playerList;
    private List<Character> characterList;
    private Deck deck;

    public List<Player> getPlayerList() {
        return playerList;
    }

    /**
     * Add a player to the game
     */
    protected void addPlayer(Player player) {
        if (playerList == null) playerList = new ArrayList<>(List.of(player));
        else this.playerList.add(player);
    }

    protected void setDefaultDeck() {
        this.deck = new Deck();
    }

    public void start() {
        LOGGER.log(Level.INFO, "Game starts");
        setDefaultDeck();
        playerList = new ArrayList<>(List.of(new Bot("bot1", 2, deck.draw(2))));
        for (int i = 1; true; i++) {
            LOGGER.log(Level.INFO, "Turn {0}", i);
            if (gameTurn()) break;
        }
        Player winner = getWinner();
        LOGGER.log(Level.INFO, "The player {0} won with {1} points !", new Object[]{winner.getName(), winner.getScore()});
        LOGGER.log(Level.INFO, "Game ends");
    }

    /**
     * Reset the list of characters
     */
    public List<Character> defaultCharacterList() {
        return new ArrayList<>(List.of(new Assassin(), new Thief(), new Magician(), new King(),
                new Bishop(), new Merchant(), new Architect(), new Warlord()));
    }

    /**
     * Each player selects a character in the character list
     */
    public void characterSelectionTurn() {
        LOGGER.log(Level.INFO, "Beginning of the character selection turn");
        characterList = defaultCharacterList();
        for (Player player : playerList) {
            Character pickedCharacter = player.pickCharacter(this.characterList);
            LOGGER.log(Level.INFO, "{0} picked the {1}", new Object[]{player.getName(), pickedCharacter});
            characterList.remove(pickedCharacter);
        }
    }

    /**
     * Player chooses the action he wants to play during his turn
     */
    public void playerTurn(Player player) {
        LOGGER.log(Level.INFO, "{0}", player);
        List<Action> actionList = new ArrayList<>(List.of(Action.INCOME, Action.DRAW, Action.BUILD));
        if (player.getCharacter().orElseThrow().getColor() != Colors.NONE) {
            actionList.add(Action.SPECIAL_INCOME); // if the character isn't white, he can claim a special income
        }
        Action action;
        while (!actionList.isEmpty() && (action = player.nextAction(actionList)) != Action.NONE) {
            switch (action) {
                case DRAW -> {
                    LOGGER.log(Level.INFO, () -> player.getName() + " chooses to draw");
                    player.pickDistrictsFromDeck(deck.draw(player.numberOfDistrictsToDraw()))
                            .forEach(district -> LOGGER.log(Level.INFO, () -> player.getName() + " obtained " + district));
                    actionList.remove(Action.INCOME); // The player cannot gain any coins if he draws
                    break;
                case INCOME -> {
                    LOGGER.log(Level.INFO, () -> player.getName() + " chooses to gains 2 coins");
                    player.gainIncome();
                    actionList.remove(Action.DRAW); // The player cannot draw cards if he gets the income
                }
                case BUILD -> {
                    LOGGER.log(Level.INFO, () -> player.getName() + " chooses to build a district");
                    player.pickDistrictsToBuild()
                            .forEach(district -> LOGGER.log(Level.INFO, () -> player.getName() + " built " + district));
                    break;
                case SPECIAL_INCOME -> {
                    LOGGER.log(Level.INFO, () -> player.getName() + " claims his special income");
                    int coinsToClaim = 0;
                    for (District district : player.getBuiltDistricts()) {
                        if (district.getColor() == player.getCharacter().orElseThrow().getColor()) coinsToClaim++;
                    }
                    LOGGER.log(Level.INFO, "{0} gets {1} coins", new Object[]{player.getName(), Integer.toString(coinsToClaim)});
                    break;
                }
                default -> {
                        throw new UnsupportedOperationException("The action " + action + " has not yet been implemented");
            }
            actionList.remove(action);
            LOGGER.log(Level.INFO, "{0}", player);
        }
    }

    /**
     * The method which checks if the game must end according to the number of districts built for the player
     */
    public boolean end(Player player) {
        return player.getBuiltDistricts().size() >= 8;
    }

    /**
     * Defines a round to play in the game
     */
    public boolean gameTurn() {
        characterSelectionTurn();
        playerList.sort(Comparator.comparing(player -> player.getCharacter().orElseThrow()));
        LOGGER.log(Level.INFO, "The Game turn begins");
        boolean isEnd = false;
        for (Player player : playerList) {
            player.getCharacter().ifPresent(c -> LOGGER.log(Level.INFO, "It is now {0}''s turn", c));
            playerTurn(player);
            if (end(player)) isEnd = true;
        }
        return isEnd;
    }

    /**
     * @return a map of all the scores with the player associated with it
     */
    public Map<Player, Integer> getScores() {
        return playerList.stream()
                .collect(Collectors.toMap(p -> p, Player::getScore));
    }

    /**
     * @return the winner of the game
     */
    public Player getWinner() {
        return getScores().entrySet().stream()
                .max(Comparator.comparingInt(Map.Entry::getValue)).orElseThrow().getKey();
    }

    public static void main(String... args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "-%4$s- %5$s%6$s%n");
        new Game().start();
    }
}
