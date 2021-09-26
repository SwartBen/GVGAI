package assignment2.exercise2;

import java.util.Random;

import core.logging.Logger;
import tools.Utils;
import tracks.ArcadeMachine;

public class Test {

    public static void main(String[] args) {
    // Available tracks:
		String sampleRandomController = "tracks.singlePlayer.simple.sampleRandom.Agent";
		String doNothingController = "tracks.singlePlayer.simple.doNothing.Agent";
		String sampleOneStepController = "tracks.singlePlayer.simple.sampleonesteplookahead.Agent";
		String sampleFlatMCTSController = "tracks.singlePlayer.simple.greedyTreeSearch.Agent";
		String sampleGAController = "tracks.singlePlayer.deprecated.sampleGA.Agent";
		String sampleSeveralStepController = "assignment2.exercise2.Agent";

		String sampleMCTSController = "tracks.singlePlayer.advanced.sampleMCTS.Agent";
        String sampleRSController = "tracks.singlePlayer.advanced.sampleRS.Agent";
        String sampleRHEAController = "tracks.singlePlayer.advanced.sampleRHEA.Agent";
		String sampleOLETSController = "tracks.singlePlayer.advanced.olets.Agent";

		//Load available games
		String spGamesCollection =  "examples/all_games_sp.csv";
		String[][] games = Utils.readGames(spGamesCollection);

		//Game settings
		boolean visuals = true;
		int seed = new Random().nextInt();

		// Game and level to play
		int gameIdx = 0;
		int levelIdx = 0; // level names from 0 to 4 (game_lvlN.txt).
		String gameName = games[gameIdx][1];
		String game = games[gameIdx][0];

		String level1 = game.replace(gameName, gameName + "_lvl" + levelIdx);

		String recordActionsFile = null; // "actions_" + games[gameIdx] + "_lvl"
						// + levelIdx + "_" + seed + ".txt";
						// where to record the actions
						// executed. null if not to save.

		// 1. This starts a game, in a level, played by a human.
		//ArcadeMachine.playOneGame(game, level1, recordActionsFile, seed);

		// 2. This plays a game in a level by the controller.
		double[] allScores = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		double runningScore = 0;
		System.out.println("RUNNING");
		
        for(int i = 0; i < 5; i++) {
			System.out.println(i);
			String level = game.replace(gameName, gameName + "_lvl" + i);
			runningScore += ArcadeMachine.runOneGame(game, level1, visuals, sampleOneStepController, recordActionsFile, seed, 0)[1];
		}
		System.out.println("SCORE");
		System.out.println(runningScore);

		double mean = runningScore/5;

		//Print mean 
		System.out.print("Mean: ");
		System.out.println(mean);

		//Print and calculate standard deviation
		double mae = 0;
		for(int i = 0; i < 5; i++) {
			mae = mae + Math.pow(allScores[i] - mean, 2);
		}

		double stddev = Math.sqrt(mae/1);
		System.out.print("Stddev: ");
		System.out.println(stddev);

		//Print Scores
		for(int i = 0; i < 5; i++) {
			System.out.print("Scores: ");
			System.out.print(allScores[i]);
			System.out.print(" ");
		}
    }
}
