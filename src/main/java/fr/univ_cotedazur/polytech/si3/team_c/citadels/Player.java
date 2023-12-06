package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A player (human or robot)
 *
 * @author Team C
 */
public abstract class Player {
    private final String name;
    private int coins;
    private final ArrayList<District> builtDistricts;
    private final ArrayList<District> handDistricts;
    private Character character;


    protected Player(String name, int coins, List<District> districts) {
        this.name = name;
        this.coins = coins;
        handDistricts = new ArrayList<>(districts);
        builtDistricts = new ArrayList<>();
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
     * Add some coins to the player "wallet"
     *
     * @param quantity Quantity of coins to add
     */
    public void gainCoins(int quantity) {
        coins += quantity;
    }

    /**
     * Gets all the districts that the player built (and that haven't been destroyed)
     */
    public List<District> getBuiltDistricts() {
        return new ArrayList<>(builtDistricts) {
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
    protected boolean buildDistrict(District district) {
        if (!handDistricts.contains(district)) return false;
        if (!pay(district.getCost())) return false;
        handDistricts.remove(district);
        builtDistricts.add(district);
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
     * Which action should be done (will be asked until there's no more actions to do)
     */
    public abstract Action nextAction(List<Action> remainingActions);

    /**
     * Asks the player to choose n districts from the districts that has been drawn
     *
     * @param drawnCards     The list of the drawn districts
     * @param amountToChoose The amount of districts to choose
     * @return The chosen districts
     */
    public abstract List<District> pickDistrictsFromDeck(List<District> drawnCards, int amountToChoose);

    /**
     * Asks the player to choose districts from his hand to be build
     *
     * @param maxAmountToChoose The max amount of districts that can be built
     * @return The chosen districts
     */
    public abstract List<District> pickDistrictsToBuild(int maxAmountToChoose);

    /**
     * Gets the current score of the player
     */
    public int getScore() {
        return getBuiltDistricts().stream().mapToInt(District::getPoint).sum();
    }
}
