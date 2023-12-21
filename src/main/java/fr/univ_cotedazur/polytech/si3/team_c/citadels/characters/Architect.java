package fr.univ_cotedazur.polytech.si3.team_c.citadels.characters;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Action;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.Character;

public class Architect extends Character {
    public Architect() {
        super("Architect", 7);
    }

    @Override
    public Action startTurnAction() {
        return Action.BEGIN_DRAW;// The architect draw 2 extra cards in his start-of-turn
    }

    @Override
    public int numberOfDistrictToBuild() {
        return 3;
    }
}
