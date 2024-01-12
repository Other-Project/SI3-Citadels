package fr.univ_cotedazur.polytech.si3.team_c.citadels.characters;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Action;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.Character;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.Colors;

import java.util.List;

public class King extends Character {
    public King() {
        super("King", Colors.YELLOW, 4);
    }

    @Override
    public List<Action> getAction() {
        return List.of(Action.SPECIAL_INCOME);
    }
}
