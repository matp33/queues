

package uieventshandlers;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import constants.RestartOption;
import constants.UIEventType;
import events.UIEventHandler;
import events.UIEvent;
import events.UIEventQueue;
import utilities.ExpressionAnalyzer;
import spring2.Bean;
import simulation.ApplicationConfiguration;
import simulation.SimulationController;
import simulation.Simulation;
import view.RestartSimulationPanel;
import view.SimulationPanel;

@Bean
public class RestartButtonClickHandler implements UIEventHandler {

    private final String FORMAT_ERROR=
            "<html><font color='red'>Invalid input.<br>" +
            "Please enter a valid number..</font></html>";

    private final String RANGE_ERROR=
            "<html><font color='red'>Please enter a proper number<br>" +   
            "according to symulation time.</font></html>";


    private ApplicationConfiguration applicationConfiguration;

    private RestartSimulationPanel restartSimulationPanel;

    private SimulationController simulationController;

    private SimulationPanel simulationPanel;

    private class ValidationResult {
        private boolean valid;
        private double time;

    }

    public RestartButtonClickHandler(UIEventQueue uiEventQueue, ApplicationConfiguration applicationConfiguration, RestartSimulationPanel restartSimulationPanel, SimulationController simulationController, SimulationPanel simulationPanel){

        this.applicationConfiguration = applicationConfiguration;
        this.restartSimulationPanel = restartSimulationPanel;
        this.simulationController = simulationController;
        this.simulationPanel = simulationPanel;
        uiEventQueue.subscribeToEvents(this, UIEventType.RESTART_BUTTON_CLICK);
        restartSimulationPanel.createPanel();
    }

    @Override
    public void handleEvent(UIEvent<?> uiEvent) {
        JPanel panel = restartSimulationPanel.getPanel();

        simulationController.pauseSimulation();
        int chosenOption= simulationPanel.displayWindowWithPanel(panel, Simulation.TITLE_FROM_BEGINNING);

        if (chosenOption==JOptionPane.NO_OPTION){
            simulationController.startSimulation();
            return;
        }

        if (chosenOption==JOptionPane.YES_OPTION ){

            if (restartSimulationPanel.getRestartOptionActive().equals(RestartOption.FROM_SELECTED_TIME)) {
                ValidationResult validationResult = validate();
                if (validationResult.valid){
                    simulationController.restart(validationResult.time);
                    restartSimulationPanel.hideError();
                }
                else{
                    handleEvent(uiEvent);
                }

            }
            else{
                simulationController.restart(0);

            }


        }

    }

    private ValidationResult validate(){
        JTextField timeInput = restartSimulationPanel.getTimeInput();
        boolean isValidInput = ExpressionAnalyzer.analyze(timeInput.getText());
        boolean isWithinSimulationRange = false;
        double time = 0;
        if (isValidInput){
            time = Double.parseDouble(timeInput.getText());
            isWithinSimulationRange = applicationConfiguration.getSimulationTime() >= time;
        }
        if (!isValidInput){
            setErrorTextTo(FORMAT_ERROR);
        }
        if (!isWithinSimulationRange){
            setErrorTextTo(RANGE_ERROR);
        }
        ValidationResult validationResult = new ValidationResult();
        validationResult.valid = isValidInput && isWithinSimulationRange;
        validationResult.time = time;
        return validationResult;
    }



    private void setErrorTextTo (String text){
        restartSimulationPanel.setErrorText(text);
    }

}
