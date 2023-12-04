package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.characters.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Game {
    private static final Logger LOGGER = Logger.getGlobal();
    private List<Player> playerList;
    private List<Character> characterList;
    private Deck deck;

    public void start() {
        LOGGER.log(Level.INFO, "Game starts");
        deck = new Deck();
        playerList = new ArrayList<>(List.of(new Bot("bot1", 2, deck.draw(2))));
        while (!gameTurn()) {
        }
        LOGGER.log(Level.INFO, "Game ends");
    }

    /**
     * Reset the list of characters
     */
    public void setDefaultCharacterList() {
        characterList = new ArrayList<>(List.of(new Assassin(), new Thief(), new Magician(), new King(),
                new Bishop(), new Merchant(), new Architect(), new Warlord()));
    }

    /**
     * Each player selects a character in the character list
     */
    public void characterSelectionTurn() {
        setDefaultCharacterList();
        for (Player player : playerList) {
            characterList.remove(player.pickCharacter(this.characterList));
        }
    }

    /**
     * Player chooses the action he wants to play during his turn
     */
    public void playerTurn(Player player) {
        List<Action> actionList = new ArrayList<>(List.of(Action.INCOME, Action.DRAW, Action.BUILD));
        Action action;
        while (!actionList.isEmpty() && (action = player.nextAction(actionList)) != Action.NONE) {
            switch (action) {
                case DRAW:
                    LOGGER.log(Level.INFO, "The player chooses to draw");
                    player.pickDistrictsFromDeck(deck.draw(2), 1).forEach(player::addDistrictToHand);
                    actionList.remove(Action.INCOME); // The player cannot gain any coins if he draws
                    LOGGER.log(Level.INFO, player.getHandDistricts().toString());
                    break;
                case INCOME:
                    LOGGER.log(Level.INFO, "The player chooses to gains 2 coins");
                    player.gainCoins(2);
                    actionList.remove(Action.DRAW); // The player cannot draw cards if he gets the income
                    break;
                case BUILD:
                    LOGGER.log(Level.INFO, "The player builds a district");
                    for (District district : player.pickDistrictsToBuild(1)) {
                        player.buildDistrict(district);
                    }
                    LOGGER.log(Level.INFO, player.getBuiltDistricts().toString());
                    break;
                default:
                    break;
            }
            actionList.remove(action);
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
        boolean isEnd = false;
        for (Player player : playerList) {
            playerTurn(player);
            if (end(player)) isEnd = true;
        }
        return isEnd;
    }

    public static void main(String... args) {
        new Game().start();
    }
}
