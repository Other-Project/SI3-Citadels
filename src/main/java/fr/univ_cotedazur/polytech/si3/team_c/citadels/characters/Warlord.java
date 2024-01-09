package fr.univ_cotedazur.polytech.si3.team_c.citadels.characters;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Action;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.Character;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.Colors;

import java.util.List;
import java.util.Optional;

public class Warlord extends Character {
    public Warlord() {
        super("Warlord", Colors.RED, 8);
    }

    @Override
    public Optional<List<Action>> getAction() {
        return Optional.of(List.of(Action.SPECIAL_INCOME, Action.DESTROY));
    }
}
