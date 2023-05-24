

package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JFileChooser;

import constants.TypeOfTimeEvent;
import core.MainLoop;
import events.UIEventQueue;
import otherFunctions.FileAnalyzer;
import symulation.ApplicationConfiguration;
import symulation.Painter;
import symulation.ClientArrivalEvent;

public class ListenerOpenFile implements ActionListener{
    
private JFileChooser fileChoosingWindow = new JFileChooser();
private static final String TXT_FILES_DIR = "./src/txtFiles";

protected Painter painter;

private UIEventQueue UIEventQueue;

private ApplicationConfiguration applicationConfiguration;

    public ListenerOpenFile ( Painter painter, UIEventQueue UIEventQueue)  {
    	JFileChooser fileChooser=new JFileChooser();
    	File txtFilesDirectory = new File(TXT_FILES_DIR);
        fileChooser.setCurrentDirectory(txtFilesDirectory);
        
        fileChoosingWindow=fileChooser;         
        this.painter = painter;
        this.UIEventQueue = UIEventQueue;
        applicationConfiguration = ApplicationConfiguration.getInstance();
    }
    
  //TODO nice option would be to show somewhere infos about the file we opened: average time in queue, 
  //clients per minute etc (things already known but not included in the file)    
    
    @Override
    public void actionPerformed(ActionEvent e){

        painter.pause();
        int optionChooser= fileChoosingWindow.showOpenDialog(null);

           if (optionChooser==JFileChooser.APPROVE_OPTION){
               analyze(e);
           }
           
           else if (MainLoop.getInstance().isPaused()){
               return;
           }


        painter.resume(false);
    }
    
    private void analyze(ActionEvent e) {
    	
    	File selectedFile = fileChoosingWindow.getSelectedFile();                 
        SortedSet<ClientArrivalEvent> timeTable= new TreeSet<>();
        
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
         


        Integer lastQueueIndex = timeTable.stream().max(Comparator.comparing(ClientArrivalEvent::getQueueNumber)).map(ClientArrivalEvent::getQueueNumber).orElseThrow(() -> new IllegalArgumentException("empty time table"));

         timeTable=processTimeTable(timeTable);

             if(applicationConfiguration.getNumberOfQueues() != lastQueueIndex){
                 MainLoop.getInstance().pause();
                 applicationConfiguration.setNumberOfQueues(lastQueueIndex+1);
                 painter.initiate();
             }
                          

         UIEventQueue.publishNewTimetableEvent(timeTable);
    }
    
    // method to be overriden if we wanna do something on the time table
    protected SortedSet<ClientArrivalEvent> processTimeTable(SortedSet<ClientArrivalEvent> tt) {
    	return tt;
    }

}
