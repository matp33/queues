package view;

import constants.RestartOption;
import events.RestartActionObserver;
import listeners.RadioListenerFromStart;
import listeners.RadioListenerFromTime;
import spring2.Bean;

import javax.swing.*;
import java.awt.*;

@Bean
public class RestartSimulationPanel implements RestartActionObserver {
    private JPanel panel;
    private ButtonGroup buttonGroup;

    private JRadioButton btnFromStart;
    private JRadioButton btnFromTime;
    private Dimension dimension=new Dimension(200,100);

    private JTextField timeInput;

    private JLabel labelError;


    private RadioListenerFromStart radioListenerFromStart;

    private RadioListenerFromTime radioListenerFromTime;

    public RestartSimulationPanel(RadioListenerFromStart radioListenerFromStart, RadioListenerFromTime radioListenerFromTime) {
        this.radioListenerFromStart = radioListenerFromStart;
        this.radioListenerFromTime = radioListenerFromTime;
        radioListenerFromTime.setObserver(this);
        radioListenerFromStart.setObserver(this);
    }

    public void createPanel (){

        panel=new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));

        btnFromStart=new JRadioButton("From the start");
        btnFromTime=new JRadioButton("From the selected time");
        btnFromTime.setActionCommand(RestartOption.FROM_SELECTED_TIME.name());
        btnFromStart.setActionCommand(RestartOption.FROM_BEGINNING.name());

        btnFromTime.addActionListener(radioListenerFromTime);
        btnFromStart.addActionListener(radioListenerFromStart);

        buttonGroup=new ButtonGroup();
        buttonGroup.add(btnFromStart);
        buttonGroup.add(btnFromTime);
        buttonGroup.setSelected(btnFromStart.getModel(),true);

        timeInput = new JTextField();
        timeInput.setVisible(false);

        labelError=new JLabel("abc");
        labelError.setVisible(false);

        panel.add(btnFromStart);
        panel.add(btnFromTime);
        panel.add(timeInput);
        panel.add(labelError);
        panel.setPreferredSize(dimension);

    }

    public JTextField getTimeInput() {
        return timeInput;
    }

    public RestartOption getSelectedOption (){
        ButtonModel selection = buttonGroup.getSelection();
        return RestartOption.valueOf( selection.getActionCommand());
    }

    public JPanel getPanel() {
        return panel;
    }

    public void setErrorText (String text){
        labelError.setText(text);
        labelError.setVisible(true);
        SwingUtilities.getWindowAncestor(panel).pack();

    }

    public void hideError (){
        labelError.setVisible(false);
    }

    @Override
    public void actionPerformed(RestartOption restartOption) {
        if (restartOption.equals(RestartOption.FROM_BEGINNING)){
            timeInput.setVisible(false);
        }
        else{
            timeInput.setVisible(true);
        }
        SwingUtilities.getWindowAncestor(panel).pack();




    }
}
