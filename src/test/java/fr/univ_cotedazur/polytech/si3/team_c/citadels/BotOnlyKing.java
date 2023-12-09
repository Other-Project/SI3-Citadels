package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.characters.King;

import java.util.List;

public class BotOnlyKing extends Bot {

    public BotOnlyKing(String name, int coins, List<District> districts) {
        super(name, coins, districts);
    }

    @Override
    public Character pickCharacter(List<Character> availableCharacters) {
        setCharacter(new King());
        return getCharacter().orElseThrow();
    }
}
