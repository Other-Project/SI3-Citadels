package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import java.util.ArrayList;
import java.util.List;


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

    public String getName() {
        return name;
    }

    public int getCoins() {
        return coins;
    }

    /**
     * Tries to pay a certain amount of money
     *
     * @param price The price to pay
     * @return True if the player has enough money
     */
    public boolean pay(int price) {
        if (coins < price) return false;
        coins -= price;
        return true;
    }

    public List<District> getBuiltDistricts() {
        return builtDistricts;
    }

    public List<District> getHandDistricts() {
        return handDistricts;
    }

    public Character getCharacter() {
        return character;
    }

    protected void setCharacter(Character character) {
        this.character = character;
    }

    protected boolean buildDistricts(District district) {
        if (!handDistricts.contains(district)) return false;
        if (!pay(district.getCost())) return false;
        handDistricts.remove(district);
        builtDistricts.add(district);
        return true;
    }

    public abstract Character pickCharacter(List<Character> availableCharacters);

    public abstract boolean pickCoins();

    public abstract List<District> pickDistrictsToBuild();
}
