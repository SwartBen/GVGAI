package tracks.singlePlayer.assignment2.exercise4;
import java.util.Random;

import tools.Utils;
import tracks.ArcadeMachine;

public class Test {

    public static void main(String[] args) {
        
        System.out.println("OWN CONTROLLER");
            
        //Testing setup
        String ownController = "tracks.singlePlayer.assignment2.ownController.Agent";
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

        /*----------------- TEST OWN CONTROLLER ----------------------------*/
        String levelNum = game.replace(gameName, gameName + "_lvl" + 1);
        int score = 0;
        for (int i = 0; i <= 4; i++) {
            System.out.println("LEVEL: ");
            System.out.println(i);

            String level = game.replace(gameName, gameName + "_lvl" + i);
            score += ArcadeMachine.runOneGame(game, levelNum, visuals, ownController, recordActionsFile, seed, 0)[1];
        }

        System.out.println(score);
    }
}