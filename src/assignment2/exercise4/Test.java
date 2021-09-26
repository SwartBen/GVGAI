package assignment2.exercise4;
import java.util.Random;

import tools.Utils;
import tracks.ArcadeMachine;

public class Test {

    public static void main(String[] args) {
                    
        //Testing setup
        String ownController = "assignment2.exercise4.Agent";
        String spGamesCollection =  "examples/all_games_sp.csv";
        String[][] games = Utils.readGames(spGamesCollection);
        boolean visuals = true;
        int seed = new Random().nextInt();


        String recordActionsFile = null; // "actions_" + games[gameIdx] + "_lvl"
                        // + levelIdx + "_" + seed + ".txt";
                        // where to record the actions
                        // executed. null if not to save.

        /*----------------- TEST OWN CONTROLLER ----------------------------*/
        //String levelNum = game.replace(gameName, gameName + "_lvl" + 0);
        
        int[] gameIdx = {0, 11, 13, 18};

        //For each game
        for (int gameid = 0; gameid <= 3; gameid++) {
            
            System.out.print("-----------GAME-------------- => ");
            System.out.println(gameid);

            String gameName = games[gameIdx[gameid]][1];
            String game = games[gameIdx[gameid]][0];
            
            //For each level
            for (int levelNum = 0; levelNum <= 4; levelNum ++) {

                System.out.print("LEVEL: ");
                System.out.println(levelNum);

                String level = game.replace(gameName, gameName + "_lvl" + levelNum);
                double[] scores = new double[5];

                //Run each level 5 times
                for (int i = 0; i <= 4; i++) {
                    scores[i] = ArcadeMachine.runOneGame(game, level, visuals, ownController, recordActionsFile, seed, 0)[1];
                }

                //Calculate average
                double total_score = 0;
                for (int j = 0; j < scores.length; j++) {
                    total_score += scores[j];
                }
                double mean = total_score / 5;
                System.out.print("Average Score: ");
                System.out.println(mean);
                
                //Calculate Standard Deviation
                double std = 0;
                for (int j = 0; j < scores.length; j++) {
                    std += Math.pow(scores[j] - mean, 2);
                } 
                System.out.print("STD: ");
                System.out.println(Math.sqrt(std/scores.length));
            }
        }
    }
}
