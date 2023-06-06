package view;

import constants.RestartOption;
import constants.UIEventType;
import events.UIEvent;
import events.UIEventHandler;
import events.UIEventQueue;
import spring2.Bean;

import javax.swing.*;
import java.awt.*;

@Bean
public class RestartSimulationPanel implements UIEventHandler {
    private JPanel panel;
    private ButtonGroup buttonGroup;

    private JRadioButton btnFromStart;
    private JRadioButton btnFromTime;
    private Dimension dimension=new Dimension(200,100);

    private JTextField timeInput;

    private JLabel labelError;

    private UIEventQueue uiEventQueue;

    private RestartOption restartOptionActive = RestartOption.FROM_BEGINNING;

    public RestartSimulationPanel(UIEventQueue uiEventQueue) {
        this.uiEventQueue = uiEventQueue;
        uiEventQueue.subscribeToEvents(this, UIEventType.RESTART_FROM_BEGINNING_OPTION_CHOSEN, UIEventType.RESTART_FROM_TIME_OPTION_CHOSEN);
    }

    public void createPanel (){

        panel=new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));

        btnFromStart=new JRadioButton("From the start");
        btnFromTime=new JRadioButton("From the selected time");

        btnFromTime.addActionListener(e-> uiEventQueue.publishNewEvent(new UIEvent<>(UIEventType.RESTART_FROM_TIME_OPTION_CHOSEN, RestartOption.FROM_SELECTED_TIME)));
        btnFromStart.addActionListener(e-> uiEventQueue.publishNewEvent(new UIEvent<>(UIEventType.RESTART_FROM_BEGINNING_OPTION_CHOSEN, RestartOption.FROM_BEGINNING)));

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

    public RestartOption getRestartOptionActive() {
        return restartOptionActive;
    }

    public JTextField getTimeInput() {
        return timeInput;
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
    public void handleEvent(UIEvent<?> uiEvent) {
        switch (uiEvent.getUiEventType()){
            case RESTART_FROM_BEGINNING_OPTION_CHOSEN:
                timeInput.setVisible(false);
                break;
            case RESTART_FROM_TIME_OPTION_CHOSEN:
                timeInput.setVisible(true);
                break;
        }
        restartOptionActive = (RestartOption) uiEvent.getData();
        SwingUtilities.getWindowAncestor(panel).pack();
    }
}
