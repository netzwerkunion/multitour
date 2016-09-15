// Memetic Algorithm
// Problem Set by Andre Leicht
// Solution Method Created by Andre Leicht 2016-09-17
// Solution Method Modified by Daniel Neumann 2016-09-15

package tour.algorithm;

import java.util.ArrayList;
//import java.util.Arrays;
import java.util.Collections;
//import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class MemeticAlgorithm implements initAlgorithm<Tour> {

	// Solution Parameters
	private int generationCounter = 0; // Initialisierung des Index Generationen-Index
	private static final int POP_SIZE = 20; // Population Size
	private static final int GENERATION_LIMIT = 55000; // Limit of computed generations 
	private static final double PERIOD = 10000;
	private double mutationsReichweite = 2;
	private double explorationRatio = 1;
	
	// Problem Structure
	// VRPTW Distance Matrix between Stations
	private double[][] pathMatrice =    
								   {
								   {0,4.5,4,3.5,3,2.5,3,3.5,4,3.5,3,2.5,2,3,3.5,4,4.5},
								   {4.5,0,0.5,1,1.5,3.5,3,2.5,2,2.5,3,3.5,4,5,4.5,4,3.5},
							       {4,0.5,0,0.5,1,3,3.5,3,2.5,3,3.5,4,3.5,4.5,5,4.5,4},
								   {3.5,1,0.5,0,0.5,2.5,3,3.5,3,3.5,4,3.5,3,4,4.5,5,4.5},
								   {3,1.5,1,0.5,0,2,2.5,3,3.5,4,3.5,3,2.5,3.5,4,4.5,5},
								   {2.5,3.5,3,2.5,2,0,0.5,1,1.5,3.5,3,2.5,2,3,3.5,4,4.5},
								   {3,3,3.5,3,2.5,0.5,0,0.5,1,3,3.5,3,2.5,3.5,4,4.5,4},
								   {3.5,2.5,3,3.5,3,1,0.5,0,0.5,2.5,3,3.5,3,4,4.5,4,3.5},
								   {4,2,2.5,3,3.5,1.5,1,0.5,0,2,2.5,3,3.5,4.5,4,3.5,3},
								   {3.5,2.5,3,3.5,4,3.5,3,2.5,2,0,0.5,1,1.5,4,3.5,3,2.5},
								   {3,3,3.5,4,3.5,3,3.5,3,2.5,0.5,0,0.5,1,3.5,4,3.5,3},
								   {2.5,3.5,4,3.5,3,2.5,3,3.5,3,1,0.5,0,0.5,3,3.5,4,3.5},
								   {2,4,3.5,3,2.5,2,2.5,3,3.5,1.5,1,0.5,0,2.5,3,3.5,4},
								   {3,5,4.5,4,3.5,3,3.5,4,4.5,4,3.5,3,2.5,0,0.5,1,1.5},
								   {3.5,4.5,5,4.5,4,3.5,4,4.5,4,3.5,4,3.5,3,0.5,0,0.5,1},
								   {4,4,4.5,5,4.5,4,4.5,4,3.5,3,3.5,4,3.5,1,0.5,0,0.5},
								   {4.5,3.5,4,4.5,5,4.5,4,3.5,3,2.5,3,3.5,4,1.5,1,0.5,0} 
								   };
	// Commissioning Times to Fill a Supplier Unit
	private double[] comTimes = {6,6,6,6,5,5,5,5,4,4,4,4,2,2,2,2}; 
	
	// Latest Arrival Times to for Material at the assembly line
	private double[] maxTimes = {38,20,32,50,25,39,36,54,30,39,23,20,51,41,40,25}; 
	
	// Start Solution
	// create starting population
	@Override
	public List<Tour> createStartingPopulation() {
		List<Tour> population = new ArrayList<Tour>(); 
		Random randomizer = new Random(); 
		for (int i = 0; i < POP_SIZE; i++) 
		{
			Tour tour = new Tour(); 
			List<Integer> currentRoute = new ArrayList<Integer>(); 
			List<Integer> currentCuts = new ArrayList<Integer>();
			
			// Create MultiTourChromosome
			for (int j = 0; j < 16; j++) 
			{
				boolean found = false; 
				while(!found) 
				{
					int randomNumber = randomizer.nextInt(16) + 1; 
					if (!currentRoute.contains(randomNumber)) 
					{
						currentRoute.add(randomNumber); 
						found = true; 
					}
				}
			}
			
			// Create CutChromosome
			boolean isValidCutConfiguration = false; 
			while(!isValidCutConfiguration) 
			{
				int firstCut = randomizer.nextInt(9); 
				int secondCut = randomizer.nextInt(9); 
				int thirdCut = randomizer.nextInt(9); 
				
				if (firstCut + secondCut + thirdCut > 8) 
				{
					isValidCutConfiguration = true; 
					currentCuts.add(firstCut); 
					currentCuts.add(secondCut); 
					currentCuts.add(thirdCut); 
				}
			}
			tour.setRoute(currentRoute); 
			tour.setCuts(currentCuts); 
			population.add(tour); 
		}
		
		// Local Optimization of four randomly chosen Individuals
		List<Tour> newPop = new ArrayList<Tour>(); 
		List<Tour> optis = new ArrayList<Tour>(); 
		List<Integer> optiIDs = new ArrayList<Integer>(); 
		for (int i = 0; i < 4; i++) 
		{
			int idx = randomizer.nextInt(population.size()); 
			optiIDs.add(idx); 
			Tour t = population.get(idx); 
			t = updateEntry(t); 
			optis.add(t); 
		}
		optis = localOptimization(optis); 
		
		int optiCounter = 0; 
		for (int i = 0; i < POP_SIZE; i++) 
		{
			if (optiIDs.contains(i)) 
			{
				newPop.add(optis.get(optiCounter)); 
				optiCounter++; 
			} else {
				newPop.add(population.get(i)); 
			}
		}
		
		return newPop; 
	}

	// selection process with SUS for recombination
	@Override
	public List<List<Tour>> chooseParents(List<Tour> population) 
	{
		Random randomizer = new Random(); 
		double fitnessSum = 0; 
		List<Tour> parents = new ArrayList<Tour>(); 
		for (Tour key : population) 
		{
			fitnessSum += key.getFitness(); 
		}
		List<Integer> parentIDs = new ArrayList<Integer>(); 
		//start SUS
		for (int u = 0; u < 2; u++) 
		{
			double p = fitnessSum / 5.0; 
			double start = randomizer.nextDouble()*p; 
			double[] pointers = 
				{
						start + 0*p, 
						start + 1*p, 
						start + 2*p, 
						start + 3*p, 
						start + 4*p, 
				};
			double currentFitnessSum = 0;
			int i = -1; 
			for (double pointer : pointers) 
			{
				while (currentFitnessSum <= pointer) 
				{
					currentFitnessSum += population.get(i+1).getFitness(); 
					i++; 
				}
				Tour parent = new Tour(); 
				parent.setRoute(new ArrayList<>(population.get(i).getRoute())); 
				parent.setCuts(new ArrayList<>(population.get(i).getCuts())); 
				parentIDs.add(i);
				parents.add(parent); 
				
				
			}
		}
	
		// definition of not selected parents 
		List<Tour> notParents = new ArrayList<Tour>(); 
		for (int i = 0; i < population.size(); i++) 
		{
			if (!parentIDs.contains(i)) 
			{
				notParents.add(population.get(i)); 
			}
		}
		List<List<Tour>> pnp = new ArrayList<List<Tour>>(); 
		pnp.add(parents); 
		pnp.add(notParents); 
		return pnp;
	}

	// recombination process
	@Override
	public List<Tour> makeChildren(List<Tour> parents) {
		Random rand = new Random(); 
		List<Tour> children = new ArrayList<Tour>(); 
		// choose 2 parents and recombinate with probability ratio between 0.6 and 1 
		for (int i = 0; i < parents.size(); i = i +2 ) 
		{
			Tour parent1 = parents.get(i);  
			Tour parent2 = parents.get(i+1);  

			double rekoChance = rand.nextDouble(); 
			if (rekoChance < explorationRatio*0.4) 
			{
				Tour t = new Tour(); 
				t.setRoute(new ArrayList<Integer>(parent1.getRoute())); 
				t.setCuts(new ArrayList<Integer>(parent1.getCuts())); 
				children.add(t); 
				continue;
			}
			
			// Recombination MultiTourChromosome
			// O-crossover
			int dx1 = rand.nextInt(16); 
			int dx2 = rand.nextInt(16); 
			
			int swap = Math.min(dx1, dx2); 
			dx2 = Math.max(dx1, dx2); 
			dx1 = swap; 
		
			List<Integer> midPart = new ArrayList<>(parent1.getRoute().subList(dx1, dx2)); 
			Tour child = new Tour(); 
			List<Integer> route = new ArrayList<Integer>(); 
			List<Integer> routeNew = new ArrayList<Integer>(); 
			List<Integer> donts = new ArrayList<Integer>(); 
			for (Integer key : midPart) 
			{
				donts.add(key); 
			}
			int midCounter = 0; 
			int xf[] = new int [16]; 
			
			for (int j = dx2; j - dx2 < 16; j++) 
			{
				if (j%16 >= dx1 && j%16 < dx2 )    
				{
					xf[j%16] = midPart.get(midCounter); 
					midCounter++; 
				} else {
					for (int u = dx2; u - dx2 < 16; u++) 
					{
						int entry = parent2.getRoute().get(u%16); 
						if (donts.contains(entry)) 
						{
							continue; 
						}
						xf[j%16] = entry; 
						donts.add(entry); 
						break; 
					}
				}
			}
			for (int b = 0; b < 16; b++)
			{
				route.add(xf[b]);
			}
			routeNew = route; 
			child.setRoute(routeNew); 
			
			// Recombination CutChromosome
			// 1-Point-Crossover
			int x = rand.nextInt(parent1.getCuts().size() +1); 
			int y1 = 0,y2 = 0,y3 = 0; 
			if (x == 0) 
			{
				y1 = parent2.getCuts().get(x);
				y2 = parent2.getCuts().get(1);
				y3 = parent2.getCuts().get(2);
			} else if (x == 1) 
			{
				y1 = parent1.getCuts().get(0);
				y2 = parent2.getCuts().get(x);
				y3 = parent2.getCuts().get(2);
			} else if (x == 2) 
			{
				y1 = parent1.getCuts().get(0);
				y2 = parent1.getCuts().get(1);
				y3 = parent2.getCuts().get(x);
			}else if (x == 3) 
			{
				y1 = parent1.getCuts().get(0);
				y2 = parent1.getCuts().get(1);
				y3 = parent1.getCuts().get(2);
			}
			
			
			List<Integer> cuts = new ArrayList<Integer>(); 
			cuts.add(y1); 
			cuts.add(y2); 
			cuts.add(y3); 
			// CutChromosome Feasibility Test
			if (y1 + y2 + y3 > 8) 
			{
				child.setCuts(cuts); 
			} else {
				child.setCuts(new ArrayList<Integer>(parent1.getCuts())); 
			}
			
			children.add(child); 
		}
		generationCounter++;  
		return children;
	}

	// Mutate IndividualChildren
	@Override
	public List<Tour> mutate(List<Tour> children) {
		Random rand = new Random(); 
		// Select all IndividualChildren and Mutate with probability ratio between 0.0625 and 1   
		for (Tour child : children) 
		{
			double mutateChance = rand.nextDouble(); 
			if (mutateChance < Math.max(0.0625,explorationRatio)) 
			{
				// Start Mutation of MultiTourChromosome
				// Inverse Mutation
				int pos1 = rand.nextInt(15) +1; 
				int pos2 = rand.nextInt(15) +1; 
				int swap = Math.min(pos1, pos2); 
				pos2 = Math.max(pos1, pos2); 
				pos1 = swap; 
				List<Integer> subList = new ArrayList<Integer>(child.getRoute()); 
				subList = subList.subList(pos1 -1, pos2); 
				Collections.reverse(subList); 
				List<Integer> newRoute = new ArrayList<Integer>(); 
				int copyCounter = 0; 
				for (int i = 0; i < 16; i++) 
				{
					if (i >= pos1 -1 && i <= pos2-1) 
					{
						newRoute.add(subList.get(copyCounter)); 
						copyCounter++; 
					} else {
						newRoute.add(child.getRoute().get(i)); 
					}
				}
				child.setRoute(newRoute); 
				
				// Mutation CutChromosome
				List<Integer> newCuts = new ArrayList<Integer>(); 
				// Mutation CutChromosome using Gaussian Function (0-2)
				for (int i = 0; i < 3; i++) 
				{
					int r = (int) Math.round(rand.nextGaussian()*mutationsReichweite); 
					newCuts.add(Math.min(8,Math.max(0,child.getCuts().get(i) + r))); 
				}
				
				// Feasibility Test CutChromosome
				if (newCuts.get(0) + newCuts.get(1) + newCuts.get(2) > 8) 
				{
					child.setCuts(newCuts); 
				} 
			}
		}
		return children; 
	}

	// Start Local Opimization
	@Override
	public List<Tour> localOptimization(List<Tour> children) {
		int indCounter = 1; 
		List<Tour> optimizedChildren = new ArrayList<Tour>();
		for (Tour child : children) 
		{
			boolean optimized = true; 
			while (optimized) 
			{
				optimized = false; 
				
				// comparison ls-optimized Children with start children   
//				System.out.println("-----------------------------" + "\n");	
//				System.out.println("start 2opt:" + "\n"); 
				Tour optimizedChild = localOpt2Opt(child); 
				updateEntry(optimizedChild); 
				
				// Ausgabe normales Kind und optimiert nach Berechnung
//				System.out.println("-----------------------------" + "\n");	
//				System.out.println(indCounter + ". Kind normal nach:" + "\n"); 
//				printTour(child);
//				System.out.println("-----------------------------" + "\n");	
//				System.out.println(indCounter + ". Kind optimiert nach:" + "\n"); 
//				printTour(optimizedChild);

				if (optimizedChild.getFitness() < child.getFitness()) 
				{
					child = optimizedChild; 
//					System.out.println("-----------------------------" + "\n");	
//					System.out.println(indCounter + ". Kind optimiert nach 2opt:" + "\n"); 
//					printTour(child);
					optimized = true; 
					continue; 
				}

//				System.out.println("-----------------------------" + "\n");	
//				System.out.println("start 2opt*:" + "\n"); 
				
				//Ausgabe normales Kind und optimiert vor Berechnung
//				System.out.println("-----------------------------" + "\n");	
//				System.out.println(indCounter + ". Kind normal vor:" + "\n"); 
//				printTour(child);
//				System.out.println("-----------------------------" + "\n");	
//				System.out.println(indCounter + ". Kind optimiert vor:" + "\n"); 
//				printTour(optimizedChild);
				
				optimizedChild = localOpt2OptStar(child); 
				updateEntry(optimizedChild); 
				
				//Ausgabe normales Kind und optimiert nach Berechnung
//				System.out.println("-----------------------------" + "\n");	
//				System.out.println(indCounter + ". Kind normal nach:" + "\n"); 
//				printTour(child);
//				System.out.println("-----------------------------" + "\n");	
//				System.out.println(indCounter + ". Kind optimiert nach:" + "\n"); 
//				printTour(optimizedChild);
								
				if (optimizedChild.getFitness() < child.getFitness()) 
				{
					child = optimizedChild; //Kind zum optimierten Kind
//					System.out.println("-----------------------------" + "\n");	
//					System.out.println(indCounter + ". Kind optimiert nach 2opt*:" + "\n"); 
//					printTour(child);
					optimized = true; 
					continue; 
				}

//				System.out.println("-----------------------------" + "\n");	
//				System.out.println("start Relocate:" + "\n"); 
				
				//Ausgabe normales Kind und optimiert vor Berechnung
//				System.out.println("-----------------------------" + "\n");	
//				System.out.println(indCounter + ". Kind normal vor:" + "\n");
//				printTour(child);
//				System.out.println("-----------------------------" + "\n");	
//				System.out.println(indCounter + ". Kind optimiert vor:" + "\n"); 
//				printTour(optimizedChild);
				
				optimizedChild = localOptRelocate(child); 
				updateEntry(optimizedChild); 
				
				//Ausgabe normales Kind und optimiert nach Berechnung
//				System.out.println("-----------------------------" + "\n");	
//				System.out.println(indCounter + ". Kind normal nach:" + "\n");
//				printTour(child);
//				System.out.println("-----------------------------" + "\n");	
//				System.out.println(indCounter + ". Kind optimiert nach:" + "\n");
//				printTour(optimizedChild);
				
				if (optimizedChild.getFitness() < child.getFitness()) 
				{
					child = optimizedChild; //Kind zum optimierten Kind
//					System.out.println("-----------------------------" + "\n");	
//					System.out.println(indCounter + ". Kind optimiert nach Relocate:" + "\n"); 
//					printTour(child);
					optimized = true; 
					continue; 
				}
//				System.out.println("-----------------------------" + "\n");	
//				System.out.println("start Swap:" + "\n"); 
				
				//Ausgabe normales Kind und optimiert vor Berechnung
//				System.out.println("-----------------------------" + "\n");	
//				System.out.println(indCounter + ". Kind normal vor:" + "\n");
//				printTour(child);
//				System.out.println("-----------------------------" + "\n");	
//				System.out.println(indCounter + ". Kind optimiert vor:" + "\n"); 
//				printTour(optimizedChild);
				
				optimizedChild = localOptSwap(child); 
				updateEntry(optimizedChild); 
				
				//Ausgabe normales Kind und optimiert nach Berechnung
//				System.out.println("-----------------------------" + "\n");	
//				System.out.println(indCounter + ". Kind normal nach:" + "\n");
//				printTour(child);
//				System.out.println("-----------------------------" + "\n");	
//				System.out.println(indCounter + ". Kind optimiert nach:" + "\n");
//				printTour(optimizedChild);
				
				if (optimizedChild.getFitness() < child.getFitness()) 
				{
					child = optimizedChild; 
//					System.out.println("-----------------------------" + "\n");	
//					System.out.println(indCounter + ". Kind optimiert nach Swap:" + "\n"); 
//					printTour(child);
					optimized = true; 
					continue; 
				}
				indCounter = indCounter + 1; 
			}
			optimizedChildren.add(child);
		}
//		for (Tour key : optimizedChildren) 
//		{
//			System.out.println("-----------test" + "\n");	
//			printTour(key); 
//		}
		return optimizedChildren; 
	}
	
	//start 2-opt procedure with splitting in subroutes
	private Tour localOpt2Opt(Tour t)
	{
		Random rand = new Random(); 
		int idx = rand.nextInt(t.getSubRoutes().size()); 
		List<Integer> subRoute = new ArrayList<Integer>(t.getSubRoutes().get(idx)); 
		
		int pos1 = rand.nextInt(subRoute.size()) +1; 
		int pos2 = rand.nextInt(subRoute.size()) +1; 
		int swap = Math.min(pos1, pos2); 
		pos2 = Math.max(pos1, pos2); 
		pos1 = swap; 
		List<Integer> subList = new ArrayList<Integer>(subRoute.subList(pos1-1, pos2)); 
		Collections.reverse(subList); 
		List<Integer> newRoute = new ArrayList<Integer>();  
		int copyCounter = 0; 
		for (int i = 0; i < subRoute.size(); i++) 
		{
			if (i >= pos1 -1 && i <= pos2-1)  
			{
				newRoute.add(subList.get(copyCounter)); 
				copyCounter++; 
			} else {
				newRoute.add(subRoute.get(i)); 
			}
		}
		// construct new multitour and cut-chromosom
		List<List<Integer>> subRoutes = new ArrayList<List<Integer>>(); 
		for (int i = 0; i < t.getSubRoutes().size(); i++) 
		{
			if (i == idx) 
			{
				subRoutes.add(newRoute); 
			} else {
				subRoutes.add(t.getSubRoutes().get(i)); 
			}
		}
		Tour tmp = buildGlobalRoute(subRoutes); 
		return tmp; 
	}

	// generates global route out of multiple subRoutes
	private Tour buildGlobalRoute(List<List<Integer>> subRoutes)
	{
		List<Integer> globalRoute = new ArrayList<Integer>(); 
		List<Integer> cutList = new ArrayList<Integer>();
		for (List<Integer> sub : subRoutes) 
		{
			int cuts = 0; 
			for (Integer key : sub) 
			{
				globalRoute.add(key); 
				cuts++; 
			}
			cutList.add(cuts); 
		}
		while(cutList.size() < 3) 
		{
			cutList.add(0);  
		}
		Tour t = new Tour(); 
		t.setRoute(globalRoute); 
		t.setCuts(cutList); 
		return t; 
	}
	
	//start 2-opt* procedure with splitting in subroutes
	private Tour localOpt2OptStar(Tour t)
	{
		if (t.getSubRoutes().size() < 2) 
		{
			return t; 
		}
		
		Random rand = new Random(); 
		int idx = rand.nextInt(t.getSubRoutes().size()); 
		int idx2 = rand.nextInt(t.getSubRoutes().size()); 
		while (idx == idx2) 
		{
			idx2 = rand.nextInt(t.getSubRoutes().size()); 
		}
		List<Integer> subRoute1 = new ArrayList<>(t.getSubRoutes().get(idx)); 
		List<Integer> subRoute2 = new ArrayList<>(t.getSubRoutes().get(idx2)); 
		
		int rand1 = rand.nextInt(subRoute1.size()); 
		int rand2 = rand.nextInt(subRoute2.size()); 
		
		List<Integer> s1l = new ArrayList<>(subRoute1.subList(0, rand1)); 
		List<Integer> s1r = new ArrayList<>(subRoute1.subList(rand1,subRoute1.size())); 
		List<Integer> s2l = new ArrayList<>(subRoute2.subList(0, rand2)); 
		List<Integer> s2r = new ArrayList<>(subRoute2.subList(rand2,subRoute2.size())); 
		
		s1l.addAll(new ArrayList<>(s2r)); 
		s2l.addAll(new ArrayList<>(s1r)); 
		
		List<List<Integer>> subRoutes = new ArrayList<List<Integer>>(); 
		for (int i = 0; i < t.getSubRoutes().size(); i++) 
		{
			if (i == idx) 
			{
				subRoutes.add(s1l);  
			} else if (i == idx2) 
			{
				subRoutes.add(s2l);  
			} else {
				subRoutes.add(new ArrayList<>(t.getSubRoutes().get(i))); 
			}
		}

		// construct new multitour and cut chromosom
		for (List<Integer> sub : subRoutes)  
		{
			if (sub.size() > 8) 
			{
				return t; 
			}
		}
		return buildGlobalRoute(subRoutes); 
	}
	
	//start Relocate procedure with splitting in subroutes
	private Tour localOptRelocate(Tour t)
	{
		if (t.getSubRoutes().size() < 2) 
		{
			return t; 
		}
		Random rand = new Random(); 
		int idx = rand.nextInt(t.getSubRoutes().size()); 
		List<Integer> subRoute = new ArrayList<>(t.getSubRoutes().get(idx)); 
		
		if (subRoute.size() < 2) 
		{
			return t;
		}
		
		int pos1 = rand.nextInt(subRoute.size()); 
		int pos2 = rand.nextInt(subRoute.size()); 
		while (pos1 == pos2) 
		{
			pos2 = rand.nextInt(subRoute.size()); 
		}
		int swap = Math.min(pos1, pos2);
		pos2 = Math.max(pos1, pos2); 
		pos1 = swap;  

		List<Integer> subLeft = new ArrayList<>(subRoute.subList(0, pos1)); 
		List<Integer> subMid = new ArrayList<>(subRoute.subList(pos1,pos2)); 
		List<Integer> subRight = new ArrayList<>(subRoute.subList(pos2,subRoute.size())); 
		
		subLeft.addAll(subRight);	
		int insertPos = rand.nextInt(subLeft.size());
		
		List<Integer> leftIns = new ArrayList<>(subLeft.subList(0, insertPos)); 
		List<Integer> rightIns = new ArrayList<>(subLeft.subList(insertPos,subLeft.size()));
		
		List<Integer> newSubRoute = new ArrayList<Integer>(); 
		newSubRoute.addAll(leftIns); 	
		newSubRoute.addAll(subMid); 	
		newSubRoute.addAll(rightIns); 
		
		// construct new multitour and cut chromosom
		List<List<Integer>> subRoutes = new ArrayList<List<Integer>>(); 
		for (int i = 0; i < t.getSubRoutes().size(); i++) 
		{
			if (i == idx) 
			{
				subRoutes.add(newSubRoute); 
			} else {
				subRoutes.add(new ArrayList<>(t.getSubRoutes().get(i))); 
			}
		}
		return buildGlobalRoute(subRoutes);
	}
	
	//start Swap procedure with splitting in subroutes
	private Tour localOptSwap(Tour t)
	{
		if (t.getSubRoutes().size() < 2) 
		{
			return t; 
		}
		
		Random rand = new Random(); 
		int idx = rand.nextInt(t.getSubRoutes().size()); 
		int idx2 = rand.nextInt(t.getSubRoutes().size()); 
		while (idx == idx2) 
		{
			idx2 = rand.nextInt(t.getSubRoutes().size()); 
		}
		List<Integer> subRoute1 = new ArrayList<>(t.getSubRoutes().get(idx)); 
		List<Integer> subRoute2 = new ArrayList<>(t.getSubRoutes().get(idx2)); 
		
		int rand1 = rand.nextInt(subRoute1.size()); 
		int rand2 = rand.nextInt(subRoute1.size()); 
	
		int swap = Math.min(rand1, rand2); 
		rand2 = Math.max(rand1, rand2); 
		rand1 = swap; 
		
		List<Integer> subLeft = new ArrayList<>(subRoute1.subList(0, rand1)); 
		List<Integer> subMid = new ArrayList<>(subRoute1.subList(rand1,rand2)); 
		List<Integer> subRight = new ArrayList<>(subRoute1.subList(rand2,subRoute1.size())); 
		
		subLeft.addAll(subRight); 	
		int insertPos = rand.nextInt(subRoute2.size()); 
		
		List<Integer> rl = new ArrayList<>(subRoute2.subList(0, insertPos)); 
		List<Integer> rr = new ArrayList<>(subRoute2.subList(insertPos,subRoute2.size()));
		
		List<Integer> inserted = new ArrayList<>(); 
		inserted.addAll(rl); 
		inserted.addAll(subMid); 
		inserted.addAll(rr); 

		if (inserted.size() > 8) 
		{
			return t; 
		}

		// construct new multitour and cut chromosom
		List<List<Integer>> subRoutes = new ArrayList<List<Integer>>(); 
		for (int i = 0; i < t.getSubRoutes().size(); i++) 
		{
			if (i == idx) 
			{
				subRoutes.add(subLeft); 
			} else if (i == idx2) 
			{
				subRoutes.add(inserted); 
			} else {
				subRoutes.add(t.getSubRoutes().get(i)); 
			}
		}
		return buildGlobalRoute(subRoutes); 
	}

	//select new Population of not parents, children and parents
	@Override
	public List<Tour> selectNewPopulation(List<Tour> parents, List<Tour> notParents, List<Tour> children)
	{
		//choose all not parents for new population
		List<Tour> newPop = new ArrayList<Tour>(); 
		newPop.addAll(notParents); 
		
		//duplicate check children
		for (Tour key : children) 
		{
			if (!newPop.contains(key)) 
			{
				newPop.add(key); 
			}
		}
	
		//random parent selection for new population
		Random randomizer = new Random(); 
		int randNum = 20 - newPop.size(); 
		List<Tour> parentsCopy = new ArrayList<Tour>(parents); 
		for (int i = 0; i < randNum; i++) 
		{
			int r = randomizer.nextInt(parentsCopy.size()); 
			Tour potTour = parentsCopy.get(r); 
			//duplicate check parents 
			if (!newPop.contains(potTour)) 
			{
				newPop.add(potTour); 
			} else {
				i--; 
			}
		}

//		System.out.println("-----------------------------" + "\n");	
//		System.out.println("neue Population:" + "\n"); 
//		
//		for (Tour key : newPop) 
//		{
//			printTour(key); 
//		}
		
		//cos Exploration Ratio
		explorationRatio = Math.cos(2.0*Math.PI*generationCounter/PERIOD)/2.0 + 0.5; 
		return newPop; 
	}

	//termination condition
	@Override
	public boolean isFinished(List<Tour> population) {
		return generationCounter > GENERATION_LIMIT; 
	}

	// Fitnessfunktion + sub routen erstellen
	@Override
	public Tour updateEntry(Tour entry) {
		double value = 0; 
		List<Integer> globalRoute = new ArrayList<Integer>(entry.getRoute()); 
		List<List<Integer>> subRoutes = new ArrayList<List<Integer>>(); 
		
		// cut global route into multiple subRoutes
		for (int cut : entry.getCuts()) 
		{
			if (globalRoute.size() > cut) 
			{
				List<Integer> tmp = new ArrayList<Integer>(globalRoute.subList(0, cut)); 
				if (!tmp.isEmpty()) 
				{
					subRoutes.add(tmp); 
					globalRoute.removeAll(subRoutes.get(subRoutes.size()-1)); 
				}
			} else {
				List<Integer> tmp = new ArrayList<Integer>(globalRoute); 
				if (!tmp.isEmpty()) 
				{
					subRoutes.add(tmp); 
				}
				globalRoute.clear(); 
			}
		}
		if (globalRoute.size() > 0) 
		{
			List<Integer> tmp = new ArrayList<Integer>(globalRoute);  
			if (!tmp.isEmpty()) 
			{
				subRoutes.add(tmp); 
			}
			globalRoute.clear(); 
		}
		entry.setSubRoutes(subRoutes); 
		
		double[] subRouteSums = new double[entry.getSubRoutes().size()]; 
		double[] subRoutePenalty = new double[entry.getSubRoutes().size()]; 
		double[] values = new double[entry.getSubRoutes().size()]; 
		
		// calculate commission time
		for (int i = 0; i < subRouteSums.length; i++) 
		{
			subRouteSums[i] = 0; 
			subRoutePenalty[i] = 0; 
			values[i] = 0; 
			for (Integer key : entry.getSubRoutes().get(i)) 
			{
				subRouteSums[i] += comTimes[key-1]; 
			}
		}
		
		int length = subRouteSums.length; 
		if (length == 3) 
		{
			if (subRouteSums[0] <= subRouteSums[1]) 
			{
				subRouteSums[2] += subRouteSums[0]; 
			} else {
				subRouteSums[2] += subRouteSums[1]; 
			}
		} else if (length == 4) 
		{
			if (subRouteSums[0] <= subRouteSums[1]) 
			{
				subRouteSums[2] += subRouteSums[0]; 
				
				if (subRouteSums[2] < subRouteSums[1]) 
				{
					subRouteSums[3] += subRouteSums[2]; 
				} else {
					subRouteSums[3] += subRouteSums[1]; 
				}
			} else 	
			{
				subRouteSums[2] += subRouteSums[1]; 
				
				if (subRouteSums[2] < subRouteSums[0]) 
				{
					subRouteSums[3] += subRouteSums[2]; 
				} else 
				{
					subRouteSums[3] += subRouteSums[0]; 
				}
			}
		}

		// determine penalty value for each subRoute
		entry.setPenaltySum(0); 
		double penaltySum = 0; 
		entry.setSubRouteComTimes( new ArrayList<Double>()); 
		for (int i = 0; i < subRouteSums.length; i++) 
		{
			entry.getSubRouteComTimes().add(subRouteSums[i]); 
			Integer previousKey = 0; 
			for (Integer key : entry.getSubRoutes().get(i)) 
			{
				double pathTime = pathMatrice[previousKey][key]; 
				subRouteSums[i] += pathTime; 
				if (subRouteSums[i] > maxTimes[key -1]) 
				{
					subRoutePenalty[i] += subRouteSums[i] - maxTimes[key-1]; 
				}
				previousKey = key; 
			}
			penaltySum += subRoutePenalty[i]; 
			entry.setPenaltySum(entry.getPenaltySum() + subRoutePenalty[i]); 
		}
		
		// only consider latest arriving subRoute
		double maxSum = Double.MIN_VALUE; 
		for (int i = 0; i < length; i++) 
		{
			if (subRouteSums[i] > maxSum) 
			{
				maxSum = subRouteSums[i]; 
			}
		}
		value = maxSum + penaltySum; 
		
		entry.setFitness(value); 
		return entry; 
	}
	
//	private void printTour(Tour t) 
//	{
//		System.out.println(Arrays.toString(t.getRoute().toArray()) + " " + Arrays.toString(t.getCuts().toArray()) + " " + t.getFitness() + " Pen:  " + t.getPenaltySum() + "\n"); 
//		int j = 0;
//		Iterator<List<Integer>> iterator = t.getSubRoutes().iterator(); 
//		while(iterator.hasNext()) 
//		{
//			List<Integer> sub = iterator.next(); //Nimm das nächste Element
//			System.out.println("    " + Arrays.toString(sub.toArray())+ "com: " + t.getSubRouteComTimes().get(j) + "\n");
//			j++;
//		}
//		return;
//	}
}
