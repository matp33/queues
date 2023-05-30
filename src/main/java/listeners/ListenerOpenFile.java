

package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JFileChooser;

import core.MainLoop;
import events.UIEventQueue;
import otherFunctions.FileAnalyzer;
import spring2.Bean;
import spring2.BeanRegistry;
import symulation.ApplicationConfiguration;
import symulation.Painter;
import symulation.ClientArrivalEvent;

@Bean
public class ListenerOpenFile implements ActionListener{
    
private JFileChooser fileChoosingWindow = new JFileChooser();
private static final String TXT_FILES_DIR = "/txtFiles";

protected UIEventQueue uiEventQueue;

private ApplicationConfiguration applicationConfiguration;

private MainLoop mainLoop;

    public ListenerOpenFile (UIEventQueue uiEventQueue, ApplicationConfiguration applicationConfiguration)  {
        JFileChooser fileChooser=new JFileChooser();
        URL resource = getClass().getResource(TXT_FILES_DIR);
        assert resource != null;
        File txtFilesDirectory = new File(resource.getPath());
        fileChooser.setCurrentDirectory(txtFilesDirectory);
        
        fileChoosingWindow=fileChooser;         
        this.uiEventQueue = uiEventQueue;
        this.applicationConfiguration = applicationConfiguration;
    }
    
  //TODO nice option would be to show somewhere infos about the file we opened: average time in queue, 
  //clients per minute etc (things already known but not included in the file)    
    
    @Override
    public void actionPerformed(ActionEvent e){
        mainLoop = BeanRegistry.getBeanByClass(MainLoop.class);
        uiEventQueue.publishPauseEvent();
        int optionChooser= fileChoosingWindow.showOpenDialog(null);

           if (optionChooser==JFileChooser.APPROVE_OPTION){
               analyze(e);
           }
           
           else if (mainLoop.isPaused()){
               return;
           }


       uiEventQueue.publishResumeEvent();

    }
    
    private void analyze(ActionEvent e) {
    	
    	File selectedFile = fileChoosingWindow.getSelectedFile();                 
        SortedSet<ClientArrivalEvent> timeTable= new TreeSet<>();
        
        try {
           timeTable = FileAnalyzer.analyze(selectedFile);
        }
        catch (IOException i){
            i.printStackTrace();
            uiEventQueue.publishNewMessageEvent("File opening failed");
        }
        catch (NumberFormatException ex){
           ex.printStackTrace();
            uiEventQueue.publishNewMessageEvent("Invalid file format!");

            actionPerformed(e);
        }
         


        Integer lastQueueIndex = timeTable.stream().max(Comparator.comparing(ClientArrivalEvent::getQueueNumber)).map(ClientArrivalEvent::getQueueNumber).orElseThrow(() -> new IllegalArgumentException("empty time table"));

         timeTable=processTimeTable(timeTable);

             if(applicationConfiguration.getNumberOfQueues() != lastQueueIndex){
                 mainLoop.pause();
                 applicationConfiguration.setNumberOfQueues(lastQueueIndex+1);
                 uiEventQueue.publishNewReinitializeEvent();
             }
                          

         uiEventQueue.publishNewTimetableEvent(timeTable);
    }
    
    // method to be overriden if we wanna do something on the time table
    protected SortedSet<ClientArrivalEvent> processTimeTable(SortedSet<ClientArrivalEvent> tt) {
    	return tt;
    }

}
