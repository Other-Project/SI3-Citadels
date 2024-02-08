package fr.univ_cotedazur.polytech.si3.team_c.citadels.players;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Character;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.*;

import java.util.*;

import static fr.univ_cotedazur.polytech.si3.team_c.citadels.Action.GET_CROWN;

public class RichardBot extends Bot {

    List<Character> charactersPresentBefore; //to store the characters available at the bot choice and know in which strategy we are when we want to kill and it is the last turn

    public RichardBot(String name) {
        this(name, 0, Collections.emptyList());

    }

    public RichardBot(String name, int coins, List<District> districts) {
        super(name, coins, districts);
    }


    public List<Character> removeCharacters(List<Character> characterList) {
        //REMOVE FOR THE cASE OF ASSASSIN
        List<Character> remove = new ArrayList<>();
        Optional<Character> warlord = characterList.stream().filter(character -> character.getAction().contains(Action.DESTROY)).findFirst();
        Optional<Character> thief = characterList.stream().filter(character -> character.getAction().contains(Action.STEAL)).findFirst();
        List<IPlayer> couldWinThief = getPlayersAbleToWin(thief.orElse(null));
        List<IPlayer> couldWinWarlord = getPlayersAbleToWin(warlord.orElse(null));
        if (thief.isPresent() && couldWinThief.isEmpty() && !enrichPossible()) remove.add(thief.get());
        if (warlord.isPresent() && couldWinWarlord.isEmpty() && !iAmFirst()) remove.add(warlord.get());

        //CASE 2 OF LAST TURN (warlod not in list in the case 3 of LAST)
        if (warlord.isPresent() && thirdOrMoreWillWin(characterList) && hasCrown())
            remove.add(warlord.get());

        return remove;
    }

    @Override
    public Character chooseCharacterToKill(List<Character> characterList) {
        characterList = new ArrayList<>(characterList); //to be sure that characterList initial is not modified
        //ASSASSIN
        characterList.removeAll(removeCharacters(characterList));

        Optional<Character> warlord = charactersPresentBefore.stream().filter(character -> character.getAction().contains(Action.DESTROY)).findFirst();
        //LastTurn
        Optional<Character> assassin = charactersPresentBefore.stream().filter(warl -> warl.getAction().contains(Action.KILL)).findFirst();
        Optional<Character> bishop = charactersPresentBefore.stream().filter(warl -> !warl.canHaveADistrictDestroyed()).findFirst();
        Optional<Character> magician = charactersPresentBefore.stream().filter(warl -> warl.getAction().contains(Action.EXCHANGE_PLAYER)).findFirst();
        if (thirdOrMoreWillWin(characterList) && iAmSecond()) {
            var annoyingCharacter = characterList.stream().filter(character -> !character.canHaveADistrictDestroyed()).findFirst();
            if (annoyingCharacter.isPresent()) return annoyingCharacter.get();
        }
        if (magician.isPresent() && assassin.isPresent() && bishop.isPresent() && thirdOrMoreWillWin(characterList) && hasCrown() && secondPlayer().getHandSize() > 5 && thirdPlayer(positionWinningPlayer(characterList)).getHandSize() == 0)
            return magician.get();

        //ARCHITECT
        Optional<Character> architect = characterList.stream().filter(character -> character.numberOfDistrictToBuild() == 3).findFirst();
        if (architect.isPresent() && onePlayerCouldBecomeUntouchable(architect.get())) {
            var annoyingCharacter = characterList.stream().filter(character -> character.numberOfDistrictToBuild() == 3).findFirst();
            if (annoyingCharacter.isPresent()) return annoyingCharacter.get();
        }

        //KING
        Optional<Character> king = characterList.stream().filter(character -> character.startTurnAction().equals(GET_CROWN)).findFirst();
        if (king.isPresent() && buildPenultimateDistrict()) {
            var annoyingCharacter = characterList.stream().filter(character -> character.startTurnAction().equals(GET_CROWN)).findFirst();
            if (annoyingCharacter.isPresent()) return annoyingCharacter.get();
        }

        if (warlord.isPresent() && iWillbuildPenultimateDistrict()) {
            var annoyingCharacter = characterList.stream().filter(character -> character.getAction().contains(Action.DESTROY)).findFirst();
            if (annoyingCharacter.isPresent()) return annoyingCharacter.get();
        }
        return super.chooseCharacterToKill(characterList);
    }

    @Override
    protected double characterProfitability(Character character, CharacterManager characterManager) {
        charactersPresentBefore = characterManager.possibleCharactersToChoose();
        //LastTurn
        Optional<Character> warlord = characterManager.charactersList().stream().filter(warl -> warl.getAction().contains(Action.DESTROY)).findFirst();
        Optional<Character> assassin = characterManager.charactersList().stream().filter(warl -> warl.getAction().contains(Action.KILL)).findFirst();
        Optional<Character> bishop = characterManager.charactersList().stream().filter(warl -> !warl.canHaveADistrictDestroyed()).findFirst();
        Optional<Character> magician = characterManager.charactersList().stream().filter(warl -> warl.getAction().contains(Action.EXCHANGE_PLAYER)).findFirst();
        if (warlord.isPresent() && assassin.isPresent() && bishop.isPresent() && thirdOrMoreWillWin(characterManager.possibleCharactersToChoose()) && hasCrown() && (character.getAction().contains(Action.DESTROY)))
            return 500;
        if (assassin.isPresent() && bishop.isPresent() && thirdOrMoreWillWin(characterManager.possibleCharactersToChoose()) && iAmSecond() && (character.getAction().contains(Action.KILL)))
            return 450;
        if (warlord.isPresent() && assassin.isPresent() && thirdOrMoreWillWin(characterManager.possibleCharactersToChoose()) && hasCrown() && (character.getAction().contains(Action.KILL)))
            return 450;
        if (warlord.isPresent() && thirdOrMoreWillWin(characterManager.possibleCharactersToChoose()) && iAmSecond() && (character.getAction().contains(Action.DESTROY)))
            return 450;
        if (assassin.isPresent() && bishop.isPresent() && thirdOrMoreWillWin(characterManager.possibleCharactersToChoose()) && hasCrown() && (character.getAction().contains(Action.KILL)))
            return 450;
        if (bishop.isPresent() && magician.isPresent() && thirdOrMoreWillWin(characterManager.possibleCharactersToChoose()) && iAmSecond() && (character.getAction().contains(Action.EXCHANGE_PLAYER)) && getHandSize() < 5)
            return 450;

        if (warlord.isPresent() && bishop.isPresent() && thirdOrMoreWillWin(characterManager.possibleCharactersToChoose()) && hasCrown() && (character.getAction().contains(Action.DESTROY)))
            return 450;
        if (bishop.isPresent() && thirdOrMoreWillWin(characterManager.possibleCharactersToChoose()) && iAmSecond() && (character.canHaveADistrictDestroyed()))
            return 450;

        //ARCHITECT
        Optional<Character> architect = characterManager.charactersList().stream().filter(archi -> archi.numberOfDistrictToBuild() == 3).findFirst();
        if (architect.isPresent() && onePlayerCouldBecomeUntouchable(architect.get())) {
            if (character.getAction().contains(Action.KILL)) return 250;
            if (character.numberOfDistrictToBuild() >= 3) return 200;
        }
        if (architect.isPresent() && couldBecomeUntouchable(architect.get()) && (character.numberOfDistrictToBuild() >= 3)) {
            return 150;
        }

        //KING
        if (buildPenultimateDistrict() || iWillbuildPenultimateDistrict()) {
            if (character.startTurnAction().equals(GET_CROWN)) return 300;
            else if (character.getAction().contains(Action.KILL)) return 150;
            else if (character.getAction().contains(Action.DESTROY)) return 120;
            else if (!character.canHaveADistrictDestroyed()) return 100;
        }
        return super.characterProfitability(character, characterManager);
    }

    @Override
    protected double exchangePlayerCard() {
        return super.exchangePlayerCard();//TODO add magician choice
    }

    private List<IPlayer> getPlayersAbleToWin(List<Character> characters) {
        Set<IPlayer> players = new HashSet<>();
        characters.forEach(character -> players.addAll(getPlayersAbleToWin(character)));
        return players.stream().toList();
    }


    private List<IPlayer> getPlayersAbleToWin(Character character) {
        if (character == null) return new ArrayList<>();
        return getPlayers().stream().filter(iPlayer -> iPlayer.getBuiltDistricts().size() + character.numberOfDistrictToBuild() >= getNumberOfDistrictsToEnd()).toList();
    }

    private boolean iAmFirst() {
        return getPlayers().stream().allMatch(iPlayer -> iPlayer.getBuiltDistricts().stream().mapToInt(District::getPoint).sum() < getBuiltDistricts().stream().mapToInt(District::getPoint).sum());
    }

    private boolean enrichPossible() {
        return potentialAmountOfCoins() >= 6;
    }

    /**
     * This method returns the potential maximum amount of coins a player can have by stealing someone else
     */
    private int potentialAmountOfCoins() {
        int potentialMaxCoins = 0;
        var players = getPlayers();
        for (int i = 0; i < players.size() - 1; i++) {
            for (int j = 1; j < players.size(); j++) {
                int currentRes = players.get(i).getCoins() + players.get(j).getCoins();
                if (currentRes >= potentialMaxCoins) potentialMaxCoins = currentRes;
            }
        }
        return potentialMaxCoins;
    }

    private boolean onePlayerCouldBecomeUntouchable(Character character) {
        return !getPlayers().stream()
                .filter(iPlayer -> iPlayer.getCoins() >= 4
                        && iPlayer.getHandSize() >= 1
                        && iPlayer.getBuiltDistricts().size() >= getNumberOfDistrictsToEnd() - character.numberOfDistrictToBuild()).toList().isEmpty();
    }

    private boolean couldBecomeUntouchable(Character character) {
        return getCoins() >= 4
                && getHandSize() >= 1
                && getBuiltDistricts().size() >= getNumberOfDistrictsToEnd() - character.numberOfDistrictToBuild();
    }

    /**
     * Detects if a player is on the verge of building his penultimate district
     *
     */
    private boolean buildPenultimateDistrict() {
        return !getPlayers().stream().filter(player ->
                player.getBuiltDistricts().size() == getNumberOfDistrictsToEnd() - 2
                        && player.getBuiltDistricts().size() >= this.getBuiltDistricts().size()).toList().isEmpty();
    }

    private boolean iWillbuildPenultimateDistrict() {
        return getBuiltDistricts().size() == getNumberOfDistrictsToEnd() - 2;
    }

    private boolean iAmSecond() {
        return getPlayersWithYou().get(1).equals(this);
    }


    private boolean thirdOrMoreWillWin(List<Character> possible) {
        return possible.stream().anyMatch(character -> !getPlayersWithYou().subList(2, getPlayersWithYou().size()).stream().filter(player -> getPlayersAbleToWin(character).contains(player)).toList().isEmpty());
    }

    private IPlayer secondPlayer() {
        return getPlayers().get(0);
    }

    private IPlayer thirdPlayer(int i) {
        if (hasCrown()) return getPlayers().get(i - 1);
        else return getPlayersWithYou().get(i);
    }

    private int positionWinningPlayer(List<Character> characters) {
        List<Integer> position = new ArrayList<>();
        List<IPlayer> players = getPlayersWithYou();
        for (IPlayer iPlayer : players) {
            if (characters.stream().anyMatch(character -> iPlayer.getBuiltDistricts().size() + character.numberOfDistrictToBuild() == getNumberOfDistrictsToEnd()))
                position.add(players.indexOf(iPlayer));
        }
        return position.stream().min(Integer::compareTo).get();
    }

}