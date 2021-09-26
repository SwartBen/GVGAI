package tracks.singlePlayer.assignment2.exercise3;
import java.util.Random;

import core.logging.Logger;
import tools.Utils;
import tracks.ArcadeMachine;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created with IntelliJ IDEA. User: Diego Date: 04/10/13 Time: 16:29 This is a
 * Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Test {

    public static double[] temp_individual = {0.9, 7, 5, 0.1, 0.14};
    
    public static void main(String[] args) {

        System.out.println("START GENETIC ALGORITHM OPTIMISED");
		
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
        double best_score = 0;
        double temp_score = 0;
        int successful_mutations = 1;
        int mutations_count = 1;
        double variation = 1;
        double c = 0.8;

        //POPULATUION INITALISATION => Game plays levels 0-4 of specified game
        for (int i = 0; i <= 4; i++) {
            String level = game.replace(gameName, gameName + "_lvl" + i);
            best_score += ArcadeMachine.runOneGame(game, level, visuals, geneticOptimised, recordActionsFile, seed, 0)[1];
        }
        
        //RUN ALGORITHM
        int gen_count = 0;
        while (gen_count < 10) {
            
            System.out.println("GENERATION: ");
            System.out.println(gen_count);
            
            //Create new individual (mutation)
            Test.temp_individual[0] = (Test.temp_individual[0] + Math.abs(randomNumGen.nextGaussian())*variation) % 1; //gamma
            Test.temp_individual[1] = Math.floor(Test.temp_individual[1] + Math.abs(randomNumGen.nextGaussian())*variation); //sim depth
            Test.temp_individual[2] = Math.floor(Test.temp_individual[2] + Math.abs(randomNumGen.nextGaussian())*variation); //pop size 
            Test.temp_individual[3] = (Test.temp_individual[3] + Math.abs(randomNumGen.nextGaussian())*variation) % 1; //recprob
            Test.temp_individual[4] = (1/Test.temp_individual[1]) % 1; //mut

            //Run new individual
            for (int i = 0; i <= 4; i++) {
                String level = game.replace(gameName, gameName + "_lvl" + i);
                temp_score += ArcadeMachine.runOneGame(game, level, visuals, geneticOptimised, recordActionsFile, seed, 0)[1];
            }

            //Evaluate score of new individual compared to the best found
            if (best_score < temp_score) {
                successful_mutations++;
                best_score = temp_score;
                best_individual = temp_individual;
            }
            
            //Reset mutation step size rule every 5 mutations??
            if(gen_count % 5 == 0) {
                successful_mutations = 1;
                mutations_count = 1;
            }
            
            //Co-Evolve mutation step size
            if(successful_mutations / mutations_count > 1/5 )
                variation /= c;
            else if(successful_mutations / mutations_count < 1/5 )
                variation *= c;

            temp_score = 0;
            gen_count++;
        }

        /*----------------- RESULTS OUTPUT ----------------------------*/

        System.out.println("OPTIMISED PARAMETERS");

        System.out.print("GAMMA: ");
        System.out.println(best_individual[0]);
        System.out.print("SIM_DEPTH: ");
        System.out.println(best_individual[1]);
        System.out.print("POP_SIZE: ");
        System.out.println(best_individual[2]);
        System.out.print("RECPROB: ");
        System.out.println(best_individual[3]);
        System.out.print("MUT: ");
        System.out.println(best_individual[4]);

    return;
    }
}
