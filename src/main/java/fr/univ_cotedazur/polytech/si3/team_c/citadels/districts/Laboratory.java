package fr.univ_cotedazur.polytech.si3.team_c.citadels.districts;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Action;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.Colors;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.District;

import java.util.List;
import java.util.Optional;

public class Laboratory extends District {
    public Laboratory() {
        super("Laboratory", 5, Colors.PURPLE, 5);
    }

    /**
     * Method to return the special action of the district Laboratory
     *
     * @return an optional with a list which contains the action DISCARD
     */
    @Override
    public Optional<List<Action>> getAction() {
        return Optional.of(List.of(Action.DISCARD));
    }
}
