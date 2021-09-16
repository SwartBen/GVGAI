package tracks.singlePlayer.assignment2.sampleseveralsteplookahead;


import tracks.singlePlayer.tools.Heuristics.SimpleStateHeuristic;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Utils;

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
     * Very simple one step lookahead agent.
     *
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        Types.ACTIONS bestAction = null;
        double maxQ = Double.NEGATIVE_INFINITY;
        for (Types.ACTIONS action : stateObs.getAvailableActions()) {

            //copies the states observation
            StateObservation stCopy = stateObs.copy(); 
            
            //On the copied state, implement the action
            stCopy.advance(action); 

            //Evaluate next actions of previous action
            double nextMaxQ = Double.NEGATIVE_INFINITY;
            SimpleStateHeuristic heuristic =  new SimpleStateHeuristic(stCopy);
            /*--------------------- Start of Second Step -----------------*/
            for (Types.ACTIONS nextAction : stCopy.getAvailableActions()) {

                //make copy of next actions
                StateObservation nextActionCopy = stCopy.copy();

                //evaluate next action                
                nextActionCopy.advance(nextAction);
                double nextQ = heuristic.evaluateState(nextActionCopy);
                nextQ = Utils.noise(nextQ, this.epsilon, this.m_rnd.nextDouble());

                //Set it as action if it is better
                if (nextQ > nextMaxQ) {
                    nextMaxQ = nextQ;
                }
            }
            /*---------------------End of Second Step-----------------*/

            //See if first step action is better
            if (nextMaxQ >= maxQ) {
                maxQ = nextMaxQ;
                bestAction = action;
            }
        }
        return bestAction;
    }

}