

package listeners;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JFileChooser;

import constants.UIEventType;
import events.UIEventHandler;
import events.UIEvent;
import events.UIEventQueue;
import otherFunctions.FileAnalyzer;
import spring2.Bean;
import symulation.ApplicationConfiguration;
import symulation.ClientArrivalEvent;
import symulation.Manager;
import view.SimulationPanel;

@Bean
public class OpenFileButtonClickHandler implements UIEventHandler {
    
private JFileChooser fileChoosingWindow;
private static final String TXT_FILES_DIR = "/txtFiles";


private ApplicationConfiguration applicationConfiguration;

private SimulationPanel simulationPanel;

private Manager manager;

private UIEventQueue uiEventQueue;


    public OpenFileButtonClickHandler(ApplicationConfiguration applicationConfiguration, SimulationPanel simulationPanel, Manager manager, UIEventQueue uiEventQueue)  {
        this.simulationPanel = simulationPanel;
        this.manager = manager;
        this.uiEventQueue = uiEventQueue;
        JFileChooser fileChooser=new JFileChooser();
        URL resource = getClass().getResource(TXT_FILES_DIR);
        assert resource != null;
        File txtFilesDirectory = new File(resource.getPath());
        fileChooser.setCurrentDirectory(txtFilesDirectory);
        
        fileChoosingWindow=fileChooser;         
        this.applicationConfiguration = applicationConfiguration;
        this.uiEventQueue.subscribeToEvents(this, getUiEventType());

    }

    protected UIEventType getUiEventType(){
        return UIEventType.OPEN_FILE_BUTTON_CLICK;
    }
    
  //TODO nice option would be to show somewhere infos about the file we opened: average time in queue, 
  //clients per minute etc (things already known but not included in the file)    


    @Override
    public void handleEvent(UIEvent uiEvent){
        boolean wasPaused = manager.pause();
        int optionChooser= fileChoosingWindow.showOpenDialog(null);

        if (optionChooser==JFileChooser.APPROVE_OPTION){
            analyze();
        }

        if (!wasPaused){
            manager.resumeSimulation();
        }
    }


    private void analyze() {
    	
    	File selectedFile = fileChoosingWindow.getSelectedFile();                 
        SortedSet<ClientArrivalEvent> timeTable= new TreeSet<>();
        
        try {
           timeTable = FileAnalyzer.analyze(selectedFile);
        }
        catch (IOException i){
            i.printStackTrace();
            simulationPanel.displayMessage("File opening failed");
        }
        catch (NumberFormatException ex){
           ex.printStackTrace();
            simulationPanel.displayMessage("Invalid file format!");
        }
         


        Integer lastQueueIndex = timeTable.stream().max(Comparator.comparing(ClientArrivalEvent::getQueueNumber)).map(ClientArrivalEvent::getQueueNumber).orElseThrow(() -> new IllegalArgumentException("empty time table"));

         timeTable=processTimeTable(timeTable);

             if(applicationConfiguration.getNumberOfQueues() != lastQueueIndex){
                 applicationConfiguration.setNumberOfQueues(lastQueueIndex+1);
             }

         manager.restart(0, timeTable);
    }
    
    protected SortedSet<ClientArrivalEvent> processTimeTable(SortedSet<ClientArrivalEvent> tt) {
    	return tt;
    }

}
