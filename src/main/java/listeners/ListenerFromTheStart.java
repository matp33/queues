

package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import constants.RestartOption;
import events.UIEventQueue;
import otherFunctions.ExpressionAnalyzer;
import spring2.Bean;
import symulation.ApplicationConfiguration;
import symulation.Simulation;
import view.RestartSimulationPanel;

@Bean
public class ListenerFromTheStart implements ActionListener{

    private final String FORMAT_ERROR=
            "<html><font color='red'>Invalid input.<br>" +
            "Please enter a valid number..</font></html>";

    private final String RANGE_ERROR=
            "<html><font color='red'>Please enter a proper number<br>" +   
            "according to symulation time.</font></html>";


    private UIEventQueue uiEventQueue;


    private ApplicationConfiguration applicationConfiguration;

    private RestartSimulationPanel restartSimulationPanel;

    private class ValidationResult {
        private boolean valid;
        private double time;

    }

    public ListenerFromTheStart(UIEventQueue uiEventQueue, ApplicationConfiguration applicationConfiguration, RestartSimulationPanel restartSimulationPanel){

        this.uiEventQueue = uiEventQueue;
        this.applicationConfiguration = applicationConfiguration;
        this.restartSimulationPanel = restartSimulationPanel;

        restartSimulationPanel.createPanel();
    }

    @Override
    public void actionPerformed (ActionEvent e){

        JPanel panel = restartSimulationPanel.getPanel();

        uiEventQueue.publishPauseEvent();
        int chosenOption= uiEventQueue.publishNewDialogEvent(panel, Simulation.TITLE_FROM_BEGINNING);

        if (chosenOption==JOptionPane.NO_OPTION){
            return;
        }

        if (chosenOption==JOptionPane.YES_OPTION ){
            RestartOption selectedOption = restartSimulationPanel.getSelectedOption();

            if (selectedOption == RestartOption.FROM_SELECTED_TIME) {
                ValidationResult validationResult = validate();
                if (validationResult.valid){
                    uiEventQueue.publishRestartEvent(validationResult.time);
                    restartSimulationPanel.hideError();
                }
                else{
                    actionPerformed(e);
                }

            }
            else{
                uiEventQueue.publishRestartEvent(0);

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
