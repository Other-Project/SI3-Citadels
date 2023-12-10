package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.characters.*;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
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
        this(new ArrayList<>());
    }

    public Game(List<Player> players) {
        deck = new Deck();
        playerList = players;
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
        LOGGER.log(Level.INFO, this::winnersDisplay);
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
        LOGGER.log(Level.INFO, "{0} have the crown", playerList.get(p).getName());
        for (int i = 0; i < playerList.size(); i++) {
            characterList.remove(playerList.get((p + i) % playerList.size()).pickCharacter(characterList));
        }
    }

    /**
     * Player chooses the action he wants to play during his turn
     */
    public void playerTurn(Player player) {
        LOGGER.log(Level.INFO, "{0}", player);
        if (player.getCharacter().orElseThrow() instanceof King) setCrown(playerList.indexOf(player));
        List<Action> actionList = new ArrayList<>(List.of(Action.INCOME, Action.DRAW, Action.BUILD));
        Action action;
        while (!actionList.isEmpty() && (action = player.nextAction(actionList)) != Action.NONE) {
            switch (action) {
                case DRAW -> {
                    LOGGER.log(Level.INFO, () -> player.getName() + " chooses to draw");
                    player.pickDistrictsFromDeck(deck.draw(player.numberOfDistrictsToDraw()))
                            .forEach(district -> LOGGER.log(Level.INFO, () -> player.getName() + " obtained " + district));
                    actionList.remove(Action.INCOME); // The player cannot gain any coins if he draws
                }
                case INCOME -> {
                    LOGGER.log(Level.INFO, () -> player.getName() + " chooses to gains 2 coins");
                    player.gainIncome();
                    actionList.remove(Action.DRAW); // The player cannot draw cards if he gets the income
                }
                case BUILD -> {
                    LOGGER.log(Level.INFO, () -> player.getName() + " chooses to build a district");
                    player.pickDistrictsToBuild()
                            .forEach(district -> LOGGER.log(Level.INFO, () -> player.getName() + " built " + district));
                }
                default ->
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
        int previousCrown = getCrown();
        characterSelectionTurn();
        List<Player> playOrder = playerList.stream().sorted(Comparator.comparing(player -> player.getCharacter().orElseThrow())).toList();
        boolean isEnd = false;
        for (Player player : playOrder) {
            player.getCharacter().ifPresent(c -> LOGGER.log(Level.INFO, "It is now {0}''s turn", c));
            playerTurn(player);
            if (end(player)) isEnd = true;
        }
        if (!(playerList.get(previousCrown).getCharacter().orElseThrow() instanceof King) && previousCrown == getCrown())
            setCrown((getCrown() + 1) % playerList.size());
        return isEnd;
    }

    /**
     * @return a tuple having as key the list of winning players and as value the score
     */
    public SimpleEntry<List<Player>, Integer> getWinners() {
        List<Player> winners = new ArrayList<>();
        int max = 0;
        for (Player player : playerList) {
            int score = player.getScore();
            if (score > max) {
                winners = new ArrayList<>(List.of(player));
                max = score;
            } else if (score == max) winners.add(player);
        }
        return new SimpleEntry<>(winners, max);
    }

    /**
     * @return the string for the winners display
     */
    public String winnersDisplay() {
        SimpleEntry<List<Player>, Integer> winners = getWinners();
        StringBuilder result = new StringBuilder();
        if (winners.getKey().size() == 1)
            result.append("The player ").append(winners.getKey().get(0).getName()).append(" won");
        else
            result.append("There is an equality between players : ")
                    .append(winners.getKey().stream().map(Player::getName).collect(Collectors.joining(", ")));
        return result.append(" with ").append(winners.getValue()).append(" points !").toString();
    }

    public static void main(String... args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "-%4$s- %5$s%6$s%n");
        new Game().start();
    }
}
