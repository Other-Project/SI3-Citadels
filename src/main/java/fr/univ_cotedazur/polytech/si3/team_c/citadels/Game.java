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
                case GET_CROWN -> {
                    LOGGER.log(Level.INFO, "{0} got the crown because he was the {1}", new Object[]{player.getName(), player.getCharacter().orElseThrow()});
                    setCrown(playerList.indexOf(player));
                }
                default ->
                        throw new UnsupportedOperationException("The start-of-turn action " + startOfTurnAction + " has not yet been implemented");
            }
        }
        Action action;
        while ((action = player.nextAction()) != Action.NONE) {
            switch (action) {
                case DRAW -> {
                    LOGGER.info(player.getName() + " draws");
                    var drawnCard = deck.draw(player.numberOfDistrictsToDraw());
                    LOGGER.log(Level.INFO, "{0} drew {1}", new Object[]{player.getName(), drawnCard});
                    List<District> districtsToKeep = player.pickDistrictsFromDeck(drawnCard);
                    drawnCard.removeAll(districtsToKeep);
                    LOGGER.log(Level.INFO, "{0} kept {1}", new Object[]{player.getName(), districtsToKeep});
                    discard.addAll(drawnCard); // We add to the discard the districts that the player doesn't want to keep
                    player.removeAction(Action.INCOME); // The player cannot gain any coins if he draws
                }
                case INCOME -> {
                    LOGGER.info(player.getName() + " claims his income");
                    LOGGER.log(Level.INFO, "{0} got {1} coins", new Object[]{player.getName(), player.gainIncome()});
                    player.removeAction(Action.DRAW); // The player cannot draw cards if he gets the income
                }
                case BUILD -> {
                    LOGGER.info(player.getName() + " chooses to build a district");
                    player.pickDistrictsToBuild(currentTurn)
                            .forEach(district -> LOGGER.log(Level.INFO, "{0} built {1}", new Object[]{player.getName(), district}));
                }
                case SPECIAL_INCOME -> {
                    LOGGER.info(player.getName() + " claims his special income");
                    int claimedCoins = player.gainSpecialIncome();
                    LOGGER.log(Level.INFO, "{0} got {1} coins", new Object[]{player.getName(), Integer.toString(claimedCoins)});
                }
                case TAKE_THREE -> {
                    LOGGER.log(Level.INFO, () -> player.getName() + " pays 3 coins and draw 3 cards");
                    List<District> drawnCards = deck.draw(3);
                    player.pay(3);
                    drawnCards.forEach(player::addDistrictToHand);
                    LOGGER.log(Level.INFO, "{0} payed 3 coins in order to received: {1}", new Object[]{player.getName(), drawnCards});
                }
                case DISCARD -> {
                    District card = player.cardToDiscard();
                    LOGGER.log(Level.INFO, () -> player.getName() + " discards one card and receives one coin");
                    discard.add(card);
                    player.getHandDistricts().remove(card); // If no card chose the player would not be able to do this action
                    player.gainCoins(1);
                    LOGGER.log(Level.INFO, "{0} discarded {1} in order to received one coin", new Object[]{player.getName(), card});
                }
                case STEAL -> {
                    if (charactersToInteractWith.isEmpty()) return;
                    LOGGER.info(player.getName() + " wants to steal a character");
                    characterToRob = player.chooseCharacterToRob(charactersToInteractWith);
                    LOGGER.log(Level.INFO, "{0} tries to steal the {1}", new Object[]{player.getName(), characterToRob});
                    robber = player;
                }
                case KILL -> {
                    if (charactersToInteractWith.isEmpty()) return;
                    characterToKill = player.chooseCharacterToKill(charactersToInteractWith);
                    LOGGER.log(Level.INFO, "{0} kills the {1}", new Object[]{player.getName(), characterToKill});
                }
                case EXCHANGE_DECK -> {
                    List<District> cardsToExchange = player.chooseCardsToExchangeWithDeck();
                    assert (!cardsToExchange.isEmpty());
                    discard.addAll(cardsToExchange);
                    player.removeFromHand(cardsToExchange);
                    List<District> cards = deck.draw(cardsToExchange.size());
                    cards.forEach(player::addDistrictToHand);
                    LOGGER.log(Level.INFO, "{0} exchanges some cards {1} with the deck, he got {2}", new Object[]{player.getName(), cardsToExchange, cards});
                    player.removeAction(Action.EXCHANGE_PLAYER);// The player cannot exchange with another player if he exchanged some cards with the deck
                }
                case EXCHANGE_PLAYER -> {
                    Player playerToExchangeCards = player.playerToExchangeCards(getPlayerList());
                    List<District> hand1 = player.getHandDistricts();
                    List<District> handExchange = playerToExchangeCards.getHandDistricts();
                    player.removeFromHand(hand1);
                    playerToExchangeCards.removeFromHand(handExchange);
                    hand1.forEach(playerToExchangeCards::addDistrictToHand);
                    handExchange.forEach(player::addDistrictToHand);
                    LOGGER.log(Level.INFO, "{0} exchanges his cards {1} with {2}, he got {3}", new Object[]{player.getName(), hand1, playerToExchangeCards, handExchange});
                    player.removeAction(Action.EXCHANGE_DECK);// The player cannot exchange with the deck if he exchanged cards with another player
                }
                case DESTROY -> {
                    Map<String, List<District>> districtListToDestroyFrom = getDistrictListToDestroyFrom();
                    SimpleEntry<String, District> districtToDestroy = player.destroyDistrict(districtListToDestroyFrom).orElseThrow();
                    Player playerToTarget = linkStringToPlayer(districtToDestroy.getKey());
                    playerToTarget.removeDistrictFromDistrictBuilt(districtToDestroy.getValue());
                    player.pay(districtToDestroy.getValue().getCost() - 1);
                    LOGGER.log(Level.INFO, "{0} destroys the {1} of {2}\n{0} has now {3} coins", new Object[]{
                            player.getName(), districtToDestroy.getValue(), playerToTarget.getName(), player.getCoins()});
                }
                default ->
                        throw new UnsupportedOperationException("The action " + action + " has not yet been implemented");
            }
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
            if (!playerInList.getCharacter().orElseThrow().canHaveADistrictDestroyed() || end(playerInList))
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
        deck.addAll(discard); // We add at the bottom of the deck the discarded cards
        discard.clear(); // Reset of the discard
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
