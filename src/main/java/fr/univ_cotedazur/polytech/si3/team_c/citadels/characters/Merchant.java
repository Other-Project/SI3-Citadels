package fr.univ_cotedazur.polytech.si3.team_c.citadels.characters;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Action;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.Character;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.Colors;

import java.util.List;
import java.util.Optional;

public class Merchant extends Character {
    public Merchant() {
        super("Merchant", Colors.GREEN, 6);
    }

    @Override
    public Optional<List<Action>> getAction() {
        return Optional.of(List.of(Action.SPECIAL_INCOME));
    }

    @Override
    public int coinsToEarnAtTurnStartup() {
        return 1;
    }
}
