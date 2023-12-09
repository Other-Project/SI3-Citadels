package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.characters.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Game {
    private static final Logger LOGGER = Logger.getGlobal();
    private List<Player> playerList;

    private Deck deck;

    private int crown;
    private final Random random = new Random();

    public Game() {
        this(0);
    }

    public Game(int players) {
        deck = new Deck();
        playerList = new ArrayList<>();
        for (int i = 1; i <= players; i++) playerList.add(new Bot("bot" + i, 2, deck.draw(2)));
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public Deck getDeck() {
        return deck;
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

    public int getCrown() {
        return crown;
    }

    public void setCrown(int player) {
        crown = player;
    }


    public void start() {
        LOGGER.log(Level.INFO, "Game starts");
        setCrown(random.nextInt(playerList.size()));
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
        List<Character> characterList = defaultCharacterList();
        int p = getCrown();
        for (int i = 0; i < playerList.size(); i++) {
            characterList.remove(playerList.get((p + i) % playerList.size()).pickCharacter(characterList));
        }
    }

    /**
     * Player chooses the action he wants to play during his turn
     */
    public void playerTurn(Player player) {
        LOGGER.log(Level.INFO, "{0}", player);
        if (player.getCharacter().orElseThrow().getClass() == King.class) setCrown(playerList.indexOf(player));
        List<Action> actionList = new ArrayList<>(List.of(Action.INCOME, Action.DRAW, Action.BUILD));
        Action action;
        while (!actionList.isEmpty() && (action = player.nextAction(actionList)) != Action.NONE) {
            switch (action) {
                case DRAW:
                    LOGGER.log(Level.INFO, () -> player.getName() + " chooses to draw");
                    player.pickDistrictsFromDeck(deck.draw(2), 1)
                            .forEach(district -> LOGGER.log(Level.INFO, () -> player.getName() + " obtained " + district));
                    actionList.remove(Action.INCOME); // The player cannot gain any coins if he draws

                    break;
                case INCOME:
                    LOGGER.log(Level.INFO, () -> player.getName() + " chooses to gains 2 coins");
                    player.gainCoins(2);
                    actionList.remove(Action.DRAW); // The player cannot draw cards if he gets the income
                    break;
                case BUILD:
                    LOGGER.log(Level.INFO, () -> player.getName() + " chooses to build a district");
                    player.pickDistrictsToBuild(1)
                            .forEach(district -> LOGGER.log(Level.INFO, () -> player.getName() + " built " + district));
                    break;
                default:
                    break;
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
        int previousCrown = getCrown();
        characterSelectionTurn();
        List<Player> playOrder = playerList.stream().sorted(Comparator.comparing(player -> player.getCharacter().orElseThrow())).toList();
        boolean isEnd = false;
        for (Player player : playOrder) {
            player.getCharacter().ifPresent(c -> LOGGER.log(Level.INFO, "It is now {0}''s turn", c));
            playerTurn(player);
            if (end(player)) isEnd = true;
        }
        if (!playerList.get(previousCrown).getCharacter().orElseThrow().getClass().equals(King.class) && previousCrown == getCrown())
            setCrown((getCrown() + 1) % playerList.size());
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
