package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.characters.Character;

import java.util.ArrayList;
import java.util.List;

public abstract class Player {
    private final String name;
    private int coins;
    private ArrayList<District> builtDistricts;
    private ArrayList<District> handDistricts;
    private Character character;


    protected Player(String name) {
        this.name = name;
        coins = 0;
        builtDistricts = new ArrayList<>();
        handDistricts = new ArrayList<>();
    }

    public abstract Character pickCharacter(List<Character> availableCharacters);

    public String getName() {
        return name;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
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
}
