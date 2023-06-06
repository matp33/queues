

package uieventshandlers;

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
import utilities.FileAnalyzer;
import spring2.Bean;
import simulation.ApplicationConfiguration;
import dto.ClientArrivalEventDTO;
import simulation.SimulationController;
import view.SimulationPanel;

@Bean
public class OpenFileButtonClickHandler implements UIEventHandler {
    
private JFileChooser fileChoosingWindow;
private static final String TXT_FILES_DIR = "/txtFiles";


private ApplicationConfiguration applicationConfiguration;

private SimulationPanel simulationPanel;

private SimulationController simulationController;

private UIEventQueue uiEventQueue;


    public OpenFileButtonClickHandler(ApplicationConfiguration applicationConfiguration, SimulationPanel simulationPanel, SimulationController simulationController, UIEventQueue uiEventQueue)  {
        this.simulationPanel = simulationPanel;
        this.simulationController = simulationController;
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
    public void handleEvent(UIEvent<?> uiEvent){
        boolean wasPaused = simulationController.pauseSimulation();
        int optionChooser= fileChoosingWindow.showOpenDialog(null);

        if (optionChooser==JFileChooser.APPROVE_OPTION){
            analyze();
        }

        if (!wasPaused){
            simulationController.startSimulation();
        }
    }


    private void analyze() {
    	
    	File selectedFile = fileChoosingWindow.getSelectedFile();                 
        SortedSet<ClientArrivalEventDTO> timeTable;
        
        try {
           timeTable = FileAnalyzer.analyze(selectedFile);
            Integer lastQueueIndex = timeTable.stream().max(Comparator.comparing(ClientArrivalEventDTO::getQueueNumber)).map(ClientArrivalEventDTO::getQueueNumber).orElseThrow(() -> new IllegalArgumentException("empty time table"));

            timeTable=processTimeTable(timeTable);

            int newAmountOfQueues = lastQueueIndex + 1;
            if(applicationConfiguration.getNumberOfQueues() != newAmountOfQueues){
                applicationConfiguration.setNumberOfQueues(newAmountOfQueues);
                simulationController.requestRepaint();
            }

            simulationController.restart(0, timeTable);
        }
        catch (IOException i){
            i.printStackTrace();
            simulationPanel.displayMessage("File opening failed");
        }
        catch (NumberFormatException ex){
           ex.printStackTrace();
            simulationPanel.displayMessage("Invalid file format!");
        }

    }
    
    protected SortedSet<ClientArrivalEventDTO> processTimeTable(SortedSet<ClientArrivalEventDTO> tt) {
    	return tt;
    }

}
