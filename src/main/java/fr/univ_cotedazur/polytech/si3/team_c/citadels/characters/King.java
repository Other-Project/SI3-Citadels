package fr.univ_cotedazur.polytech.si3.team_c.citadels.characters;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Action;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.Character;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.Colors;

import java.util.List;
import java.util.Optional;

public class King extends Character {
    public King() {
        super("King", Colors.YELLOW, 4);
    }

    @Override
    public Optional<List<Action>> getAction() {
        return Optional.of(List.of(Action.SPECIAL_INCOME));
    }

    @Override
    public Action startTurnAction() {
        return Action.GET_CROWN;
    }
}
