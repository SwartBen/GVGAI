package tracks.singlePlayer.assignment2.exercise3;

import java.util.Random;

import core.logging.Logger;
import tools.Utils;
import tracks.ArcadeMachine;
import java.util.concurrent.ThreadLocalRandom;

public class Test2 {

    public static double[] temp_individual = {0.9, 7, 5, 0.1, 0.14};

    public static void main(String[] args) {

        /*----------------- TESTING VARIABLES SETUP ----------------------------*/

        String geneticOptimised = "tracks.singlePlayer.assignment2.geneticAdvanced.Agent";
        String spGamesCollection =  "examples/all_games_sp.csv";
        String[][] games = Utils.readGames(spGamesCollection);
        boolean visuals = false;
        int seed = new Random().nextInt();
        int gameIdx = 0;
        String gameName = games[gameIdx][1];
        String game = games[gameIdx][0];
        Random randomNumGen = new Random();
        String recordActionsFile = null;
        
        /*----------------- THE EVO ALGORITHM ----------------------------*/
        
        //Initialise population - just start with the pre-set params I guess
        double[] best_individual = Test.temp_individual;
        double score = 0;
        String level = game.replace(gameName, gameName + "_lvl" + 0);

        //POPULATUION INITALISATION => Game plays levels 0-4 of specified game
        for (int i = 0; i <= 4; i++) {
            score = ArcadeMachine.runOneGame(game, level, visuals, geneticOptimised, recordActionsFile, seed, 0)[1];
            System.out.println(score);
        }
    }
}
