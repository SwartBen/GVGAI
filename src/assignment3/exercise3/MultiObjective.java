package assignment3.exercise3;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tracks.singlePlayer.tools.Heuristics.SimpleStateHeuristic;
import tracks.singlePlayer.tools.Heuristics.StateHeuristic;
import tracks.singlePlayer.tools.Heuristics.WinScoreHeuristic;

import java.util.*;

public class MultiObjective {

    //NSGA-II variables

    private Population population, next_population;
    //private ArrayList<Individual> returned_population;

    
    // Parameters
    private int calltoAdvance;
    private int POPULATION_SIZE = 20;
    private int SIMULATION_DEPTH = 40;
    private StateHeuristic heuristic;

    // Constants
    public static final double epsilon = 1e-6;

    // Class vars
    private ArrayList<Double> population_fitness, next_population_fitness;
    private int N_ACTIONS;
    private HashMap<Integer, Types.ACTIONS> action_mapping;
    private Random randomGenerator;

    /**
     * Public constructor with state observation and time due.
     *
     * @param stateObs     state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    public MultiObjective(StateObservation stateObs) {
        calltoAdvance = 0;
        randomGenerator = new Random();
        heuristic = new WinScoreHeuristic(stateObs);
        
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

    public Types.ACTIONS[] evolve(StateObservation stateObs) {

        //Initialise population
        population = intialise_population();

        //Fast non dominated sort 
        fast_nondominated_sort(population);

        //Calculate crowding distance
        for (ArrayList<Individual> front : population.fronts)
            calculate_crowding_distance(front);

        //Get children
        ArrayList<Individual> children = create_children(population.population);     

        //Set new population to null
        Population returned_population = null;
        
        //Until termination condition is met
        while(calltoAdvance < 5000000) {
            //add children to population
            population.population.addAll(children);

            //fast non dominated sort population
            fast_nondominated_sort(population);
            //Create new pop
            Population new_population = new Population();
            int front_count = 0;

            while (new_population.population.size() + population.fronts.get(front_count).size() <= POPULATION_SIZE) {
                //calculate crowding distance
                calculate_crowding_distance(population.fronts.get(front_count));
                new_population.population.addAll(population.fronts.get(front_count));
                front_count += 1;
            }
            calculate_crowding_distance(population.fronts.get(front_count));

            //fronts[front_count].sort(key=lambda individual: individual.crowding_distance, reverse=True)
            //new_population.extend(fronts[front_num][0:POPULATION_SIZE-new_population.length])
            returned_population = new_population;
            population = new_population;
            fast_nondominated_sort(population);

            for (ArrayList<Individual> front : population.fronts) 
                calculate_crowding_distance(front);
            
            children = create_children(population.population);
        }

        //return returned_population.fronts[0];
        Types.ACTIONS returnVal[] = {Types.ACTIONS.ACTION_NIL};
        return returnVal;
    }

    //Creates a population of individuals with random actions
    private Population intialise_population() {

        population = new Population();

        for (int i = 0; i < POPULATION_SIZE; i++) 
            population.population.add(new Individual(SIMULATION_DEPTH, N_ACTIONS, randomGenerator));
        
        return population;
    }

    private void fast_nondominated_sort(Population population) {
        population.fronts.add( new ArrayList<Individual>());
        for (Individual individual: population.population) {
            individual.domination_count = 0;
            individual.dominated_solutions = new ArrayList<Individual>();
            
            for(Individual other_individual: population.population) {
                if(individual.dominates(other_individual)) {
                    individual.dominated_solutions.add(other_individual);
                }
                else if (other_individual.dominates(individual)) {
                    individual.domination_count += 1;
                }
            }
            if(individual.domination_count == 0) {
                individual.rank = 0;
                population.fronts.get(0).add(individual);
            }
        }
        int i = 0;
        while(population.fronts.get(i).size() > 0) {
            ArrayList<Individual> temp = new ArrayList<Individual>();
            for(Individual individual: population.fronts.get(i)) {
                for (Individual other_individual: individual.dominated_solutions) {
                    other_individual.domination_count -= 1;
                    if (other_individual.domination_count == 0) {
                        other_individual.rank = i+1;
                        temp.add(other_individual);
                    }
                }
                i = i+1;
                population.fronts.add(temp);
            }
        }
    }

    private void calculate_crowding_distance(ArrayList<Individual> front) {
        if (front.size() > 0) {
            int solutions_num = front.size();
            for (Individual individual: front) {
                individual.crowding_distance = 0;

                for (int m = 0; m < (front.get(0).objectives).size(); m++) {
                    //front.sort(key=lambda individual: individual.objectives[m]);
                    front.get(0).crowding_distance = Math.pow(10,9);
                    front.get(solutions_num-1).crowding_distance = Math.pow(10,9);
                    
                    ArrayList<Integer> m_values = new ArrayList<Integer>();
                    
                    for(Individual indv : front) {
                        m_values.add(indv.objectives.get(m));
                    }
                   
                    int scale = Collections.max(m_values) - Collections.min(m_values);
                    
                    if (scale == 0) 
                        scale = 1;

                    for (int i = 1; i < solutions_num-1; i++)
                        front.get(i).crowding_distance += (front.get(i+1).objectives[m] - front.get(i-1).objectives[m])/scale;
                }
            }
        }
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

        for (int i = 0; i < individual.actions.length; i++) {
            if (! st.isGameOver()) {
                st.advance(action_mapping.get(individual.actions[i]));
                calltoAdvance++;
            } else {
                break;
            }
        }
        
        double score = heuristic.evaluateState(st);

        if(score == 1000) 
            score = -1000;
        
        individual.value = score;
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
    private Individual[] one_point_crossover(Individual mum, Individual dad) {
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

        Individual[] returnVal = {child1, child2};
        return returnVal;
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

    public ArrayList<Individual> create_children(ArrayList<Individual> population) {
        ArrayList<Individual> children = new ArrayList<Individual>();

        while(children.size() < population.size()) {
            Individual parent1 = tournament_select(population);
            Individual parent2 = tournament_select(population);
            while(parent1==parent2)
                parent2 = tournament_select(population);

            Individual[] child = one_point_crossover(parent1, parent2);
            swap_mutate(child[0]);
            swap_mutate(child[1]);
            calculate_objectives(child[0]);
            calculate_objectives(child[1]);
            children.add(child[0]);
            children.add(child[1]);
        }
        return children;
    }

    //Parent selection method => Tournament select
    //Selects 3 random individuals from population. Best individual returned for breeding.
    private Individual tournament_select(ArrayList<Individual> population) {

        ArrayList<Individual> tournament = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            int index = randomGenerator.nextInt(population.size());
            tournament.add(population.get(index));
            tournament_fitness.add(population_fitness.get(index));
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

    

