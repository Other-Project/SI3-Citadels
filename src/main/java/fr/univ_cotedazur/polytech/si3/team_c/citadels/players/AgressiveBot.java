package fr.univ_cotedazur.polytech.si3.team_c.citadels.players;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.District;

import java.util.List;

public class AgressiveBot extends Bot {
    @Override
    protected double crownFear() {
        return super.crownFear() * 2;
    }

    @Override
    protected double killFear() {
        return super.killFear() * 2;
    }

    @Override
    protected double stealFear() {
        return super.stealFear() * 2;
    }

    @Override
    protected double exchangePlayerFear() {
        return super.exchangePlayerFear() * 2;
    }

    @Override
    protected double destroyFear() {
        return super.destroyFear() * 2;
    }

    public AgressiveBot(String name) {
        super(name);
    }

    public AgressiveBot(String name, int coins, List<District> districts) {
        super(name, coins, districts);
    }

    @Override
    protected double districtProfitability(District district) {
        return super.districtProfitability(district) - district.getCost();
    }
}
