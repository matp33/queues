

package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import core.MainLoop;
import events.UIEventQueue;
import otherFunctions.FileAnalyzer;
import otherFunctions.TimeTable;
import symulation.Manager;
import symulation.Painter;

public class ListenerOpenFile implements ActionListener{
    
private JFileChooser fileChoosingWindow = new JFileChooser();
private static final String TXT_FILES_DIR = "./src/txtFiles";

protected Painter painter;

private UIEventQueue UIEventQueue;

    public ListenerOpenFile ( Painter painter, UIEventQueue UIEventQueue){
    	JFileChooser fileChooser=new JFileChooser();
    	File txtFilesDirectory = new File(TXT_FILES_DIR);
        fileChooser.setCurrentDirectory(txtFilesDirectory);
        
        fileChoosingWindow=fileChooser;         
        this.painter = painter;
        this.UIEventQueue = UIEventQueue;
    }
    
  //TODO nice option would be to show somewhere infos about the file we opened: average time in queue, 
  //clients per minute etc (things already known but not included in the file)    
    
    @Override
    public void actionPerformed(ActionEvent e){

        try {
            painter.pause();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        int optionChooser= fileChoosingWindow.showOpenDialog(null);

           if (optionChooser==JFileChooser.APPROVE_OPTION){
               try {
                   analyze(e);
               } catch (Exception ex) {
                   throw new RuntimeException(ex);
               }
           }         
           
           else{
               try {
                   if (MainLoop.getInstance().isPaused()){
                       return;
                   }
               } catch (Exception ex) {
                   throw new RuntimeException(ex);
               }

           }
        painter.resume(false);
    }
    
    private void analyze(ActionEvent e) throws Exception {
    	
    	File selectedFile = fileChoosingWindow.getSelectedFile();                 
        TimeTable timeTable= new TimeTable();
        
            try {
         	   timeTable = FileAnalyzer.analyze(selectedFile);
            }
            catch (IOException i){
                i.printStackTrace();
                painter.displayMessage("File opening failed");
            }
            catch (NumberFormatException ex){
               ex.printStackTrace();
                painter.displayMessage("Invalid file format!");
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
             
         maximum++; // counters are counted from 0
         timeTable=processTimeTable(timeTable);
         arrivals=timeTable.arrivals; // TODO reconsider it; we taking 2 times arrivals and departures from timeTable
         departures=timeTable.departures;
         
             if(Painter.getNumberOfQueues() != maximum){
                 MainLoop.getInstance().pause();
                 painter = Painter.initialize(maximum);
                 painter.initiate();
             }
                          

         try{                       
             UIEventQueue.publishNewTimetableEvent(new TimeTable(arrivals, departures));
         }
         catch (Exception ex){
             ex.printStackTrace();
         }
    }
    
    // method to be overriden if we wanna do something on the time table
    protected TimeTable processTimeTable(TimeTable tt) {
    	return tt;
    }

}
