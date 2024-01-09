package fr.univ_cotedazur.polytech.si3.team_c.citadels.characters;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Action;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.Character;

import java.util.List;
import java.util.Optional;

public class Assassin extends Character {
    public Assassin() {
        super("Assassin", 1);
    }

    @Override
    public Optional<List<Action>> getAction() {
        return Optional.of(List.of(Action.KILL));
    }
}
