

package uieventshandlers;

import java.awt.BorderLayout;
import java.util.*;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import constants.UIEventType;
import events.UIEventQueue;
import spring2.Bean;
import simulation.ApplicationConfiguration;
import dto.ClientArrivalEventDTO;
import simulation.Manager;
import view.SimulationPanel;

@Bean
public class ExtractionButtonClickHandler extends OpenFileButtonClickHandler {
	private String title = "Choosing queue number.";

	private SimulationPanel simulationPanel;



    public ExtractionButtonClickHandler(ApplicationConfiguration applicationConfiguration, SimulationPanel simulationPanel, Manager manager, UIEventQueue uiEventQueue){
        super(  applicationConfiguration, simulationPanel, manager, uiEventQueue);
		this.simulationPanel = simulationPanel;
	}

	@Override
	protected UIEventType getUiEventType() {
		return UIEventType.EXTRACT_BUTTON_CLICK;
	}

	@Override
    protected SortedSet<ClientArrivalEventDTO> processTimeTable(SortedSet<ClientArrivalEventDTO> timetable){
    	
    	int number= findLastQueueIndex(timetable);
    	List<Integer> chosenQueues=makeDialog(number);
    	timetable=extractQueues(timetable,chosenQueues);
    	return timetable;
    	
    }
    
    private List<Integer> makeDialog(int number){
		//TODO add class - ui view that creates panels

		JPanel p=new JPanel();
    	p.setLayout(new BorderLayout());
    	
    	JPanel panel = new JPanel();
    	    	    	
    	BoxLayout b=new BoxLayout(panel,BoxLayout.LINE_AXIS);
    	panel.setLayout(b);
    	JCheckBox[] boxes = new JCheckBox[number];
    	
	    	for (int i=0; i<number; i++){
	    		JCheckBox c=new JCheckBox(""+(i+1));
	    		boxes[i] = c;
	        	panel.add(c);
	    	}
	    	
	    p.add(new JLabel("Please choose the queues you want to extract."), BorderLayout.NORTH);	
    	p.add(panel,BorderLayout.SOUTH);
    	
		simulationPanel.displayWindowWithPanel(p, title);
    	
    	List <Integer> numbers = new ArrayList <Integer> ();
    	for (int i=0; i<number; i++){
    		JCheckBox c = boxes[i];
    		if (c.isSelected()){
    			numbers.add(i);
    		}
    	}
    	return numbers;
    }
    
    private SortedSet<ClientArrivalEventDTO> extractQueues(SortedSet<ClientArrivalEventDTO> table, List<Integer> chosenQueues){


		return table.stream().filter(event->chosenQueues.contains( event.getQueueNumber())).collect(Collectors.toCollection(()->new TreeSet<>(Comparator.comparing(ClientArrivalEventDTO::getArrivalTime))));
    }
    
    private int findLastQueueIndex(SortedSet<ClientArrivalEventDTO> timeTable){
    	return timeTable.stream().max(Comparator.comparing(ClientArrivalEventDTO::getQueueNumber)).map(ClientArrivalEventDTO::getQueueNumber).orElseThrow(() -> new IllegalArgumentException("empty time table"))+1;
    }

}
