package tour.algorithm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
//import java.util.Arrays;
import java.util.Collections;
//import java.util.Iterator;
import java.util.List;

public class AlgorithmHandler {

	private IAlgorithm<Tour> algorithm = null; 
	private List<Tour> population = null; 
	private Tour best = null; 
	
	public AlgorithmHandler(IAlgorithm<Tour> algorithm)
	{
		this.algorithm = algorithm; 
		this.population = algorithm.createStartingPopulation(); 
		for (Tour key : population) 
		{
			algorithm.updateEntry(key); 
		}
	}
	
	public Tour getBest() { 
		return best; 
	}

	public void setBest(Tour best) {
		this.best = best; 
	}

	public Tour findBestSolution()
	{
		
		try(FileWriter fw = new FileWriter("d:/bla2.txt", true);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
			    //out.println("the text");
				while(!algorithm.isFinished(population)) 
				{
					doStep(); 
					double sum = 0;
					double currentBest = population.get(0).getFitness();
					double allTimeBest = best.getFitness();
					for (Tour key : population)
					{
						sum += key.getFitness();
					}
					sum = sum / population.size();
					out.println(sum + "," + currentBest + "," + allTimeBest);
				}
			} catch (IOException e) {
			    //exception handling left as an exercise for the reader
				}
		return best; 
	}
	
	public void doStep()
	{
		List<List<Tour>> parents = algorithm.chooseParents(population); 
		// 0 = parents, 1 = not parents;
		// update population
		
		//Make Children
		List<Tour> children = algorithm.makeChildren(parents.get(0)); 
		
		for (Tour key : parents.get(0)) 
		{
			key = algorithm.updateEntry(key); 
		}
//		System.out.println("-----------------------------" + "\n");	
//		System.out.println("gewählte Eltern:" + "\n"); 
//		for (Tour key : parents.get(0)) 
//		{
//			printTour(key); 
//		}
		
		for (Tour key : children) 
		{
			key = algorithm.updateEntry(key); 
		}
//		System.out.println("-----------------------------" + "\n");	
//		System.out.println("Kinder nach Rekombination:" + "\n"); 
//		for (Tour key : children) 
//		{
//			printTour(key); 
//		}	
		
		//Mutate
		children = algorithm.mutate(children); 	
		
		for (Tour key : children) 
		{
			key = algorithm.updateEntry(key); 
		}
//		System.out.println("-----------------------------" + "\n");	
//		System.out.println("Kinder nach Mutation:" + "\n"); 
//		for (Tour key : children) 
//		{
//			printTour(key); 
//		}
		
		//local Optimization
		children = algorithm.localOptimization(children); 
		
		for (Tour key : parents.get(1)) 
		{
			key = algorithm.updateEntry(key); 
		}
		
		for (Tour key : children) 
		{
			key = algorithm.updateEntry(key); 
		}

		population = algorithm.selectNewPopulation(parents.get(0),parents.get(1),children); 
				
		// always store best solution
		Collections.sort(population); 
		if (best == null)
		{
			best = population.get(0); 
		} else if (population.get(0).getFitness() < best.getFitness()) { 
			best = population.get(0); 
		}
		
	}
	
	public List<Tour> getPopulation() 
	{
		return population;
	}
//	private void printTour(Tour key) 
//	{
//		System.out.println(Arrays.toString(key.getRoute().toArray()) + " " + Arrays.toString(key.getCuts().toArray()) + " " + key.getFitness() + " Pen:  " + key.getPenaltySum() + "\n"); 
//		int j = 0;
//		Iterator<List<Integer>> iterator = key.getSubRoutes().iterator(); 
//		while(iterator.hasNext()) 
//		{
//			List<Integer> sub = iterator.next(); 
//			System.out.println("    " + Arrays.toString(sub.toArray())+ "com: " + key.getSubRouteComTimes().get(j) + "\n"); 
//			j++;
//		}
//	}
  }
