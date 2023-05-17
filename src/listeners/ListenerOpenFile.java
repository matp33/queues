

package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import otherFunctions.FileAnalyzer;
import otherFunctions.TimeTable;
import symulation.Manager;

public class ListenerOpenFile implements ActionListener{
    
private JFileChooser fileChoosingWindow = new JFileChooser();
private static final String TXT_FILES_DIR = "./src/txtFiles";
protected Manager manager;

    public ListenerOpenFile ( Manager s){
    	JFileChooser fileChooser=new JFileChooser();
    	File txtFilesDirectory = new File(TXT_FILES_DIR);
        fileChooser.setCurrentDirectory(txtFilesDirectory);
        
        fileChoosingWindow=fileChooser;         
        manager=s;
    }
    
  //TODO nice option would be to show somewhere infos about the file we opened: average time in queue, 
  //clients per minute etc (things already known but not included in the file)    
    
    @Override
    public void actionPerformed(ActionEvent e){
    	
    	manager.pause();
        int optionChooser= fileChoosingWindow.showOpenDialog(null);

           if (optionChooser==JFileChooser.APPROVE_OPTION){
               analyze(e);          
           }         
           
           else{
        	   if (!manager.isRunning()){
        		   return;
        	   }
        	   
           }
           manager.resume(false);
    }
    
    private void analyze(ActionEvent e){
    	
    	File selectedFile = fileChoosingWindow.getSelectedFile();                 
        TimeTable timeTable= new TimeTable();
        
            try {
         	   timeTable = FileAnalyzer.analyze(selectedFile);
            }
            catch (IOException i){
                i.printStackTrace();
                manager.displayMessage("File opening failed");
            }
            catch (NumberFormatException ex){
               ex.printStackTrace();
               manager.displayMessage("Invalid file format!");
               actionPerformed(e);                   
            }
         
         double [][] arrivals=timeTable.arrivals;
         double [][] departures=timeTable.departures;
         int maximum=Integer.MIN_VALUE;
         
             for (int i=0; i<arrivals.length;i++){
                 if ((int)arrivals[i][1]>maximum){
                     maximum=(int)arrivals[i][1];
                 }
             }
             
         maximum++; // queues are counted from 0         
         timeTable=processTimeTable(timeTable);
         arrivals=timeTable.arrivals; // TODO reconsider it; we taking 2 times arrivals and departures from timeTable
         departures=timeTable.departures;
         
             if(manager.isQueueNumberSame(maximum)==false){  
             manager.doChange(maximum);
             }
                          
         manager.saveTimeTable(arrivals, departures);
             
         try{                       
        	 manager.restart(0);
         }
         catch (InterruptedException ex){
             ex.printStackTrace();
         }
    }
    
    // method to be overriden if we wanna do something on the time table
    protected TimeTable processTimeTable(TimeTable tt) {
    	return tt;
    }

}
