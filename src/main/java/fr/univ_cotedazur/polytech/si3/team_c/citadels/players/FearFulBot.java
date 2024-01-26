package fr.univ_cotedazur.polytech.si3.team_c.citadels.players;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Colors;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.District;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.IPlayer;

import java.util.List;

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

    public FearFulBot(String name) {
        super(name);
    }

    public FearFulBot(String name, int coins, List<District> districts) {
        super(name, coins, districts);
    }

    @Override
    protected double districtProfitability(District district) {
        if (getBuiltDistricts().contains(district)) return -1; // We can't build the same district twice
        return (district.getPoint() - 2) * 0.25
                + districtPropertyGain(district, District::numberOfDistrictsToDraw, this::numberOfDistrictsToDraw) / (getBuiltDistricts().size() + 1)
                + districtPropertyGain(district, District::numberOfDistrictsToKeep, this::numberOfDistrictsToKeep) / (getBuiltDistricts().size() + 1)
                + (district.isDestructible() ? 0 : 1);
    }

    private boolean couldDestroy(District district, IPlayer player) {
        return district.isDestructible() && player.getCoins() + 2 + player.getBuiltDistricts().stream().filter(district1 -> district1.getColor() == Colors.RED).count() >= district.getCost();
    }

    private boolean possibleDestruction(List<IPlayer> players) {
        return getBuiltDistricts().stream().anyMatch(district -> players.stream().anyMatch(iPlayer -> couldDestroy(district, iPlayer)));
    }
}
