package fr.univ_cotedazur.polytech.si3.team_c.citadels.players;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.District;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.Game;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.IPlayer;

import java.util.Comparator;
import java.util.List;

public class DiscreetBot extends Bot {
    // The discreet player behaviour is passive : he doesn't attack other players
    @Override
    protected double crownFear() {
        return -10;
    }

    @Override
    protected double destroyFear() {
        return -10;
    }

    @Override
    protected double exchangePlayerFear() {
        return -10;
    }

    @Override
    protected double killFear() {
        return -10;
    }

    @Override
    protected double stealFear() {
        return -10;
    }

    @Override
    protected double numberOfDistrictToBuildMultiplier() {
        // We increase the coefficient relating to the number of district we can build according to tryFinalRush
        return tryFinalRush() ? super.numberOfDistrictToBuildMultiplier() * 9999 : super.numberOfDistrictToBuildMultiplier();
    }

    public DiscreetBot(String name) {
        super(name);
    }

    public DiscreetBot(String name, int coins, List<District> districts) {
        super(name, coins, districts);
    }

    @Override
    protected double districtProfitability(District district) {
        if (getBuiltDistricts().contains(district)) return -1; // We can't build the same district twice

        double defaultProfitability = districtPropertyGain(district, District::numberOfDistrictsToDraw, this::numberOfDistrictsToDraw) / (getBuiltDistricts().size() + 1)
                + districtPropertyGain(district, District::numberOfDistrictsToKeep, this::numberOfDistrictsToKeep) / (getBuiltDistricts().size() + 1)
                + bonusProfitability(district);
        if (differenceOfDistrictsWithFirst() >= 3) {
            return 1.0 / (district.getCost() << 2) + defaultProfitability;
        }
        // Lorentz function that prioritize mid-cost districts
        return 9.52 / (Math.pow((district.getCost() - 3.77), 2) + Math.pow((3.14 / 2), 2)) + defaultProfitability;
    }

    /**
     * Calculates the district profitability depending on what it brings to get the final bonus
     *
     * @param district the current district
     */
    private double bonusProfitability(District district) {
        return (getBuiltDistricts().size() * 2 + 1.0) / (getBuiltDistricts().stream().filter(district1 -> district1.getColor() == district.getColor()).count() + 1 << 2);
    }

    private boolean tryFinalRush() {
        return Game.DISTRICT_NUMBER_TO_END - getHandSize() > 1
                && Game.DISTRICT_NUMBER_TO_END - getHandSize() <= 3
                && getCoins() >= 4;
    }

    private int differenceOfDistrictsWithFirst() {
        IPlayer firstPlayer = getPlayers().stream().sorted(
                Comparator.<IPlayer>comparingInt(player -> player.getBuiltDistricts().size()).reversed()).toList().get(0);
        return firstPlayer.getHandSize() - getHandSize();
    }
}
