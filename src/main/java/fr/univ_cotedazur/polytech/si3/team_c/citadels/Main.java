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

    public static void twoThousandGame() {
        HashMap<String, Statistic> stat = new HashMap<>();
        stat.put(Bot.class.getName(), new Statistic());
        stat.put(FearFulBot.class.getName(), new Statistic());
        stat.put(DiscreetBot.class.getName(), new Statistic());

        for (int k = 0; k < 1000; k++) {

            Game game = new Game(new Bot("Bot"), new DiscreetBot("Discrete Bot"), new FearFulBot("Fearful Bot"));
            game.start();

            SimpleEntry<List<Player>, Integer> winners = game.getWinners();
            List<String> botWinners = winners.getKey().stream().map(bot -> bot.getClass().getName()).toList();


            if (botWinners.size() == 1) stat.get(botWinners.get(0)).addWin();
            else botWinners.forEach(bot -> stat.get(bot).addEquality());


            List<String> players = new ArrayList<>(game.getPlayerList().stream().map(player -> player.getClass().getName()).toList());
            players.removeAll(botWinners.stream().toList());
            players.forEach(bot -> stat.get(bot).addLoose());
        }
        for (Map.Entry<String, Statistic> entry : stat.entrySet()) {
            String message = entry.getKey() + " " + stat.get(entry.getKey()).toString();
            LOGGER.log(Level.INFO, message);
        }
    }
}
