package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.characters.*;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Game {
    private static final Logger LOGGER = Logger.getGlobal();
    private List<Player> playerList;

    private Deck deck;

    private int crown;
    private int currentTurn = 0;

    /**
     * The characters the player can interact with
     */
    private List<Character> charactersToInteractWith;
    private Player robber;
    private Character characterToRob;
    private Character characterToKill;
    private final Random random = new Random();
    private final List<District> discard;
    private final GameObserver gameStatus = new GameObserver(this);

    public GameObserver getGameObserver() {
        return gameStatus;
    }
    public Game() {
        this(Collections.emptyList());
    }

    public Game(int numberPlayers, Player... players) {
        this(List.of(players));
        int initLength = playerList.size();
        for (int i = 1; i <= numberPlayers - initLength; i++) {
            Bot bot = new Bot("bot" + i, 2, deck.draw(2));
            playerList.add(bot);
            bot.setGameStatus(gameStatus);
        }
    }

    public Game(List<Player> players) {
        deck = new Deck();
        playerList = new ArrayList<>(players);
        charactersToInteractWith = new ArrayList<>();
        for (Player p : playerList) {
            p.pickDistrictsFromDeck(deck.draw(2), 2);
            p.setGameStatus(gameStatus);
        }
        discard = new ArrayList<>();
    }

    public List<Player> getPlayerList() {
        return new ArrayList<>(playerList);
    }

    public Deck getDeck() {
        return deck;
    }

    /**
     * Add a player to the game
     */
    protected void addPlayer(Player player) {
        player.setGameStatus(gameStatus);
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
        if (playerList.isEmpty()) throw new IllegalStateException("No players in this game");
        LOGGER.log(Level.INFO, "Game starts");
        setCrown(random.nextInt(playerList.size()));
        for (int i = 1; true; i++) {
            LOGGER.log(Level.INFO, "===== Turn {0} =====", i);
            currentTurn = i;
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
        for (int i = 0; i < playerList.size(); i++) {
            var player = playerList.get((p + i) % playerList.size());
            var choosenCharacter = player.pickCharacter(characterList);
            LOGGER.log(Level.INFO, "{0} has chosen the {1}", new Object[]{player.getName(), choosenCharacter});
            characterList.remove(choosenCharacter);
        }
    }

    /**
     * Player chooses the action he wants to play during his turn
     */
    public void playerTurn(Player player) {
        LOGGER.info(player::toString);
        player.createActionSet();
        charactersToInteractWith.remove(player.getCharacter().orElseThrow());
        if (player.getCharacter().orElseThrow() instanceof King) setCrown(playerList.indexOf(player));
        if (player.getCharacter().orElseThrow().equals(characterToRob)) {
            LOGGER.log(Level.INFO, "{0} was robbed because he was the {1}", new Object[]{player.getName(), characterToRob});
            LOGGER.log(Level.INFO, "{0} gains {1} coins from {2} and has now {3} coins",
                    new Object[]{robber.getName(), player.getCoins(), player.getName(), player.getCoins() + robber.getCoins()});
            robber.gainCoins(player.getCoins());
            player.pay(player.getCoins());
            // The player who has been robbed give all his coins to the Thief
        }
        if (player.getCharacter().orElseThrow().equals(characterToKill)) {
            LOGGER.log(Level.INFO, "{0} was killed because he was the {1}", new Object[]{player.getName(), characterToKill});
            return;
        }
        Action startOfTurnAction = player.playStartOfTurnAction();
        if (startOfTurnAction != Action.NONE) {
            switch (startOfTurnAction) {
                case BEGIN_DRAW -> {
                    LOGGER.log(Level.INFO, () -> player.getName() + " draws 2 extra districts");
                    var drawnCards = deck.draw(2);
                    for (District district : drawnCards) {
                        player.addDistrictToHand(district);
                        LOGGER.log(Level.INFO, () -> player.getName() + " drew " + district);
                    }
                }
                case STARTUP_INCOME -> {
                    LOGGER.log(Level.INFO, "{0} earned a coin because he was the {1}", new Object[]{player.getName(), player.getCharacter().orElseThrow()});
                    player.gainCoins(1);
                }
                default ->
                        throw new UnsupportedOperationException("The start-of-turn action " + startOfTurnAction + " has not yet been implemented");
            }
        }
        Action action;
        while ((action = player.nextAction()) != Action.NONE) {
            LOGGER.log(Level.INFO, "{0} wants to {1}", new Object[]{player.getName(), action.getDescription()});
            action.doAction(player);
            player.removeAction(action);
            LOGGER.info(player::toString);
        }
    }

    /**
     * This method create the list of district that can be destroyed by the Warlord
     */
    protected Map<String, List<District>> getDistrictListToDestroyFrom() {
        Map<String, List<District>> districtListToDestroyFrom = getGameObserver().getBuiltDistrict();
        for (Player playerInList : playerList) {
            if (!playerInList.getCharacter().orElseThrow().canHaveADistrictDestroyed())
                districtListToDestroyFrom.remove(playerInList.getName());// Removes the player who can't get target by the Warlord
            else districtListToDestroyFrom.replace(playerInList.getName(),
                    districtListToDestroyFrom.get(playerInList.getName()).stream().filter(District::isDestructible).toList());
        }
        return districtListToDestroyFrom;
    }

    /**
     * This method links a player to his name
     *
     * @param playerName the name of the player
     * @return the player associated to playerName
     */
    private Player linkStringToPlayer(String playerName) {
        for (Player player : getPlayerList()) {
            if (playerName.equals(player.getName())) return player;
        }
        throw new NoSuchElementException("The player is not in the game");
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
        characterToRob = null;
        robber = null;
        characterToKill = null;
        charactersToInteractWith = defaultCharacterList();
        characterSelectionTurn();
        LOGGER.log(Level.INFO, "The game turn begins");
        List<Player> playOrder = playerList.stream().sorted(Comparator.comparing(player -> player.getCharacter().orElseThrow())).toList();
        boolean isEnd = false;
        for (Player player : playOrder) {
            player.getCharacter().ifPresent(c -> LOGGER.log(Level.INFO, "It is now {0}''s turn", c));
            playerTurn(player);
            if (end(player)) {
                if (!isEnd) player.endsGame();
                isEnd = true;
            }
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
            int score = player.getScore(currentTurn);
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
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$s] %5$s%6$s%n");
        new Game(2).start();
    }
}
