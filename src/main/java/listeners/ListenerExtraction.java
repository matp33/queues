

package listeners;

import java.awt.BorderLayout;
import java.util.*;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import core.MainLoop;
import events.UIEventQueue;
import spring2.Bean;
import spring2.BeanRegistry;
import symulation.ApplicationConfiguration;
import symulation.Painter;
import symulation.ClientArrivalEvent;

@Bean
public class ListenerExtraction extends ListenerOpenFile{
	private String title = "Choosing queue number.";


    public ListenerExtraction ( UIEventQueue UIEventQueue, ApplicationConfiguration applicationConfiguration){
        super( UIEventQueue, applicationConfiguration);
	}
    
    @Override 
    protected SortedSet<ClientArrivalEvent> processTimeTable(SortedSet<ClientArrivalEvent> timetable){
    	
    	int number= findLastQueueIndex(timetable);
    	List<Integer> chosenQueues=makeDialog(number);
    	timetable=extractQueues(timetable,chosenQueues);
    	return timetable;
    	
    }
    
    private List<Integer> makeDialog(int number){
		Painter painter = BeanRegistry.getBeanByClass(Painter.class);

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
    	
    	painter.displayWindowWithPanel(p, title);
    	
    	List <Integer> numbers = new ArrayList <Integer> ();
    	for (int i=0; i<number; i++){
    		JCheckBox c = boxes[i];
    		if (c.isSelected()){
    			numbers.add(i);
    		}
    	}
    	return numbers;
    }
    
    private SortedSet<ClientArrivalEvent> extractQueues(SortedSet<ClientArrivalEvent> table, List<Integer> chosenQueues){


		return table.stream().filter(event->chosenQueues.contains( event.getQueueNumber())).collect(Collectors.toCollection(()->new TreeSet<>(Comparator.comparing(ClientArrivalEvent::getArrivalTime))));
    }
    
    private int findLastQueueIndex(SortedSet<ClientArrivalEvent> timeTable){
    	return timeTable.stream().max(Comparator.comparing(ClientArrivalEvent::getQueueNumber)).map(ClientArrivalEvent::getQueueNumber).orElseThrow(() -> new IllegalArgumentException("empty time table"))+1;
    }

}
