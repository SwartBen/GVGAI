package assignment2.exercise2;


import tracks.singlePlayer.tools.Heuristics.SimpleStateHeuristic;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Utils;
import java.util.ArrayList;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 21:45
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Agent extends AbstractPlayer {

    public double epsilon = 1e-6;
    public Random m_rnd;

    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        m_rnd = new Random();
    }

    /**
     *
     * Very simple mutli-step lookahead agent.
     *
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        SimpleStateHeuristic heuristic;
        Types.ACTIONS bestAction = null;
        double maxQ = Double.NEGATIVE_INFINITY;
        ArrayList<Double> scores = new ArrayList<>();
        ArrayList<Types.ACTIONS> actions = new ArrayList<>();
        double score;
        
        for (Types.ACTIONS action : stateObs.getAvailableActions()) {

            //copies the states observation
            StateObservation stCopy = stateObs.copy(); 
            
            //advance action
            stCopy.advance(action); 

            //Evaluate action
            heuristic = new SimpleStateHeuristic(stCopy);
            score = heuristic.evaluateState(stCopy);
            score = Utils.noise(score, this.epsilon, this.m_rnd.nextDouble());

            //store the score and action
            scores.add(heuristic.evaluateState(stCopy));
            actions.add(action);

            /*--------------------- Start of Second Step -----------------*/
            for (Types.ACTIONS nextAction : stCopy.getAvailableActions()) {

                //make copy of next actions
                StateObservation nextActionCopy = stCopy.copy();

                //advance next action                
                nextActionCopy.advance(nextAction);

                //evaluate next action                
                heuristic = new SimpleStateHeuristic(nextActionCopy);
                score = heuristic.evaluateState(nextActionCopy);
                score = Utils.noise(score, this.epsilon, this.m_rnd.nextDouble());
                
                //store the score and action
                scores.add(score);
                actions.add(nextAction);

            }

            if(elapsedTimer.remainingTimeMillis() < 5) {
                break;
            }
        }
        //Retrieve best action
        double best = scores.get(0);
        int bestIndex = 0;

        for (int i = 1; i < scores.size(); i++) {
        //  System.out.println(scores.get(i));
            if (scores.get(i) > best) {
                best = scores.get(i);
                bestIndex = i;
            }
        }
        // System.out.println("----------");
        // System.out.println(actions.get(bestIndex));

        return actions.get(bestIndex);
    }

}