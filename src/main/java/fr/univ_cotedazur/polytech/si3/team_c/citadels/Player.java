package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import java.util.*;
import java.util.function.Function;

/**
 * A player (human or robot)
 *
 * @author Team C
 */
public abstract class Player {
    private static final int INCOME = 2;
    private static final int NUMBER_OF_DISTRICTS_TO_DRAW = 2;
    private static final int NUMBER_OF_DISTRICTS_TO_KEEP = 1;
    private static final int NUMBER_OF_DISTRICTS_TO_BUILD = 1;
    private final String name;
    private boolean gameEnder = false;
    private int coins;
    private final Map<Integer, List<District>> builtDistricts;
    private final ArrayList<District> handDistricts;
    private Character character;

    private Set<Action> actionSet;


    protected Player(String name, int coins, List<District> districts) {
        this.name = name;
        this.coins = coins;
        handDistricts = new ArrayList<>(districts);
        actionSet = new HashSet<>(List.of(Action.INCOME, Action.DRAW, Action.BUILD));
        builtDistricts = new HashMap<>();
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
     * Add some coins to the player "wallet"
     *
     * @param quantity Quantity of coins to add
     */
    protected void gainCoins(int quantity) {
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
                StringBuilder stringBuilder = new StringBuilder();
                for (District d : this) stringBuilder.append("\n\t").append(d);
                return stringBuilder.toString();
            }
        };
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
    protected boolean buildDistrict(District district, int turn) {
        if (!handDistricts.contains(district)) return false;
        if (!pay(district.getCost())) return false;
        handDistricts.remove(district);
        builtDistricts.putIfAbsent(turn, new ArrayList<>());
        builtDistricts.get(turn).add(district);
        return true;
    }

    /**
     * Adds a district to the player's hand
     *
     * @param district The district to add
     */
    protected void addDistrictToHand(District district) {
        handDistricts.add(district);
    }

    /**
     * Asks the player to choose a character
     *
     * @param availableCharacters A list of the characters available
     * @return The character that has been chosen
     */
    public abstract Character pickCharacter(List<Character> availableCharacters);

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
    protected abstract List<District> pickDistrictsFromDeck(List<District> drawnCards, int amountToChoose);

    /**
     * Asks the player to choose district(s) from his hand to be build
     *
     * @return The chosen districts
     */
    public List<District> pickDistrictsToBuild(int turn) {
        return pickDistrictsToBuild(NUMBER_OF_DISTRICTS_TO_BUILD, turn);
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
     * Choose a color for the bonus score and add it
     *
     * @param tookColors the colors already chosen
     * @return the chosen color
     */
    public abstract Optional<Colors> pickBonusColor(Set<Colors> tookColors);

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
        Set<Colors> tookColors = new HashSet<>();
        int anyCount = 0;
        for (District district : getBuiltDistricts()) {
            Optional<Colors> color = district.bonusColors(builtInTheLastTurn(district, lastTurn));
            if (color.isPresent()) tookColors.add(color.get());
            else anyCount++;
        }
        if (tookColors.size() == Colors.values().length - 1) return true;
        else if (anyCount + tookColors.size() >= Colors.values().length - 1) {
            for (int i = 0; i < anyCount; i++) {
                pickBonusColor(tookColors).ifPresent(tookColors::add);
            }
        }
        return Colors.values().length - 1 == tookColors.size();
    }

    /**
     * @param lastTurn the number of the last turn
     * @return the current score of the player
     */
    public int getScore(int lastTurn) {
        int score = getDistrictsScore();
        if (allColorsInDistricts(lastTurn)) score += 3;
        if (isGameEnder()) score += 4;
        else if (getBuiltDistricts().size() >= 8) score += 2;
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
        getBuiltDistricts().forEach(district -> district.getAction().ifPresent(actionSet::addAll)); // Add the special action of each district if it has one
        character.getAction().ifPresent(actionSet::addAll); // Add the special actions of the character
        return actionSet;
    }

    public Set<Action> getActionSet() {
        return actionSet;
    }

    public boolean removeAction(Action action) {
        return actionSet.remove(action);// Remove the action of the actionSet
    }

     /**
     * Set the endPlayer boolean to true
     */
    public void endsGame() {
        gameEnder = true;
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
     * Earn the turn startup coins of the character
     */
    public void earnTurnStartupCoins() {
        gainCoins(character.coinsToEarnAtTurnStartup());
    }
}
