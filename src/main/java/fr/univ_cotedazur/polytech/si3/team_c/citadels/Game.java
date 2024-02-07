package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.players.*;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Game {
    private static final Logger LOGGER = Logger.getGlobal();
    private List<Player> playerList;
    private Deck deck;
    private int currentTurn = 0;
    private final Map<Action, Player> eventActions;
    private final CharacterManager characterManager;

    /**
     * The characters the player can interact with
     */
    private List<Character> charactersToInteractWith;
    private final Random random = new Random();

    public Game() {
        this(0);
    }

    public Game(Player... players) {
        this(players.length, players);
    }

    public Game(int numberPlayers, Player... players) {
        this(numberPlayers, null, players);
    }

    public Game(int numberPlayers, CharacterManager characterManager, Player... players) {
        this.characterManager = (characterManager == null) ? new CharacterManager(numberPlayers, random) : characterManager;
        deck = new Deck();
        playerList = new ArrayList<>(List.of(players));
        charactersToInteractWith = new ArrayList<>();
        eventActions = new EnumMap<>(Action.class);
        int initLength = playerList.size();
        for (int i = 1; i <= numberPlayers - initLength; i++) {
            Player bot = switch (random.nextInt(5)) {
                case 4 -> new RandomBot("randomBot" + i);
                case 3 -> new AgressiveBot("agressiveBot" + i);
                case 2 -> new FearFulBot("fearFullBot" + i);
                case 1 -> new DiscreetBot("discreetBot" + i);
                default -> new Bot("bot" + i);
            };
            playerList.add(bot);
            bot.setPlayers(() -> new ArrayList<>(playerList.stream().filter(player -> !player.equals(bot)).toList()));
        }
    }

    public List<Player> getPlayerList() {
        return new ArrayList<>(playerList);
    }

    public List<IPlayer> getIPlayerList() {
        return new ArrayList<>(playerList);
    }

    public Deck getDeck() {
        return deck;
    }

    /**
     * Add a player to the game
     */
    protected void addPlayer(Player player) {
        player.setPlayers(() -> new ArrayList<>(playerList.stream().filter(p -> !p.equals(player)).toList()));
        if (playerList == null) playerList = new ArrayList<>(List.of(player));
        else this.playerList.add(player);
    }

    /**
     * @return the number of districts to build to end the game
     */
    public int numberOfDistrictsToEnd() {
        if (playerList.size() == 3) return 10;
        return 8;
    }

    protected void setDefaultDeck() {
        this.deck = new Deck();
    }

    protected void setParametrisedDeck(List<District> cards) {
        this.deck = new Deck(cards);
    }

    public Player getCrown() {
        return playerList.stream().filter(Player::hasCrown).findFirst().orElse(playerList.get(0));
    }

    public int getCrownIndex() {
        return playerList.indexOf(getCrown());
    }

    public void setCrown(Player player) {
        player.setCrown();
    }

    private void setCrown(int player) {
        Player playerToCrown = playerList.get(player);
        setCrown(playerToCrown);
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public List<Character> getCharactersToInteractWith() {
        return new ArrayList<>(charactersToInteractWith);
    }

    public void registerPlayerForEventAction(Player player, Action eventAction) {
        eventActions.put(eventAction, player);
    }

    public void unregisterPlayerForEventAction(Player player, Action eventAction) {
        if (eventActions.get(eventAction) == player) eventActions.remove(eventAction);
    }

    public <T> void callEventAction(Action eventAction, Player caller, T param) {
        if (!eventActions.containsKey(eventAction)) return;
        String text = eventAction.doEventAction(this, caller, eventActions.get(eventAction), param);
        if (text != null)
            LOGGER.fine(text);
    }


    public void start() {
        playerList.forEach(Player::resetPlayer);
        playerInitialization();
        if (playerList.isEmpty()) throw new IllegalStateException("No players in this game");
        LOGGER.log(Level.FINE, "Game starts");
        setCrown(random.nextInt(playerList.size()));
        for (int i = 1; true; i++) {
            LOGGER.log(Level.FINE, "===== Turn {0} =====", i);
            currentTurn = i;
            if (gameTurn()) break;
        }
        LOGGER.log(Level.FINE, this::winnersDisplay);
        LOGGER.log(Level.FINE, "Game ends");
    }

    public void playerInitialization() {
        for (Player p : playerList) {
            p.pickDistrictsFromDeck(deck.draw(2), 2);
            p.gainCoins(2);
            p.setPlayers(() -> new ArrayList<>(playerList.stream().filter(player -> !player.equals(p)).toList()));
            p.setNumberOfDistrictsToEnd(numberOfDistrictsToEnd());
            p.resetCrown();
        }
    }

    /**
     * Each player selects a character in the character list
     */
    public void characterSelectionTurn() {
        characterManager.generate();
        LOGGER.fine(characterManager::toString);
        charactersToInteractWith = new ArrayList<>(characterManager.getAvailableCharacters());
        int crownedPlayerIndex = getCrownIndex();
        for (int i = 0; i < playerList.size(); i++) {
            int playerIndex = (crownedPlayerIndex + i) % playerList.size();
            var player = playerList.get(playerIndex);
            // Stores players who have already chosen their character
            List<IPlayer> beforePlayers;
            if (playerIndex < crownedPlayerIndex) {
                beforePlayers = new ArrayList<>(playerList.subList(crownedPlayerIndex, playerList.size()));
                beforePlayers.addAll(playerList.subList(0, playerIndex));
            } else beforePlayers = new ArrayList<>(playerList.subList(crownedPlayerIndex, playerIndex));
            Character choosenCharacter = player.pickCharacter(characterManager);
            player.setPossibleCharacters(beforePlayers, characterManager);
            characterManager.addPlayerCharacter(player, choosenCharacter);
            LOGGER.log(Level.FINE, "{0} has chosen the {1}", new Object[]{player.getName(), choosenCharacter});
            characterManager.getAvailableCharacters().remove(choosenCharacter);
        }
    }

    /**
     * Player chooses the action he wants to play during his turn
     */
    public void playerTurn(Player player) {
        LOGGER.fine(player::toString);
        player.createActionSet();
        charactersToInteractWith.remove(player.getCharacter().orElseThrow());
        if (player.sufferAction(SufferedActions.STOLEN)) {
            Player robber = (Player) player.actionCommitter(SufferedActions.STOLEN).orElseThrow();
            LOGGER.log(Level.FINE, "{0} was robbed because he was the {1}", new Object[]{player.getName(), player.getCharacter().orElseThrow()});
            LOGGER.log(Level.FINE, "{0} gains {1} coins from {2} and has now {3} coins",
                    new Object[]{robber.getName(), player.getCoins(), player.getName(), player.getCoins() + robber.getCoins()});

            robber.gainCoins(player.getCoins());
            player.pay(player.getCoins());
            // The player who has been robbed give all his coins to the Thief
        }
        if (player.sufferAction(SufferedActions.KILLED)) {
            LOGGER.log(Level.FINE, "{0} was killed because he was the {1}", new Object[]{player.getName(), player.getCharacter().orElseThrow()});
            return;
        }
        Action startOfTurnAction = player.playStartOfTurnAction();
        if (startOfTurnAction != Action.NONE)
            startOfTurnAction.doAction(this, player);

        Action action;
        while ((action = player.nextAction()) != Action.NONE) {
            LOGGER.log(Level.FINE, "{0} wants to {1}", new Object[]{player.getName(), action.getDescription()});
            String message = action.doAction(this, player);
            LOGGER.fine(message);
            player.removeAction(action);
            LOGGER.fine(player::toString);
        }
    }

    /**
     * Defines a round to play in the game
     */
    public boolean gameTurn() {

        Player previousCrownedPlayer = getCrown();
        int previousCrownedPlayerIndex = getCrownIndex();
        characterSelectionTurn();
        LOGGER.log(Level.FINE, "The game turn begins");
        boolean isEnd = false;
        for (Character character : characterManager.charactersList()) {
            if (characterManager.characterIsChosen(character)) {
                Player player = characterManager.getPlayer(character);
                LOGGER.log(Level.FINE, "It is now {0}''s turn", character);
                playerTurn(player);
                if (!isEnd && player.endsGame())
                    isEnd = true;
            } else charactersToInteractWith.remove(character);
        }
        Optional<Character> characterKing = playerList.get(previousCrownedPlayerIndex).getCharacter();
        if (getCrown() == previousCrownedPlayer && characterKing.isPresent() && !characterKing.get().startTurnAction().equals(Action.GET_CROWN))
            setCrown((previousCrownedPlayerIndex + 1) % playerList.size());
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

    /**
     * Perform an action on a character
     *
     * @param character the character who will suffer the action
     * @param committer the player who commits the action
     * @param action    the committed action
     */
    public void performActionOnCharacter(Character character, IPlayer committer, SufferedActions action) {
        if (characterManager.characterIsChosen(character))
            characterManager.getPlayer(character).addSufferedAction(action, committer);
    }
}
