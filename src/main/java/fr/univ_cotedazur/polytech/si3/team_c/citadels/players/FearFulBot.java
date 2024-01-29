package fr.univ_cotedazur.polytech.si3.team_c.citadels.players;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Colors;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.District;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.IPlayer;

import java.util.List;
import java.util.Optional;

public class FearFulBot extends Bot {
    @Override
    protected double destroyFear() {
        var nb = possibleDestruction(getPlayers()).size();
        if (nb == 0) {
            return 0;
        }
        return 10. + nb * 2.;
    }

    @Override
    protected double stealCoin() {
        var players = possibleDestruction(getPlayers());
        return Math.min(0.10 * players.stream().filter(iPlayer -> iPlayer.getCoins() > getCoins()).toList().size(), 1);
    }

    @Override
    protected double nonDestructibleSecurity() {
        var nb = possibleDestruction(getPlayers()).size();
        if (nb == 0) {
            return 0;
        }
        return 21;
    }

    @Override
    protected double exchangePlayerFear() {
        var nb = possibleExchange(getPlayers());
        if (nb < getPlayers().size() / 2.) {
            return 5 + nb;
        }
        return 7 + nb * 2;
    }

    @Override
    protected double killFear() {
        var nb = possibleKill(getPlayers());
        if (nb == 0) {
            return 0;
        }
        return nb + 5;
    }

    @Override
    protected double stealFear() {
        var nb = possibleSteal(getPlayers());
        if (nb == 0) {
            return 0;
        }
        return 5 + nb;
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

    protected List<IPlayer> possibleDestruction(List<IPlayer> players) {
        return players.stream().filter(iPlayer -> getBuiltDistricts().stream().anyMatch(district -> couldDestroy(district, iPlayer))).toList();
    }

    protected double possibleKill(List<IPlayer> players) {
        var mostDangerous = getMostDangerousPlayersByBuiltDistricts(players);
        int numberOfPoint = getBuiltDistricts().stream().mapToInt(District::getPoint).sum();
        return (double) players.size() - mostDangerous.stream().takeWhile(iPlayer -> iPlayer.getBuiltDistricts().stream().mapToInt(District::getPoint).sum() >= numberOfPoint).toList().size();
    }

    protected double possibleExchange(List<IPlayer> players) {
        return players.stream().filter(iPlayer -> iPlayer.getHandSize() < getHandSize()).toList().size();
    }

    protected double possibleSteal(List<IPlayer> players) {
        return players.stream().filter(iPlayer -> iPlayer.getCoins() < getCoins()).toList().size();
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
