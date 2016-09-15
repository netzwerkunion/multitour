package tour.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import tour.algorithm.algorithmHandler;
import tour.algorithm.memeticAlgorithm;
import tour.algorithm.tour;

public class MainWindow extends JFrame {
	
	
	private static final long serialVersionUID = 4575209619441571141L; 
	private JTextArea output = new JTextArea(); 
	private JScrollPane jp = new JScrollPane(output); 
	
	memeticAlgorithm algorithm = new memeticAlgorithm();
	algorithmHandler handler = new algorithmHandler(algorithm); 

	public MainWindow() {
		this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		setTitle("TourPlaner - Master Andre"); 
		setLayout(new BorderLayout()); 
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
		JButton startButton = new JButton("Run - complete"); 
		JButton init = new JButton("Init - population"); 
		JButton step = new JButton("do Step"); 
		startButton.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent e) { 
				algorithm = new MemeticAlgorithm(); 
				handler = new AlgorithmHandler(algorithm); 
				Tour key = handler.findBestSolution(); 
				output.append("--------------------------------------\n"); 
				output.append("Best Solution:\n"); 
				printTour(key); 
			}
		});
		
		step.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { 
				handler.doStep(); 
				output.append("--------------------------------------\n");
				output.append("Population next Iteration:\n"); 
				List<Tour> pop = handler.getPopulation(); 
				for (int i = 0; i < pop.size(); i++) 					
				{
					for (int j = 0; j < pop.size(); j++)	
					{
						if (i != j)			
						{
							if (pop.get(i).equals(pop.get(j))) 
							{
								output.append("Duplikat gefunden!"); 
							}
						}
						
					}
				}
				
			for (Tour tour : handler.getPopulation())
			{
				printTour(tour);
				}
			}
		});
		
		init.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				algorithm = new MemeticAlgorithm();  
				handler = new AlgorithmHandler(algorithm); 
				output.append("--------------------------------------\n"); 
				output.append("Initial Population:\n"); 
				
				List<Tour> pop = handler.getPopulation();
				for (int i = 0; i < pop.size(); i++)
				{
					Tour key = pop.get(i); 
					printTour(key); 
				}
			}
		});
		
		JPanel buttons = new JPanel(); 
		buttons.setLayout(new FlowLayout()); 
		buttons.add(startButton); 
		startButton.setPreferredSize(new Dimension(200,30)); 
		step.setPreferredSize(new Dimension(200,30)); 
		init.setPreferredSize(new Dimension(200,30)); 
		buttons.add(Box.createVerticalStrut(30)); 
		buttons.add(init); 
		buttons.add(step); 
		JButton exit = new JButton("Exit");
		exit.addActionListener(new ActionListener() { 
			
			@Override
			public void actionPerformed(ActionEvent e) { 
				System.exit(0); 
			}
		});
		buttons.add(exit); 
		exit.setPreferredSize(new Dimension(200,30)); 
		add(buttons,BorderLayout.NORTH); 
		add(jp, BorderLayout.CENTER); 
	}

	private void printTour(Tour key) 
	{
		output.append(Arrays.toString(key.getRoute().toArray()) + " " + Arrays.toString(key.getCuts().toArray()) + " " + key.getFitness() + " Pen:  " + key.getPenaltySum() + "\n"); 
		int j = 0;
		Iterator<List<Integer>> iterator = key.getSubRoutes().iterator(); 
		while(iterator.hasNext()) 
		{
			List<Integer> sub = iterator.next(); 
			output.append("    " + Arrays.toString(sub.toArray())+ "com: " + key.getSubRouteComTimes().get(j) + "\n"); 
			j++;
		}
		JScrollBar vertical = jp.getVerticalScrollBar();
		vertical.setValue(vertical.getMaximum() ); 
	}



}
