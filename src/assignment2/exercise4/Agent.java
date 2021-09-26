package assignment2.exercise4;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tracks.singlePlayer.tools.Heuristics.StateHeuristic;
import tracks.singlePlayer.tools.Heuristics.WinScoreHeuristic;

import java.util.*;

@SuppressWarnings("FieldCanBeLocal")
public class Agent extends AbstractPlayer {

    // Parameters
    private int POPULATION_SIZE = 10;
    private int SIMULATION_DEPTH = 10;
    private StateHeuristic heuristic;

    // Constants
    private final long BREAK_MS = 10;
    public static final double epsilon = 1e-6;

    // Class vars
    private ArrayList<Individual> population, newPop;
    private ArrayList<Double> pop_fitness, new_fitness;
    private int N_ACTIONS;
    private HashMap<Integer, Types.ACTIONS> action_mapping;
    private Random randomGenerator;

    // Budgets
    private ElapsedCpuTimer timer;
    private double avgTimeTaken = 0;
    private long remaining;

    /**
     * Public constructor with state observation and time due.
     *
     * @param stateObs     state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        randomGenerator = new Random();
        heuristic = new WinScoreHeuristic(stateObs);
        this.timer = elapsedTimer;

        //Initialise actions
        N_ACTIONS = stateObs.getAvailableActions().size();
        action_mapping = new HashMap<>();
        int k = 0;
        for (Types.ACTIONS action : stateObs.getAvailableActions()) {
            action_mapping.put(k, action);
            k++;
        }
        action_mapping.put(k, Types.ACTIONS.ACTION_NIL);

    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        this.timer = elapsedTimer;
        avgTimeTaken = 0;
        remaining = timer.remainingTimeMillis();
        
        //Initialise pop
        population = intialise_population();

        //Calculate fitness of entire population
        pop_fitness = new ArrayList<Double>();
        for (int i = 0; i < population.size(); i++)
            pop_fitness.add(evaluate(population.get(i), heuristic, stateObs));
    
        //Loop over generations until termination conditon is met.
        remaining = timer.remainingTimeMillis();
        int generation = 0;
        boolean break_generation = false;
        
        while (remaining > avgTimeTaken && remaining > BREAK_MS && !break_generation) {

            newPop = new ArrayList<Individual>();

            //Elitism select
            int elitismIndex = getMaxIndex(pop_fitness);
            newPop.add(population.get(elitismIndex));

            //Iterate over population

            for (int i = 1; i < population.size(); i++) {
                //Parent Select - Tournament
                Individual parent1 = tournament_select(population, pop_fitness);
                Individual parent2 = tournament_select(population, pop_fitness);
                while (parent2 == parent1)
                    parent2 = tournament_select(population, pop_fitness);

                //Crossover - uniform
                //Individual child = uniform_crossover(parent1, parent2);

                //Crossover - one_point_crossover
                //Individual child = one_point_crossover(parent1, parent2, stateObs);
                
                //Crossover - two_point_crossover
                Individual child =  two_point_crossover(parent1, parent2, stateObs);
                
                //Mutation - swap
                //child = swap_mutate(child);
                
                //Mutation - random
                child = random_mutate(child);

                newPop.add(child);
                evaluate(child, heuristic, stateObs);

                remaining = timer.remainingTimeMillis();

                if(remaining < BREAK_MS) {
                    break_generation = true;
                    break; 
                }
            }

            population = newPop;
            
            //Calculate fitness of entire population
            new_fitness = new ArrayList<Double>();
            for (int i = 0; i < population.size(); i++) {
                new_fitness.add(evaluate(population.get(i), heuristic, stateObs));
            }
            pop_fitness = new_fitness;

            generation++;
            remaining = timer.remainingTimeMillis();
            avgTimeTaken = timer.elapsedMillis()/generation;
        }

        // System.out.println("----------");
        // System.out.println("----------");
        // System.out.println("----------");
        // for (int i = 0; i < pop_fitness.size(); i++)
        //     System.out.println(pop_fitness.get(i));
        
        // System.out.println("----------");
        // System.out.println(pop_fitness.get(getMaxIndex(pop_fitness)));
        // System.out.println("----------");

        // RETURN ACTION
        int bestIndex = getMaxIndex(pop_fitness);
        int bestAction = population.get(bestIndex).actions[0]; 
        return action_mapping.get(bestAction); 
    }

    //Creates a population of individuals with random actions
    private ArrayList<Individual> intialise_population() {

        population = new ArrayList<Individual>();

        for (int i = 0; i < POPULATION_SIZE; i++) 
            population.add(new Individual(SIMULATION_DEPTH, N_ACTIONS, randomGenerator));
        
        return population;
    }

    

    /**
     * Evaluates an individual by rolling the current state with the actions in the individual
     * and returning the value of the resulting state; random action chosen for the opponent
     * @param individual - individual to be valued
     * @param heuristic - heuristic to be used for state evaluation
     * @param state - current state, root of rollouts
     * @return - value of last state reached
     */
    private double evaluate(Individual individual, StateHeuristic heuristic, StateObservation state) {

        StateObservation st = state.copy();
        double acum = 0, avg;

        for (int i = 0; i < SIMULATION_DEPTH; i++) {
            if (! st.isGameOver()) {
                ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
                st.advance(action_mapping.get(individual.actions[i]));

                acum += elapsedTimerIteration.elapsedMillis();
                avg = acum / (i+1);
                remaining = timer.remainingTimeMillis();
                if (remaining < 2*avg || remaining < BREAK_MS) break;
            } else {
                break;
            }
        }

        individual.value = heuristic.evaluateState(st);
        return individual.value;
    }

    //Crossover method. Loops over action array of mum and dad with an equal change to take action from either parent.
    private Individual uniform_crossover(Individual mum, Individual dad) {
        Individual child = new Individual(SIMULATION_DEPTH, N_ACTIONS, randomGenerator);

        for (int i = 0; i < mum.actions.length; i++) {
            if (randomGenerator.nextDouble() < 0.5)
                child.actions[i] = mum.actions[i];
            else 
                child.actions[i] = dad.actions[i];
        }

        return child;
    }

    //Crossover method. Splits parent action arrays. Child is comprised of part of the mum, and part of the dad.
    private Individual one_point_crossover(Individual mum, Individual dad, StateObservation state) {
        Individual child1 = new Individual(SIMULATION_DEPTH, N_ACTIONS, randomGenerator);
        Individual child2 = new Individual(SIMULATION_DEPTH, N_ACTIONS, randomGenerator);

        int splitIndex = randomGenerator.nextInt(mum.actions.length - 3) + 1;

        for (int i = 0; i < splitIndex; i++) {
                child1.actions[i] = mum.actions[i];
                child2.actions[i] = dad.actions[i];
        }
        for (int i = splitIndex; i < mum.actions.length; i++) {
                child1.actions[i] = dad.actions[i];
                child2.actions[i] = mum.actions[i];
        }

        //Returns best child
        if (evaluate(child1, heuristic, state) > evaluate(child2, heuristic, state))
            return child1;
        else 
            return child2;
    }

    //Crossover method. Selects two points to split the mum and dad array.
    //Creates child based on the split points.
    private Individual two_point_crossover(Individual mum, Individual dad, StateObservation state) {
        
        Individual child1 = new Individual(SIMULATION_DEPTH, N_ACTIONS, randomGenerator);
        Individual child2 = new Individual(SIMULATION_DEPTH, N_ACTIONS, randomGenerator);

        //Pick two random index's
        int smallIndex = randomGenerator.nextInt(mum.actions.length - 3) + 1;
        int bigIndex = randomGenerator.nextInt(mum.actions.length - 3) + 1;
        int temp;


        //If same index chosen, pick again
        while(smallIndex == bigIndex)
            bigIndex = randomGenerator.nextInt(mum.actions.length - 3) + 1;
        
        //ensure small index is smaller than big index
        if(smallIndex > bigIndex) {
            temp = smallIndex;
            smallIndex = bigIndex;
            bigIndex = temp;
        }

        // System.out.println("-------------");
        // System.out.println(bigIndex);
        // System.out.println(smallIndex);
        // System.out.println("-------------");

        //Create child action arrays
        int count = 0;
        for (int i = count; i < smallIndex; i++) {
            child1.actions[i] = mum.actions[i];
            child2.actions[i] = dad.actions[i];
            count++;
        }
        for (int i = count; i < bigIndex; i++) {
            child1.actions[i] = dad.actions[i];
            child2.actions[i] = mum.actions[i];
            count++;
        }
        for (int i = count; i < mum.actions.length; i++) {
            child1.actions[i] = mum.actions[i];
            child2.actions[i] = dad.actions[i];
            count++;
        }

        //Returns best child
        if (evaluate(child1, heuristic, state) > evaluate(child2, heuristic, state))
            return child1;
        else 
            return child2;
    }

    //Parent selection method => Tournament select
    //Selects 3 random individuals from population. Best individual returned for breeding.
    private Individual tournament_select(ArrayList<Individual> population, ArrayList<Double> pop_fitness) {

        ArrayList<Individual> tournament = new ArrayList<>();
        ArrayList<Double> tournament_fitness = new ArrayList<>();


        for (int i = 0; i < 3; i++) {
            int index = randomGenerator.nextInt(population.size());
            tournament.add(population.get(index));
            tournament_fitness.add(pop_fitness.get(index));
        }

        int bestIndex = getMaxIndex(tournament_fitness);

        return tournament.get(bestIndex);
    }

    //Mutation method. Swaps to random actions in the individuals action array.
    private Individual swap_mutate(Individual individual) {
       
        int index1 = randomGenerator.nextInt(individual.actions.length);
        int index2 = randomGenerator.nextInt(individual.actions.length);

        while (index1 == index2) 
            index2 = randomGenerator.nextInt(individual.actions.length);
        
        int temp = index1;
        individual.actions[index1] = individual.actions[index2];
        individual.actions[index2] = individual.actions[temp];
        
    return individual;
    }
    
    //Mutation method. Randomly mutates actions in the individuals action array.
    private Individual random_mutate(Individual individual) {
       
        //Loop over action array
        for (int i = 0; i < individual.actions.length; i++) {
            //Mutate
            if (randomGenerator.nextDouble() < 0.5) {
                int newActionIndex = randomGenerator.nextInt(individual.nLegalActions);
                individual.actions[i] = individual.actions[newActionIndex];
            }
        }
        
    return individual;
    }

    //Returns index of best fitness in population
    private int getMaxIndex(ArrayList<Double> fitness) {

        double max = fitness.get(0);
        int maxIndex = 0;
        for (int i = 1; i < fitness.size(); i++) {
            if(fitness.get(i) > max) {
                max = fitness.get(i);
                maxIndex = i;
            }
        }
        return maxIndex;
    }

}