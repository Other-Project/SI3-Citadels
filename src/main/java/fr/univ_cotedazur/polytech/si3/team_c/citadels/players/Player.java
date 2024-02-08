package fr.univ_cotedazur.polytech.si3.team_c.citadels.players;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Character;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.*;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A player (human or robot)
 *
 * @author Team C
 */
public abstract class Player implements IPlayer {
    private static final int INCOME = 2;
    private static final int NUMBER_OF_DISTRICTS_TO_DRAW = 2;
    private static final int NUMBER_OF_DISTRICTS_TO_KEEP = 1;
    private final String name;
    private boolean gameEnder = false;
    private int coins;
    private final Map<Integer, List<District>> builtDistricts;
    private Map<SufferedActions, IPlayer> sufferedActions;
    private final ArrayList<District> handDistricts;
    private Character character;

    private Set<Action> actionSet;

    private Callable<List<IPlayer>> players;
    private boolean hasCrown;

    private int numberOfDistrictsToEnd = 8;

    protected Player(String name, int coins, List<District> districts) {
        this.name = name;
        this.coins = coins;
        handDistricts = new ArrayList<>(districts);
        actionSet = new HashSet<>(List.of(Action.INCOME, Action.DRAW, Action.BUILD));
        builtDistricts = new HashMap<>();
        sufferedActions = new EnumMap<>(SufferedActions.class);
        players = Collections::emptyList;
        hasCrown = false;
    }

    /**
     * Gets the name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the amount of coins the player have
     */
    public int getCoins() {
        return coins;
    }

    @Override
    public String toString() {
        return getName() + " (" + getCoins() + " coins) " + getHandDistricts() + " :" + getBuiltDistricts();
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Player player && Objects.equals(name, player.name);
    }

    /**
     * Tries to pay a certain amount of money
     *
     * @param price The price to pay
     * @return True if the player had enough money
     */
    public boolean pay(int price) {
        if (coins < price || price < 0) return false;
        coins -= price;
        return true;
    }

    /**
     * Claims player's income
     *
     * @return Player's income
     */
    public int gainIncome() {
        int income = INCOME;
        gainCoins(income);
        return income;
    }

    /**
     * If the player is a character of color, he can claim a special income depending on the number of districts built of the same color
     *
     * @return The number of coins the player claimed
     */
    public int gainSpecialIncome() {
        int income = quantityOfColorBuilt(getCharacter().orElseThrow().getColor());
        gainCoins(income);
        return income;
    }

    /**
     * Ask the player which card to discard
     *
     * @return The card that the player chose to discard
     */
    public abstract District cardToDiscard();

    /**
     * Add some coins to the player "wallet"
     *
     * @param quantity Quantity of coins to add
     */
    public void gainCoins(int quantity) {
        coins += quantity;
    }

    /**
     * The quantity of built district of a given color
     *
     * @param color The color that the districts need to match
     */
    protected int quantityOfColorBuilt(Colors color) {
        return (int) getBuiltDistricts().stream().filter(district -> district.matchColor(color)).count();
    }

    /**
     * Gets all the districts that the player built (and that haven't been destroyed)
     */
    public List<District> getBuiltDistricts() {
        return new ArrayList<>(builtDistricts.values().stream().flatMap(List::stream).toList()) {
            @Override
            public String toString() {
                if (isEmpty()) return "\n\tNo district built";
                StringBuilder stringBuilder = new StringBuilder();
                for (District d : this) stringBuilder.append("\n\t").append(d);
                return stringBuilder.toString();
            }
        };
    }

    /**
     * Gets all the destroyable districts that the player built
     */
    public List<District> getDestroyableDistricts() {
        return character != null && character.canHaveADistrictDestroyed() && getBuiltDistricts().size() < getNumberOfDistrictsToEnd() ?
                getBuiltDistricts().stream().filter(District::isDestructible).toList() : Collections.emptyList();
    }

    /**
     * Gets the amount of cards the player have in hand
     */
    public int getHandSize() {
        return handDistricts.size();
    }

    /**
     * Gets all the districts in the hand of the player (but not built for now)
     */
    public List<District> getHandDistricts() {
        return new ArrayList<>(handDistricts);
    }

    /**
     * Gets the chosen character (can be empty if the player hasn't chosen yet)
     */
    public Optional<Character> getCharacter() {
        return Optional.ofNullable(character);
    }

    /**
     * Sets the character of the player
     *
     * @param character The character to set
     */
    protected void setCharacter(Character character) {
        this.character = character;
    }

    /**
     * Tries to build a district
     *
     * @param district The district to build
     * @return True if the district have been successfully built
     */
    public boolean buildDistrict(District district, int turn) {
        if (!handDistricts.contains(district)) return false;
        if (getBuiltDistricts().contains(district)) return false;
        if (!pay(district.getCost())) return false;
        handDistricts.remove(district);
        builtDistricts.putIfAbsent(turn, new ArrayList<>());
        builtDistricts.get(turn).add(district);
        return true;
    }

    /**
     * The player wants to destroy a district
     *
     * @param players List of players whose districts can be destroyed
     * @return the district to be destroyed
     */
    public abstract SimpleEntry<IPlayer, District> destroyDistrict(List<IPlayer> players);

    /**
     * The player removes a district from his built district
     *
     * @param district the district to remove
     */
    public void removeDistrictFromDistrictBuilt(District district) {
        for (Map.Entry<Integer, List<District>> mapEntry : builtDistricts.entrySet()) {
            if (mapEntry.getValue().contains(district)) {
                List<District> newDistrictList = mapEntry.getValue();
                newDistrictList.remove(district);
                mapEntry.setValue(newDistrictList);
                break;
            }
        }
    }

    /**
     * Adds a district to the player's hand
     *
     * @param district The district to add
     */
    public void addDistrictToHand(District district) {
        handDistricts.add(district);
    }

    /**
     * Asks the player to choose a character
     *
     * @param characterManager to access the available characters and the shown characters
     * @return The character that has been chosen
     */
    public Character pickCharacter(CharacterManager characterManager) {
        sufferedActions = new EnumMap<>(SufferedActions.class);
        return null;
    }

    /**
     * Ask the player which action should be done (will be asked until there's no more actions to do)
     *
     * @return The action chosen by the player to be done
     */
    public Action nextAction() {
        return nextAction(getActionSet());
    }

    /**
     * Ask the player which action should be done (will be asked until there's no more actions to do). The action will be chosen in the Set in entry
     *
     * @param actions Set of actions in which will be chosen the action to do
     */
    public abstract Action nextAction(Set<Action> actions);

    /**
     * Asks the player to choose districts among the drawn ones
     *
     * @param drawnCards The list of the drawn districts
     * @return The chosen districts
     */
    public List<District> pickDistrictsFromDeck(List<District> drawnCards) {
        return pickDistrictsFromDeck(drawnCards, numberOfDistrictsToKeep());
    }

    /**
     * Asks the player to choose n districts from the districts that has been drawn
     *
     * @param drawnCards     The list of the drawn districts
     * @param amountToChoose The amount of districts to choose
     * @return The chosen districts
     */
    public abstract List<District> pickDistrictsFromDeck(List<District> drawnCards, int amountToChoose);

    /**
     * Asks the player to choose district(s) from his hand to be build
     *
     * @return The chosen districts
     */
    public List<District> pickDistrictsToBuild(int turn) {
        return pickDistrictsToBuild(getCharacter().orElseThrow().numberOfDistrictToBuild(), turn);
    }

    /**
     * Asks the player to choose district(s) from his hand to be build
     *
     * @param maxAmountToChoose The max amount of districts that can be built
     * @param turn              the current turn of the Game
     * @return The chosen districts
     */
    protected abstract List<District> pickDistrictsToBuild(int maxAmountToChoose, int turn);

    /**
     * @return the current score given by the districts of the player
     */
    public int getDistrictsScore() {
        return getBuiltDistricts().stream().mapToInt(District::getPoint).sum();
    }

    /**
     * @param lastTurn the number of the last turn
     * @return true if the district was built in the last turn
     */
    private boolean builtInTheLastTurn(District district, int lastTurn) {
        if (builtDistricts.containsKey(lastTurn)) return builtDistricts.get(lastTurn).contains(district);
        return false;
    }

    /**
     * Check if the player has all the colors
     *
     * @param lastTurn the number of the last turn
     **/
    public boolean allColorsInDistricts(int lastTurn) {
        return allColorsInDistricts(lastTurn, getBuiltDistricts(), Collections.emptySet(), 0);
    }

    private boolean allColorsInDistricts(int lastTurn, List<District> builtDistricts, Set<Colors> selectedColors, int index) {
        if (selectedColors.containsAll(Arrays.stream(Colors.values()).filter(Predicate.not(Colors.NONE::equals)).toList()))
            return true;
        if (index >= builtDistricts.size()) return false;
        District district = builtDistricts.get(index);
        List<Colors> colors = district.bonusColors(builtInTheLastTurn(district, lastTurn));
        return colors.stream().anyMatch(color -> {
            var colorsToTry = new HashSet<>(selectedColors);
            colorsToTry.add(color);
            return allColorsInDistricts(lastTurn, builtDistricts, colorsToTry, index + 1);
        });
    }

    /**
     * @param lastTurn the number of the last turn
     * @return the current score of the player
     */
    public int getScore(int lastTurn) {
        int score = getDistrictsScore();
        if (allColorsInDistricts(lastTurn)) score += 3;
        if (isGameEnder()) score += 4;
        else if (getBuiltDistricts().size() >= getNumberOfDistrictsToEnd()) score += 2;
        return score;
    }

    /**
     * @param cardSelector the method to select the maximum appropriate district value
     * @param defaultValue the default value of the searched value
     * @return the maximum selected district value for the player
     */
    private int maxBuiltDistrictValue(Function<District, Optional<Integer>> cardSelector, int defaultValue) {
        return getBuiltDistricts().stream()
                .map(cardSelector)
                .flatMap(Optional::stream)
                .max(Integer::compare)
                .orElse(defaultValue);
    }

    /**
     * @return the number of districts to draw for the player
     */
    public int numberOfDistrictsToDraw() {
        return maxBuiltDistrictValue(District::numberOfDistrictsToDraw, NUMBER_OF_DISTRICTS_TO_DRAW);
    }

    /**
     * @return the number of districts to keep for the player
     */
    public int numberOfDistrictsToKeep() {
        return maxBuiltDistrictValue(District::numberOfDistrictsToKeep, NUMBER_OF_DISTRICTS_TO_KEEP);
    }

    /**
     * Creates a list of possible actions for a player, depending on the chosen character and the built districts.
     */
    public Set<Action> createActionSet() {
        actionSet = new HashSet<>(List.of(Action.INCOME, Action.DRAW, Action.BUILD));
        getBuiltDistricts().forEach(district -> actionSet.addAll(district.getAction())); // Add the special action of each district if it has one
        actionSet.addAll(character.getAction()); // Add the special actions of the character
        return actionSet;
    }

    /**
     * Set the actionSet of the player
     *
     * @param actionSet Set of actions to put in the actionSet of the player
     */
    public void setActionSet(Set<Action> actionSet) {
        this.actionSet = actionSet;
    }

    public Set<Action> getActionSet() {
        return actionSet;
    }

    /**
     * Remove an action and it's incompatibilities
     *
     * @param action The action to remove
     * @return True if the action has been deleted successfully
     */
    public boolean removeAction(Action action) {
        boolean success = actionSet.remove(action);
        if (success) actionSet.removeAll(action.getIncompatibleActions());
        return success;
    }

    /**
     * Checks whether the player has fulfilled the end-of-game conditions
     * Note: Must not be called if the game has already been ended by another player
     */
    public boolean endsGame() {
        if (getBuiltDistricts().size() >= getNumberOfDistrictsToEnd()) {
            gameEnder = true;
            return true;
        }
        return false;
    }

    /**
     * @return true if the Player is the game ender
     */
    public boolean isGameEnder() {
        return gameEnder;
    }

    /**
     * Ask the player to choose a character to rob
     *
     * @param characterList the list of character the player can rob
     */
    public abstract Character chooseCharacterToRob(List<Character> characterList);

    /**
     * Ask the player to choose a character to kill
     *
     * @param characterList the list of character the player can kill
     * @return the player to kill
     */
    public abstract Character chooseCharacterToKill(List<Character> characterList);

    /**
     * Asks the player to choose another player with whom to exchange his hand.
     *
     * @param playerList List of players he can exchange with
     * @return The player chosen for the exchange (Or empty if he doesn't want to make an exchange)
     */
    public abstract IPlayer playerToExchangeCards(List<IPlayer> playerList);

    /**
     * Ask the player to choose cards to exchange with the deck
     *
     * @return The list of cards he wants to exchange with the deck
     */
    public abstract List<District> chooseCardsToExchangeWithDeck();

    /**
     * Remove the cards of the player hand
     *
     * @param cards List of cards to remove of the hand
     */
    public void removeFromHand(List<District> cards) {
        handDistricts.removeAll(cards);
    }

    /**
     * The player plays his star-of-turn action
     */
    public Action playStartOfTurnAction() {
        return getCharacter().orElseThrow().startTurnAction();
    }

    /**
     * Does the player want to recover a district that has just been destroyed
     *
     * @param district the destroyed district
     * @return true if the player wants to take the district
     **/
    public abstract boolean wantsToTakeADestroyedDistrict(District district);

    public abstract void setPossibleCharacters(List<IPlayer> beforePlayers, CharacterManager characterManager);

    /**
     * Adds the action committed by a player on the player
     *
     * @param action the suffered action
     * @param player the player who commits the action
     **/
    public void addSufferedAction(SufferedActions action, IPlayer player) {
        sufferedActions.put(action, player);
    }

    /**
     * Tests if the player suffers an action
     *
     * @param action the tested action
     * @return true if the player suffer the action
     */
    public boolean sufferAction(SufferedActions action) {
        return sufferedActions.containsKey(action);
    }

    /**
     * Return the committer of the action if the player suffers the action
     *
     * @param action the committed action
     * @return the committer of the action
     */
    public Optional<IPlayer> actionCommitter(SufferedActions action) {
        return sufferAction(action) ? Optional.of(sufferedActions.get(action)) : Optional.empty();
    }

    public List<IPlayer> getPlayers() {
        try {
            List<IPlayer> test = players.call();
            return test.stream().filter(iPlayer -> !iPlayer.equals(this)).toList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<IPlayer> getPlayersWithYou() {
        try {
            return players.call();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public void setPlayers(Callable<List<IPlayer>> players) {
        this.players = players;
    }

    /**
     * Sets the number of district to end the game
     *
     * @param numberOfDistrictsToEnd the number given by the game
     */
    public void setNumberOfDistrictsToEnd(int numberOfDistrictsToEnd) {
        this.numberOfDistrictsToEnd = numberOfDistrictsToEnd;
    }

    /**
     * @return the number of districts to end the game
     */
    public int getNumberOfDistrictsToEnd() {
        return numberOfDistrictsToEnd;
    }

    public void resetPlayer() {
        this.coins = 0;
        this.handDistricts.clear();
        this.builtDistricts.clear();
        this.sufferedActions.clear();
        this.character = null;
        this.actionSet.clear();
        this.gameEnder = false;
    }

    public void setCrown() {
        hasCrown = true;
    }

    public void resetCrown() {
        hasCrown = false;
    }

    public boolean hasCrown() {
        return hasCrown;
    }
}
