package view;

import constants.UIEventType;
import events.UIEventHandler;
import events.UIEvent;
import events.UIEventQueue;
import spring2.Bean;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;


@Bean
public class NavigationPanel implements UIEventHandler {

    public static final String BUTTON_PAUSE="Pause";
    public static final String BUTTON_RESUME="Resume";
    public static final String BUTTON_OPEN="Open file";
    public static final String BUTTON_FROM_START="Restart";
    public static final String BUTTON_EXTRACT="Extract queue";

    public static final String BUTTONS_FONT = "Bodoni MT";


    private JButton btnRestart;
    private JButton btnPause;
    private JButton btnOpenFile;
    private JButton btnExtract;

    private JLabel time;

    private DecimalFormat timeFormat;

    private JPanel panel;

    private UIEventQueue uiEventQueue;

    public NavigationPanel(UIEventQueue uiEventQueue) {
        this.uiEventQueue = uiEventQueue;
        uiEventQueue.subscribeToEvents(this, UIEventType.TIME_VALUE_CHANGE);
        timeFormat = new DecimalFormat("0.00");
    }

    public JPanel getPanel() {
        return panel;
    }


    public JPanel initializePanel (){
        JPanel navigationPanel = new JPanel();
        navigationPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        navigationPanel.setLayout(new FlowLayout());
        JButton[] buttons = initializeButtons();
        for (int i=0; i<buttons.length;i++){
            navigationPanel.add(buttons[i]);
        }
        initializeTimeLabel();
        navigationPanel.add(time);
        panel = navigationPanel;
        return navigationPanel;
    }

    void initializeTimeLabel (){
        time = new JLabel();
        time.setFont(new Font(BUTTONS_FONT, Font.PLAIN,20));
        time.setForeground(new Color(0,45,110));
    }

    private JButton[] initializeButtons (){
        btnPause=new JButton(BUTTON_PAUSE);
        btnOpenFile=new JButton(BUTTON_OPEN);
        btnRestart=new JButton(BUTTON_FROM_START);
        btnExtract=new JButton(BUTTON_EXTRACT);


        btnExtract.addActionListener(e-> {
            publishEvent(UIEventType.EXTRACT_BUTTON_CLICK);
        });
        btnPause.addActionListener(e-> {
            publishEvent(UIEventType.PAUSE_BUTTON_CLICK);

        });
        btnOpenFile.addActionListener(e-> {
            publishEvent(UIEventType.OPEN_FILE_BUTTON_CLICK);
        });
        btnRestart.addActionListener(e-> {
            publishEvent(UIEventType.RESTART_BUTTON_CLICK);
        });
        btnRestart.setEnabled(false);

        JButton [] buttons=new JButton[] {btnOpenFile,btnExtract, btnRestart, btnPause};

        for (int i=0; i<buttons.length;i++){
            buttons[i].setFont(new Font(BUTTONS_FONT, Font.PLAIN,15));
        }
        return buttons;
    }

    private void publishEvent(UIEventType eventType) {
        uiEventQueue.publishNewEvent(new UIEvent<>(eventType, new Object()));
    }

    public void setButtonStopActiveness(boolean isActive){
        if (btnPause.isEnabled()!=isActive){
            btnPause.setEnabled(isActive);
        }
    }

    public void setButtonRestartToActive(){
        btnRestart.setEnabled(true);
    }

    public void setButtonStopToPause(){
        btnPause.setText(BUTTON_PAUSE);
    }

    public void setButtonStopToResume(){
        btnPause.setText(BUTTON_RESUME);
    }

    @Override
    public void handleEvent(UIEvent uiEvent) {
        this.time.setText("Time: "+ timeFormat.format(uiEvent.getData()));

    }
}
