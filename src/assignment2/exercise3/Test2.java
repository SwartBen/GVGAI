package assignment2.exercise3;

import java.util.Random;

import core.logging.Logger;
import tools.Utils;
import tracks.ArcadeMachine;
import java.util.concurrent.ThreadLocalRandom;

public class Test2 {

    public static double[] temp_individual = {0.9, 7, 5, 0.1, 0.14};

    public static void main(String[] args) {

        /*----------------- TESTING VARIABLES SETUP ----------------------------*/

        String geneticOptimised = "assignment2.exercise3.Agent";
        String spGamesCollection =  "examples/all_games_sp.csv";
        String[][] games = Utils.readGames(spGamesCollection);
        boolean visuals = false;
        int seed = new Random().nextInt();
        int gameIdx = 11;
        String gameName = games[gameIdx][1];
        String game = games[gameIdx][0];
        Random randomNumGen = new Random();
        String recordActionsFile = null;
        
        
        //Initialise population - just start with the pre-set params I guess
        double[] best_individual = Test.temp_individual;
        double[] scores = new double[5];
        double score = 0;
        
        //Run each level
        for (int lvl = 0; lvl < 5; lvl++) {
            String level = game.replace(gameName, gameName + "_lvl" + lvl);
            //Run each level 5 times
            for (int i = 0; i < 5; i++) {
                scores[i] = ArcadeMachine.runOneGame(game, level, visuals, geneticOptimised, recordActionsFile, seed, 0)[1];
            }

            System.out.print("LEVEL: ");
            System.out.println(lvl);

            //Calculate average
            double total_score = 0;
            for (int j = 0; j < scores.length; j++) {
                total_score += scores[j];
            }
            double mean = total_score / 5;
            System.out.print("Average Score: ");
            System.out.println(mean);
            
            double std = 0;
            for (int j = 0; j < scores.length; j++) {
                std += Math.pow(scores[j] - mean, 2);
            } 
            System.out.print("STD: ");
            System.out.println(Math.sqrt(std/scores.length));
        }
    }
}
