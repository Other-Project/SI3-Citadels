package fr.univ_cotedazur.polytech.si3.team_c.citadels.characters;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Action;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.Character;

import java.util.List;
import java.util.Optional;

public class Magician extends Character {
    public Magician() {
        super("Magician", 3);
    }

    @Override
    public Optional<List<Action>> getAction() {
        return Optional.of(List.of(Action.EXCHANGE_DECK, Action.EXCHANGE_PLAYER));
    }
}
