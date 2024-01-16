package fr.univ_cotedazur.polytech.si3.team_c.citadels.characters;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Action;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.Character;

import java.util.List;

public class Magician extends Character {
    public Magician() {
        super("Magician", 3);
    }

    @Override
    public List<Action> getAction() {
        return List.of(Action.EXCHANGE_DECK, Action.EXCHANGE_PLAYER);
    }
}
