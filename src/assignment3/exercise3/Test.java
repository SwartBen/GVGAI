package assignment3.exercise3;

import java.util.Random;
import core.game.Game;
import core.logging.Logger;
import tools.Utils;
import tracks.ArcadeMachine;
import core.game.StateObservation;
import ontology.Types;
import ontology.Types.ACTIONS;
import tracks.multiPlayer.tools.heuristics.SimpleStateHeuristic;
import tracks.singlePlayer.tools.Heuristics.WinScoreHeuristic;
import tracks.singlePlayer.tools.Heuristics.StateHeuristic;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;  // Import the File class
import java.io.IOException;  // Import the IOException class to handle errors
import java.io.FileWriter;   // Import the FileWriter class

public class Test {

    public static void main(String[] args) {
        
		
		//Writing Files Example
		/*
		scores = [0, 1, 2];
		sd =     [0, 2, 2];
		gameNames = ["test", "test", "test", "test"];

		score1 = []
		score2 = []
		score3 = []

		for each game
			string fileName = gameNames[i] + ".txt";
			FileWriter myWriter = new FileWriter(new File("src/assignment3/results/exercise2-results", fileName));
			fileString = "Game-Name\n";

			for each game level
				fileString += "Level i\n";

				for (i = 0; i < 10; i++) 
					if(numCalls <= 200,000)
						score1.add(score);
						score2.add(score);
						score3.add(score);
					if(numCalls > 200,000 && numCalls <= 1,000,000 )
						score2.add(score);
						score3.add(score);
					if(numCalls > 1,000,000 && numCalls <= 5,000,000)
						score3.add(score);
			
				fileString += "Average Score (200,000): score1_avg\n";
				fileString += "Standard Deviation (200,000): sd1\n";

				fileString += "Average Score (1,000,000): score2_avg\n";
				fileString += "Standard Deviation (1,000,000): sd2\n";

				fileString += "Average Score (5,000,000): score3_avg\n";
				fileString += "Standard Deviation (5,000,000): sd3\n";
			
			myWriter.write(fileString);
			myWriter.close();
		*/

		// WRITING TO FILES
		// try {
		// 	FileWriter myWriter = new FileWriter(new File("src/assignment3/results/exercise2-results", "filename.txt"));
		// 	myWriter.write("Files in Java might be tricky, but it is fun enough!\n Test");
		// 	myWriter.close();
		// 	System.out.println("Successfully wrote to the file.");
		//   } catch (IOException e) {
		// 	System.out.println("An error occurred.");
		// 	e.printStackTrace();
		// }

		Random randomGenerator = new Random();
		// Track:
		String theController = "assignment3.exercise2.Agent";

		//Load available games
		String spGamesCollection =  "examples/all_games_sp.csv";
		String[][] games = Utils.readGames(spGamesCollection);

		//Game settings
		boolean visuals = false;
		int seed = new Random().nextInt();

		// Game and level to play
		int gameIdx = 0;
		int levelIdx = 0; 
		String gameName = games[gameIdx][1];
		String game = games[gameIdx][0];

		String levelZero = game.replace(gameName, gameName + "_lvl" + levelIdx);

		String recordActionsFile = null; // "actions_" + games[gameIdx] + "_lvl"
						// + levelIdx + "_" + seed + ".txt";
						// where to record the actions
						// executed. null if not to save.

		// This plays a game in a level by the controller.
		double[] allScores = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		double runningScore = 0;
		System.out.println("RUNNING");
		
		HashMap<Integer, Types.ACTIONS> action_mapping = new HashMap<>();

		//Get initial game state and stateObs
		Game gameInstance = ArcadeMachine.runOneGame2(game, levelZero, visuals, theController, recordActionsFile, seed, 0);
		StateObservation stateObs = gameInstance.getObservation();
		
		MultiObjective runController = new MultiObjective(stateObs);
		runController.act(stateObs);

        // for(int i = 0; i < 5; i++) {
		// 	System.out.println(i);
		// 	String level = game.replace(gameName, gameName + "_lvl" + i);
		// 	runningScore += ArcadeMachine.runOneGame(game, levelZero, visuals, EAController, recordActionsFile, seed, 0)[1];
		// }
		// System.out.println("SCORE");
		// System.out.println(runningScore);

		// double mean = runningScore/5;

		// //Print mean 
		// System.out.print("Mean: ");
		// System.out.println(mean);

		// //Print and calculate standard deviation
		// double mae = 0;
		// for(int i = 0; i < 5; i++) {
		// 	mae = mae + Math.pow(allScores[i] - mean, 2);
		// }

		// double stddev = Math.sqrt(mae/1);
		// System.out.print("Stddev: ");
		// System.out.println(stddev);

		// //Print Scores
		// for(int i = 0; i < 5; i++) {
		// 	System.out.print("Scores: ");
		// 	System.out.print(allScores[i]);
		// 	System.out.print(" ");
		// }
    }
}
