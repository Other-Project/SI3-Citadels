package fr.univ_cotedazur.polytech.si3.team_c.citadels.players;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Character;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.*;

import java.util.*;

import static fr.univ_cotedazur.polytech.si3.team_c.citadels.Action.GET_CROWN;

public class RichardBot extends Bot {
    private static final int LOW_AMOUNT_OF_CARD = 3;
    private List<Character> charactersPresent; //to store the characters available at the bot choice and know in which strategy we are when we want to kill, and it is the last turn

    public RichardBot(String name) {
        this(name, 0, Collections.emptyList());
    }

    public RichardBot(String name, int coins, List<District> districts) {
        super(name, coins, districts);
    }

    /**
     * Method to implement Richard's strategy concerning the Assassin, to don't kill some Thief or Warlord in some cases .
     *
     * @param characterList: The list of characters that can be killed.
     * @return: A list of characters that the Assassin will not kill.
     */
    protected List<Character> charactersNotToKill(List<Character> characterList) {
        // ASSASSIN
        Set<Character> remove = new HashSet<>();
        List<Character> warlord = characterList.stream().filter(character -> character.getAction().contains(Action.DESTROY)).toList();
        List<Character> thief = characterList.stream().filter(character -> character.getAction().contains(Action.STEAL)).toList();
        List<IPlayer> couldWinThief = getPlayersAbleToWin(thief);
        List<IPlayer> couldWinWarlord = getPlayersAbleToWin(warlord);
        if (couldWinThief.stream().noneMatch(iPlayer -> characterEstimation(iPlayer, getPossibleCharacters(iPlayer)).getAction().contains(Action.STEAL)) && !enrichPossible())
            remove.addAll(thief);
        if (couldWinWarlord.stream().noneMatch(iPlayer -> characterEstimation(iPlayer, getPossibleCharacters(iPlayer)).getAction().contains(Action.DESTROY)) && !iAmFirst())
            remove.addAll(warlord);
        //CASE 2 OF LAST TURN (warlord not in list in the case 3 of LAST)
        if (thirdOrMoreWillWin(characterList) && hasCrown())
            remove.addAll(warlord);
        return remove.stream().toList();
    }

    /**
     * Method to implement Richard's strategy for character killing, prioritizing Richard's criteria if applicable and calling the superclass method otherwise.
     * @param characterList: The list of characters the player can kill.
     * @return: The character the Assassin must kill.
     */
    @Override
    public Character chooseCharacterToKill(List<Character> characterList) {
        characterList = new ArrayList<>(characterList); //to be sure that characterList initial is not modified
        characterList.removeAll(charactersNotToKill(characterList)); // Remove from the list of killable characters those you mustn't kill
        Optional<Character> warlord = charactersPresent.stream().filter(character -> character.getAction().contains(Action.DESTROY)).findFirst();

        //ASSASSIN THANKS TO charactersNotToKill

        //LastTurn
        Optional<Character> assassin = charactersPresent.stream().filter(c -> c.getAction().contains(Action.KILL)).findFirst();
        Optional<Character> bishop = charactersPresent.stream().filter(c -> !c.canHaveADistrictDestroyed()).findFirst();
        Optional<Character> magician = charactersPresent.stream().filter(c -> c.getAction().contains(Action.EXCHANGE_PLAYER)).findFirst();

        if (thirdOrMoreWillWin(characterList) && iAmSecond()) {
            var annoyingCharacter = characterList.stream().filter(character -> !character.canHaveADistrictDestroyed()).findFirst();
            if (annoyingCharacter.isPresent()) return annoyingCharacter.get();
        }
        if (magician.isPresent() && assassin.isPresent() && bishop.isPresent() && thirdOrMoreWillWin(characterList) && hasCrown() && secondPlayer().getHandSize() >= LOW_AMOUNT_OF_CARD && thirdPlayer(positionWinningPlayer(characterList)).getHandSize() == 0)
            return magician.get();

        //ARCHITECT
        Optional<Character> architect = characterList.stream().filter(character -> character.numberOfDistrictToBuild() > 1 && onePlayerCouldBecomeUntouchable(character)).findFirst();
        if (architect.isPresent())
            return architect.get();

        //KING
        Optional<Character> king = characterList.stream().filter(character -> character.startTurnAction().equals(GET_CROWN)).findFirst();
        if (king.isPresent() && buildPenultimateDistrict())
            return king.get();

        if (warlord.isPresent() && iWillBuildPenultimateDistrict())
            return warlord.get();

        return super.chooseCharacterToKill(characterList);
    }

    /**
     * Method to implement Richard's strategy for character selection, prioritizing Richard's criteria if applicable and calling the superclass method otherwise.
     * @param character The character whose profitability is to be calculated
     * @param characterManager The characterManager of the game to have information
     * @return The profitability of the character
     */
    @Override
    protected double characterProfitability(Character character, CharacterManager characterManager) {
        charactersPresent = new ArrayList<>(characterManager.possibleCharactersToChoose());

        //Last Turn
        Integer lastTurnProfitability = lastTurnProfitability(character, characterManager);
        if (lastTurnProfitability != null) return lastTurnProfitability;

        //ARCHITECT
        Integer counterArchitect = counterArchitectProfitability(character);
        if (counterArchitect != null) return counterArchitect;
        if (couldBecomeUntouchable(character, this))
            return 150;

        //KING
        Integer counterKing = counterKingProfitability(character);
        if (counterKing != null) return counterKing;

        return super.characterProfitability(character, characterManager);
    }

    /**
     * Method to implement Richard's strategy for character selection, considering the possibility of a player could win if he takes the king to the next turn (the penultimate turn in fact)
     * @param character The character whose profitability is to be calculated
     * @return The profitability of the character or null if the character is unprofitable
     */
    private Integer counterKingProfitability(Character character) {
        if (buildPenultimateDistrict() || iWillBuildPenultimateDistrict()) {
            if (character.startTurnAction().equals(GET_CROWN)) return 300;
            else if (character.getAction().contains(Action.KILL)) return 150;
            else if (character.getAction().contains(Action.DESTROY)) return 120;
            else if (!character.canHaveADistrictDestroyed()) return 100;
        }
        return null;
    }

    /**
     * Method to implement Richard's character selection strategy, considering the possibility of a player becoming untouchable due to the Architect
     * @param character The character whose profitability is to be calculated
     * @return The profitability of the character or null if the character is unprofitable
     */
    private Integer counterArchitectProfitability(Character character) {
        List<Character> architect = charactersPresent.stream().filter(archi -> archi.numberOfDistrictToBuild() > 1).toList();
        if (architect.stream().anyMatch(this::onePlayerCouldBecomeUntouchable)) {
            if (character.getAction().contains(Action.KILL)) return 500;
            if (character.numberOfDistrictToBuild() > 1) return 400;
        }
        return null;
    }

    /**
     * Method to implement Richard's character selection strategy, considering the possibility of a player could build his last district this turn
     * @param character The character whose profitability is to be calculated
     * @return The profitability of the character or null if the character is unprofitable
     */
    private Integer lastTurnProfitability(Character character, CharacterManager characterManager) {
        if (thirdOrMoreWillWin(characterManager.possibleCharactersToChoose())) {
            List<Character> warlord = charactersPresent.stream().filter(c -> c.getAction().contains(Action.DESTROY)).toList();
            List<Character> assassin = charactersPresent.stream().filter(c -> c.getAction().contains(Action.KILL)).toList();
            List<Character> bishop = charactersPresent.stream().filter(c -> !c.canHaveADistrictDestroyed()).toList();

            // The bot is the first player
            if (hasCrown()) {
                // First and fourth cases
                if (!warlord.isEmpty() && !bishop.isEmpty() && (character.getAction().contains(Action.DESTROY)))
                    return 500;
                // Second and third cases
                if ((!warlord.isEmpty() || !bishop.isEmpty()) && !assassin.isEmpty() && (character.getAction().contains(Action.KILL)))
                    return 450;
            }
            // The bot is the second player
            else if (iAmSecond()) {
                // First case
                if (warlord.isEmpty() && (character.getAction().contains(Action.KILL)))
                    return 450;
                // Second case
                if (bishop.isEmpty() && assassin.isEmpty() && (character.getAction().contains(Action.DESTROY)))
                    return 450;
                // Third case and fourth cases
                if (warlord.isEmpty() && assassin.isEmpty()) {
                    // To differentiate cases 3 and 4 we have chosen to favor a role which can exchange cards if the bot does not have too many cards
                    if (getHandSize() < LOW_AMOUNT_OF_CARD) {
                        if (character.getAction().contains(Action.EXCHANGE_PLAYER))
                            return 450;
                    } else if (!character.canHaveADistrictDestroyed())
                        return 450;
                }
            }
        }
        return null;
    }

    /**
     * Method to choose a player to exchange cards, implements the Richard's strategy
     * @param players List of player with which it can exchange
     *
     */
    @Override
    public IPlayer playerToExchangeCards(List<IPlayer> players) {
        if (getHandDistricts().size() < LOW_AMOUNT_OF_CARD && thirdOrMoreWillWin(charactersPresent) && charactersPresent.stream().noneMatch(character -> character.getAction().contains(Action.DESTROY)))
            return thirdPlayer(positionWinningPlayer(CharacterManager.defaultCharacterList()));
        else return super.playerToExchangeCards(players);
    }

    /**
     * Method to obtain all the players in capacity to win at the next turn with one of the character possible to choose
     * @param characters the characters that a player could take
     *
     */
    private List<IPlayer> getPlayersAbleToWin(List<Character> characters) {
        Set<IPlayer> players = new HashSet<>();
        characters.forEach(character -> players.addAll(getPlayersAbleToWin(character)));
        return players.stream().toList();
    }

    /**
     * Method to obtain all the players in capacity to win at the next turn with a character
     * @param character the character that a player could take
     *
     */
    private List<IPlayer> getPlayersAbleToWin(Character character) {
        if (character == null) return Collections.emptyList();
        return getPlayers().stream().filter(iPlayer -> iPlayer.getBuiltDistricts().size() + character.numberOfDistrictToBuild() >= getNumberOfDistrictsToEnd()).toList();
    }

    /**
     * Method to check if the bot is the first player in terms of points
     *
     */
    private boolean iAmFirst() {
        return getPlayers().stream().allMatch(iPlayer -> iPlayer.getBuiltDistricts().stream().mapToInt(District::getPoint).sum() < getBuiltDistricts().stream().mapToInt(District::getPoint).sum());
    }

    /**
     * Method to check if the bot could enrich it, in terms of Richard Strategy
     *
     */
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

    /**
     * Method to know if a player could become untouchable, on the Richard's criteria
     * @param character the character which could permit to a player ro become untouchable
     *
     */
    private boolean onePlayerCouldBecomeUntouchable(Character character) {
        return getPlayers().stream().anyMatch(iPlayer -> couldBecomeUntouchable(character, iPlayer));
    }

    /**
     * Method to know if a player could become untouchable, on the Richard's criteria
     * @param character the character which could permit to a player ro become untouchable
     * @param player the player to check
     *
     */
    private boolean couldBecomeUntouchable(Character character, IPlayer player) {
        return player.getCoins() >= 4
                && player.getHandSize() >= character.numberOfDistrictToBuild() - (character.startTurnAction().equals(Action.BEGIN_DRAW) ? 2 : 0)
                && player.getBuiltDistricts().size() >= getNumberOfDistrictsToEnd() - character.numberOfDistrictToBuild();
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

    /**
     * Detects if the bot is on the verge of building its penultimate district
     *
     */
    private boolean iWillBuildPenultimateDistrict() {
        return getBuiltDistricts().size() == getNumberOfDistrictsToEnd() - 2;
    }

    /**
     * Method to determine if the bot is the second player to choose its character
     *
     */
    private boolean iAmSecond() {
        return getPlayersWithYou().get(1).equals(this);
    }

    private boolean thirdOrMoreWillWin(List<Character> possible) {
        return possible.stream()
                .anyMatch(character -> character.numberOfDistrictToBuild() == 1 && !getPlayersWithYou().subList(2, getPlayersWithYou().size())
                        .stream()
                        .filter(player -> getPlayersAbleToWin(character).contains(player))
                        .toList().isEmpty());
    }

    /**
     * Method to obtain the player which choose his character in second
     *
     */
    private IPlayer secondPlayer() {
        return getPlayers().get(0);
    }

    /**
     * Method to obtain the player in position i the list of players
     * @param i the position of a player in the list of players
     */
    private IPlayer thirdPlayer(int i) {
        return getPlayersWithYou().get(i);
    }

    /**
     * Method to obtain the position of the player first player that could win in the list of players
     * @param characters the list of character a player could pick
     */
    private int positionWinningPlayer(List<Character> characters) {
        List<Integer> position = new ArrayList<>();
        List<IPlayer> players = getPlayersWithYou();
        for (IPlayer iPlayer : players) {
            if (getPlayersAbleToWin(characters).contains(iPlayer))
                position.add(players.indexOf(iPlayer));
        }
        return position.stream().mapToInt(i -> i).min().orElseThrow();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}