package fr.univ_cotedazur.polytech.si3.team_c.citadels.characters;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Action;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.Character;

import java.util.List;

public class Thief extends Character {
    public Thief() {
        super("Thief", 2);
    }

    @Override
    public List<Action> getAction() {
        return List.of(Action.STEAL);
    }
}
