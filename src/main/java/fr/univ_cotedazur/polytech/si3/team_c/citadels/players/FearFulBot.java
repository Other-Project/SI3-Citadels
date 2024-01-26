package fr.univ_cotedazur.polytech.si3.team_c.citadels.players;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Colors;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.District;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.IPlayer;

import java.util.List;
import java.util.Optional;

public class FearFulBot extends Bot {
    @Override
    protected double destroyFear() {
        return possibleDestruction(getPlayers()) ? 10 : 0;
    }

    @Override
    protected double exchangePlayerFear() {
        return possibleDestruction(getPlayers()) ? 10 : 0;
    }

    @Override
    protected double killFear() {
        return possibleDestruction(getPlayers()) ? 10 : 0;
    }

    @Override
    protected double stealFear() {
        return possibleDestruction(getPlayers()) ? 10 : 0;
    }

    public FearFulBot(String name, int coins, List<District> districts) {
        super(name, coins, districts);
    }

    public FearFulBot(String name) {
        super(name);
    }

    @Override
    protected double districtProfitability(District district) {
        if (getBuiltDistricts().contains(district)) return -1; // We can't build the same district twice
        return (district.getPoint() - 3) * 0.35
                + districtPropertyGain(district, District::numberOfDistrictsToDraw, this::numberOfDistrictsToDraw) / (getBuiltDistricts().size() + 1)
                + districtPropertyGain(district, District::numberOfDistrictsToKeep, this::numberOfDistrictsToKeep) / (getBuiltDistricts().size() + 1)
                + (district.isDestructible() ? 0 : 1);
    }

    protected boolean couldDestroy(District district, IPlayer player) {
        return district.isDestructible() && player.getCoins() + 2 + player.getBuiltDistricts().stream().filter(district1 -> district1.getColor() == Colors.RED).count() >= district.getCost() - 1;
    }

    protected boolean possibleDestruction(List<IPlayer> players) {
        return getBuiltDistricts().stream().anyMatch(district -> players.stream().anyMatch(iPlayer -> couldDestroy(district, iPlayer)));
    }

    @Override
    protected Optional<District> districtObjective() {
        District bestDistrict = null;
        double bestProfitability = Double.MIN_VALUE;
        for (District district : getHandDistricts()) {
            double profitability = districtProfitability(district);
            if ((bestDistrict == null || profitability > bestProfitability || (profitability == bestProfitability && district.getCost() < bestDistrict.getCost())) && profitability >= 0) {
                bestDistrict = district;
                bestProfitability = profitability;
            }
        }
        return Optional.ofNullable(bestDistrict);
    }
}
