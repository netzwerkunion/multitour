package tour.algorithm;

import java.util.ArrayList;
import java.util.List;

public class tour implements Comparable<Tour>{ 

	private List<Integer> route = new ArrayList<Integer>();
	private List<Integer> cuts = new ArrayList<Integer>();
	private List<List<Integer>> subRoutes = null;  
	private List<Double> subRouteComTimes = new ArrayList<Double>(); 
	
	private double penaltySum = 0; 
	
	private double fitness = 0; 
	
	public List<Integer> getCuts() {
		return cuts; 
	}
	
	public void setCuts(List<Integer> cuts) {
		this.cuts = cuts; 
	}
	
	public List<Integer> getRoute() {
		return route; 
	}
	
	public void setRoute(List<Integer> route) {
		this.route = route; 
	}
	
	public double getFitness() {
		return fitness; 
	}
	
	public void setFitness(double fitness) {
		this.fitness = fitness; 
	}
	
	public List<List<Integer>> getSubRoutes() {
		return subRoutes; 
	}
	
	public void setSubRoutes(List<List<Integer>> subRoutes) {
		this.subRoutes = subRoutes; 
	}
	
	public double getPenaltySum() {
		return penaltySum; 
	}
	
	public void setPenaltySum(double penaltySum) {
		this.penaltySum = penaltySum; 
	}
		
	public List<Double> getSubRouteComTimes() {
		return subRouteComTimes; 
	}
	
	public void setSubRouteComTimes(List<Double> subRouteComTimes) {
		this.subRouteComTimes = subRouteComTimes; 
	}
	
	@Override
	public int compareTo(Tour o) { 
		return (int) (this.getFitness() - o.getFitness()); 
	}
	
	@Override
	public boolean equals(Object obj) { 
		Tour other = (Tour) obj; 
		if(this.getRoute().size() != other.getRoute().size()) 
		{
			return false; 
		}
		if (this.getSubRoutes().size() != other.getSubRoutes().size())
		{
			return false; 
		}
		for (int i = 0; i < this.getRoute().size(); i++) 
		{
			if (this.getRoute().get(i) != other.getRoute().get(i)) 
			{
				return false; 
			}
		}
		for (int i = 0; i < this.getSubRoutes().size(); i++)
		{
			List<Integer> s1 = this.getSubRoutes().get(i); 
			List<Integer> s2 = other.getSubRoutes().get(i); 
			
			if (s1.size() != s2.size()) 
			{
				return false; 
			}
			for (int j = 0; j < s1.size(); j++) 
			{
				if (s1.get(j) != s2.get(j))
				{
					return false; 
				}
			}
		}
		return true;
	}
}
