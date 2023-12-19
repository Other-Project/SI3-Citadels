package fr.univ_cotedazur.polytech.si3.team_c.citadels.characters;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Action;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.Character;

import java.util.List;
import java.util.Optional;

public class Architect extends Character {
    public Architect() {
        super("Architect", 7);
    }

    @Override
    public Optional<List<Action>> getAction() {
        return Optional.of(List.of(Action.SPECIAL_DRAW));
    }

    @Override
    public int numberOfDistrictToBuild() {
        return 3;
    }
}
