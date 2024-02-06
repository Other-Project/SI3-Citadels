package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.players.Bot;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.players.DiscreetBot;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.players.FearFulBot;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.players.Player;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {
    private static final Logger LOGGER = Logger.getGlobal();
    @Parameter(names = "--2thousands")
    private static boolean twoThousand = false;

    @Parameter(names = "--demo")
    private static boolean demo = false;


    public static void main(String... args) {
        Main main = new Main();
        JCommander.newBuilder()
                .addObject(main)
                .build()
                .parse(args);
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$s] %5$s%6$s%n");
        if (twoThousand) {
            twoThousandGame();
        } else if (demo) {
            Arrays.stream(LOGGER.getParent().getHandlers()).forEach(handler -> handler.setLevel(Level.FINEST));
            LOGGER.setLevel(Level.FINEST);
            gameBase();
        }
    }

    public static void gameBase() {
        new Game(4).start();
    }

    private static HashMap<String, Statistic> playMultipleGames(List<Player> botsInit, int numberOfGames) {
        List<Player> bots = new ArrayList<>(botsInit);
        HashMap<String, Statistic> stat = new HashMap<>();
        bots.forEach(bot -> stat.put(bot.getName(), new Statistic()));

        for (int k = 0; k < numberOfGames; k++) {

            Game game = new Game();
            bots.forEach(game::addPlayer);
            game.start();

            SimpleEntry<List<Player>, Integer> winners = game.getWinners();
            List<String> botWinners = winners.getKey().stream().map(Player::getName).toList();


            if (botWinners.size() == 1) stat.get(botWinners.get(0)).addWin();
            else botWinners.forEach(bot -> stat.get(bot).addEquality());


            List<String> players = new ArrayList<>(game.getPlayerList().stream().map(Player::getName).toList());
            players.removeAll(botWinners.stream().toList());
            players.forEach(bot -> stat.get(bot).addLoose());

            bots = new ArrayList<>(botsInit);

        }

        for (Map.Entry<String, Statistic> entry : stat.entrySet()) {
            entry.getValue().getPourcent(numberOfGames);
        }

        return stat;
    }

    private static void displayResult(HashMap<String, Statistic> stat, int numberOfGames) {
        LOGGER.log(Level.INFO, "Statistic measures on {0} games : \n", numberOfGames);
        for (Map.Entry<String, Statistic> entry : stat.entrySet()) {
            String message = entry.getKey() + " " + stat.get(entry.getKey()).toString();
            LOGGER.log(Level.INFO, message);
        }
    }

    public static void twoThousandGame() {
        List<Player> bots = new ArrayList<>(List.of(new Bot("Bot"), new DiscreetBot("Discrete Bot"), new FearFulBot("Fearful Bot")));
        HashMap<String, Statistic> res = playMultipleGames(bots, 1000);
        displayResult(res, 1000);
        bots = new ArrayList<>(List.of(new Bot("Bot 1"), new Bot("Bot 2"), new Bot("Bot 3"), new Bot("Bot 4")));
        res = playMultipleGames(bots, 1000);
        displayResult(res, 1000);
    }
}
