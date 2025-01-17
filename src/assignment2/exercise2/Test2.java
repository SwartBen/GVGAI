package assignment2.exercise2;

import java.util.Random;

import core.logging.Logger;
import tools.Utils;
import tracks.ArcadeMachine;
import java.util.concurrent.ThreadLocalRandom;

public class Test2 {
    public static void main(String[] args) {
        
        System.out.println("MULTI STEP LOOKAHEAD CONTROLLER");
            
        //Testing setup
        String  severalsteplookahead = "assignment2.exercise2.Agent";
        String spGamesCollection =  "examples/all_games_sp.csv";
        String[][] games = Utils.readGames(spGamesCollection);
        boolean visuals = true;
        int seed = new Random().nextInt();
        
        int gameIdx = 0;
        String gameName = games[gameIdx][1];
        String game = games[gameIdx][0];

        String recordActionsFile = null; // "actions_" + games[gameIdx] + "_lvl"
                        // + levelIdx + "_" + seed + ".txt";
                        // where to record the actions
                        // executed. null if not to save.

        /*----------------- TEST MULTI STEP LOOK AHEAD CONTROLLER ----------------------------*/

        int score = 0;

        //Loop over game and calculate score
        for (int i = 0; i <= 4; i++) {
            System.out.println("GENERATION: ");
            System.out.println(i);

            String level = game.replace(gameName, gameName + "_lvl" + i);
            score += ArcadeMachine.runOneGame(game, level, visuals, severalsteplookahead, recordActionsFile, seed, 0)[1];
        }

        //Output score
        System.out.println(score);
    }
}
