package fr.univ_cotedazur.polytech.si3.team_c.citadels.districts;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Colors;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.District;

import java.util.Optional;

public class Library extends District {
    public Library() {
        super("Library", 6, Colors.PURPLE, 6);
    }

    @Override
    public Optional<Integer> numberOfDistrictsToKeep() {
        return Optional.of(2);
    }
}
