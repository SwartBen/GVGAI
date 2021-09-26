package assignment2.exercise4;
import java.util.Random;

public class Individual implements Comparable {

    public int[] actions; // actions in individual. length of individual = actions.length
    public int nLegalActions; // number of legal actions
    public double value;
    private Random gen;

    //Initialise individual
    Individual(int L, int nLegalActions, Random gen) {
        actions = new int[L];
        for (int i = 0; i < L; i++) {
            actions[i] = gen.nextInt(nLegalActions);
        }
        this.nLegalActions = nLegalActions;
    }   

    //Set individuals actions
    public void setActions (int[] a) {
        System.arraycopy(a, 0, actions, 0, a.length);
    }

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