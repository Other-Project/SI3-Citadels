package fr.univ_cotedazur.polytech.si3.team_c.citadels.characters;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Action;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.Character;

import java.util.List;

public class Assassin extends Character {
    public Assassin() {
        super("Assassin", 1);
    }

    @Override
    public List<Action> getAction() {
        return List.of(Action.KILL);
    }
}
