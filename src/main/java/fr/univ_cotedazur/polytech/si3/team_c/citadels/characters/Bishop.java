package fr.univ_cotedazur.polytech.si3.team_c.citadels.characters;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Action;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.Character;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.Colors;

import java.util.List;

public class Bishop extends Character {
    public Bishop() {
        super("Bishop", Colors.BLUE, 5);
    }

    @Override
    public List<Action> getAction() {
        return List.of(Action.SPECIAL_INCOME);
    }

    @Override
    public boolean canHaveADistrictDestroyed() {
        return false;
    }
}
