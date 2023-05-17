

package listeners;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import otherFunctions.TimeTable;
import otherFunctions.ArrayOperations;
import symulation.Manager;

public class ListenerExtraction extends ListenerOpenFile{
	private String title = "Choosing queue number.";

    public ListenerExtraction (Manager m){
        super(m);
    }
    
    @Override 
    protected TimeTable processTimeTable(TimeTable tt){
    	
    	int number=findMaxNumber(tt);
    	List<Integer> numbers=makeDialog(number);
    	tt=extractQueues(tt,numbers);
    	return tt;
    	
    }
    
    private List<Integer> makeDialog(int number){
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
    	
    	manager.displayWindowWithPanel(p, title);
    	
    	List <Integer> numbers = new ArrayList <Integer> ();
    	for (int i=0; i<number; i++){
    		JCheckBox c = boxes[i];
    		if (c.isSelected()){
    			numbers.add(i);
    		}
    	}
    	return numbers;
    }
    
    private TimeTable extractQueues(TimeTable table, List<Integer> queuesNumber){
    	double [][] arrivals = new double [table.arrivals.length][2];
    	double [][] departs = new double [table.departures.length][2];
    	int j=0;
    	for (int i=0; i<table.arrivals.length; i++){
    		if (queuesNumber.contains((int)table.arrivals[i][1])){
    			arrivals[j][0]=table.arrivals[i][0];
    			
    			arrivals[j][1]=table.arrivals[i][1];
    			System.out.println("#arr time: "+arrivals[j][0] +" queue "+arrivals[j][1]);
    			
    			j++;
    		}
    	}
    	
    	j=0;
    	for (int i=0; i<table.departures.length; i++){
    		if (queuesNumber.contains((int)table.departures[i][1])){
    			departs[j][0]=table.departures[i][0];
    			departs[j][1]=table.departures[i][1];
    			System.out.println("#dep time"+departs[j][0] +" queue "+departs[j][1]);
    			j++;
    		}
    	}
    	System.out.println("j2@ "+j);
    	double [][] newArr = ArrayOperations.removeZeros(arrivals,j);
    	double [][] newDep = ArrayOperations.removeZeros(departs,j);
    	
    	
    	return new TimeTable(newArr,newDep);
    }
    
    private int findMaxNumber(TimeTable timeTable){
    	double [][] arrivals = timeTable.arrivals; // same result we would get if we would take departs instead
    	double max=0;
    	for (int i=0; i<arrivals.length; i++){
    		if (arrivals[i][1]>max){
    			max=arrivals[i][1];
    		}
    	}
    	return (int)max+1;
    }

}
