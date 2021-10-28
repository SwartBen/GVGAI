package assignment3.exercise3;
import java.util.ArrayList;
import java.util.Random;

public class Individual implements Comparable {

    public int[] actions; // actions in individual. length of individual = actions.length
    public int nLegalActions; // number of legal actions
    public double value;
    public Double crowding_distance;
    public int domination_count;
    public ArrayList<Individual> dominated_solutions;
    public int rank;
    public int objectives;
    private Random gen;

    //Initialise individual
    Individual(int L, int nLegalActions, Random gen) {
        actions = new int[L];
        for (int i = 0; i < L; i++) {
            actions[i] = gen.nextInt(nLegalActions);
        }
        this.nLegalActions = nLegalActions;
    }   

    public boolean dominates(Individual other_individual) {

        return true;
    }

    //Set individuals actions
    public void setActions (int[] a) {
        actions = new int[a.length];
        System.arraycopy(a, 0, actions, 0, a.length);
    }

    public void addOneAction(int[] src, int N_ACTIONS, Random randomGen) {
        actions = new int[src.length + 1];

        for (int i = 0; i < src.length; i++) {
            actions[i] = src[i];
        }
        actions[actions.length-1] = randomGen.nextInt(N_ACTIONS);
    }
    
    // public void addMove() {
    //     int[] newActions = new int[actions.length];

    //     int i = 0;
    //     for (i = 0; i < actions.length; i++) {
    //         newActions[i] = actions[i];
    //     }
    //     //newActions[i] = gen.nextInt(nLegalActions);;
    //     actions = newActions;
    // }
    //Compares two individuals value.
    @Override
    public int compareTo(Object o) {
        Individual a = this;
        Individual b = (Individual)o;
        return Double.compare(b.value, a.value);
    }


    //Show individual as a string => for debugging
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("" + value + ": ");
        for (int action : actions) s.append(action).append(" ");
        return s.toString();
    }
}