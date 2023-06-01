package view;

import listeners.ListenerExtraction;
import listeners.ListenerFromTheStart;
import listeners.ListenerOpenFile;
import listeners.ListenerStopStart;
import spring2.Bean;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;


@Bean
public class NavigationPanel {

    public static final String BUTTON_PAUSE="Pause";
    public static final String BUTTON_RESUME="Resume";
    public static final String BUTTON_OPEN="Open file";
    public static final String BUTTON_FROM_START="Restart";
    public static final String BUTTON_EXTRACT="Extract queue";

    public static final String BUTTONS_FONT = "Bodoni MT";

    private ListenerStopStart listenerStopStart;
    private ListenerOpenFile listenerOpen;
    private ListenerFromTheStart listenerFromStart;
    private ListenerExtraction listenerExtract;

    private JButton btnRestart;
    private JButton btnPause;
    private JButton btnOpenFile;
    private JButton btnExtract;

    private JLabel time;

    private DecimalFormat timeFormat;

    private JPanel panel;

    public NavigationPanel(ListenerStopStart listenerStopStart, ListenerOpenFile listenerOpen, ListenerFromTheStart listenerFromStart, ListenerExtraction listenerExtract) {
        this.listenerStopStart = listenerStopStart;
        this.listenerOpen = listenerOpen;
        this.listenerFromStart = listenerFromStart;
        this.listenerExtract = listenerExtract;
        timeFormat = new DecimalFormat("0.00");
    }

    public JPanel getPanel() {
        return panel;
    }

    public void updateTime(double time){
        this.time.setText("Time: "+ timeFormat.format(time));
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


        btnExtract.addActionListener(listenerExtract);
        btnPause.addActionListener(listenerStopStart);
        btnOpenFile.addActionListener(listenerOpen);
        btnRestart.addActionListener(listenerFromStart);
        btnRestart.setEnabled(false);

        JButton [] buttons=new JButton[] {btnOpenFile,btnExtract, btnRestart, btnPause};

        for (int i=0; i<buttons.length;i++){
            buttons[i].setFont(new Font(BUTTONS_FONT, Font.PLAIN,15));
        }
        return buttons;
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

}
