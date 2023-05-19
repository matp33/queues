package symulation;

import constants.ClientPositionType;
import interfaces.AnimatedObject;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import listeners.ListenerExtraction;
import listeners.ListenerFromTheStart;
import listeners.ListenerOpenFile;
import listeners.ListenerStopStart;
import visualComponents.Client;

public class Painter extends JPanel {

    private static final long serialVersionUID = 1L;

    public static final String BUTTON_PAUSE="Pause";
    public static final String BUTTON_RESUME="Resume";
    public static final String BUTTON_OPEN="Open file";
    public static final String BUTTON_FROM_START="Restart";
    public static final String BUTTON_EXTRACT="Extract queue";
    private static final String MAX_VISIBLE_TIME_VALUE="+99";

    private JFrame window;

    private JButton btnRestart;
    private JButton btnPause;
    private JButton btnOpenFile;
    private JButton btnExtract;
    private JLabel time;
    private JPanel bottomPanel;

    Dimension maxTextDimensions; //TODO can remove it after we dont need to display clientNumber
    private DecimalFormat decFormat;
    private CustomLayout layout;

    public int maxClientsVisibleInQueue;

    private Rectangle movementArea; // area that will change during simulation
    private List <AnimatedObject> objects;


    private Manager manager;

    public Painter(final int numberOfQueues,
                   Manager manager) throws IOException {

        this.manager=manager;
        objects= new ArrayList <AnimatedObject>();
        window = new JFrame();
        decFormat = new DecimalFormat("0.00");

        setLayout(null);
        setFont(getFont().deriveFont(25f));

        double maxFontWidth=getFontMetrics(getFont()).stringWidth(MAX_VISIBLE_TIME_VALUE);
        double maxFontHeight=getFontMetrics(getFont()).getHeight();
        maxTextDimensions=new Dimension((int)maxFontWidth,(int)maxFontHeight);


        initiateButtons();
        initiate(numberOfQueues);

        window.pack();
        window.setLocationRelativeTo(null);
        window.setResizable(false);
        window.setVisible(true);

    }

    private void initiateButtons(){

        btnPause=new JButton(BUTTON_PAUSE);
        btnOpenFile=new JButton(BUTTON_OPEN);
        btnRestart=new JButton(BUTTON_FROM_START);
        btnExtract=new JButton(BUTTON_EXTRACT);

        // ******** Buttons positioning

        JButton [] buttons=new JButton [4];
        buttons[0]=btnExtract;
        buttons[1]=btnOpenFile;
        buttons[2]=btnRestart;
        buttons[3]=btnPause;

        bottomPanel=new JPanel();
        bottomPanel.setLayout(new FlowLayout());

        for (int i=0; i<buttons.length;i++){
            bottomPanel.add(buttons[i]);
        }

        time = new JLabel();
        time.setFont(new Font("Times new roman", Font.BOLD,20));
        time.setForeground(new Color(0,45,110));
        bottomPanel.add(time);

        JPanel mainPanel=new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(this);
        mainPanel.add(bottomPanel,BorderLayout.SOUTH);

        window.add(mainPanel);

        ActionListener listenerStopStart=new ListenerStopStart(manager);
        ActionListener listenerOpen = new ListenerOpenFile(manager);
        ActionListener listenerFromStart=new ListenerFromTheStart(manager);
        ActionListener listenerExtract=new ListenerExtraction (manager);

        btnExtract.addActionListener(listenerExtract);
        btnPause.addActionListener(listenerStopStart);
        btnOpenFile.addActionListener(listenerOpen);
        btnRestart.addActionListener(listenerFromStart);
        btnRestart.setEnabled(false);

    }


    public void initiate(int numberOfQueues) throws IOException {

        Border blackline, raisedetched, loweredetched,
                raisedbevel, loweredbevel, empty;

        blackline = BorderFactory.createLineBorder(Color.black);
        raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        raisedbevel = BorderFactory.createRaisedBevelBorder();
        loweredbevel = BorderFactory.createLoweredBevelBorder();
        empty = BorderFactory.createEmptyBorder();

        this.setBorder(blackline);

        //https://docs.oracle.com/javase/tutorial/uiswing/components/border.html TODO mess with it
        bottomPanel.setBorder(blackline);
//         bottomPanel.setBackground(Color.BLUE);

        layout=new CustomLayout(numberOfQueues, bottomPanel);

        layout.calculateWindowSize(numberOfQueues);
        maxClientsVisibleInQueue=layout.getMaximumVisibleClients();
        Rectangle r=layout.getMovementArea();

        setMovementArea(r);



    }



    @Override
    public Dimension getPreferredSize() {
        return layout.getWindowDimensions();
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2=(Graphics2D) g;

        for (int i=0; i<objects.size(); i++){
            objects.get(i).paintComponent(g2);
//        	if (objects.get(i) instanceof Client){
//        		Client c= (Client)objects.get(i);     
//        		System.out.println("client nr "+c.id);
//        	}
        }

    }

    public void paintClient(Client c){
        repaint();
    }



    public Point calculateClientCoordinates(int clientNumber, int queueNumber, ClientPositionType position){
        return layout.calculateClientDestinationCoordinates(clientNumber, queueNumber, position);
    }

    public Point calculateQueueIndicatorPosition(int queueNumber){
        return layout.calculateQueueIndicatorPosition(queueNumber);
    }

    public void setButtonStopActiveness(boolean isActive){
        if (btnPause.isEnabled()!=isActive){
            btnPause.setEnabled(isActive);
        }
    }

    public void setButtonRestartToActive(){
        btnRestart.setEnabled(true);
    }

    public void setButtonStopToPaused(){
        btnPause.setText(BUTTON_PAUSE);
    }

    public void setButtonStopToResume(){
        btnPause.setText(BUTTON_RESUME);
    }


    public Point getCounterPosition(int counterPosition){
        return layout.calculateCashRegisterPosition(counterPosition);
    }

    public Point getDoorPosition(){
        return layout.calculateDoorPosition();
    }

    public Rectangle getMovementArea() {
        return movementArea;
    }

    public void setMovementArea(Rectangle movementArea) {
        this.movementArea = movementArea;
    }

    public Point calculateWaitingRoomIndicatorPosition() {
        return layout.calculateWaitingRoomIndicatorPosition();
    }

    public void addObject (AnimatedObject a){
        objects.add(a);
    }


    public void updateTime(double time){
        this.time.setText("Time: "+decFormat.format(time));
    }

    public void cleanScreen(){
        repaint(movementArea);
    }


    public void stopSprites(){

        for (int i=0; i<objects.size(); i++){
            objects.get(i).interrupt();
        }
    }

    public void resumeSprites(){
        for (int i=0; i<objects.size(); i++){
            objects.get(i).resume();
            if (objects.get(i) instanceof Client){
                Client c=(Client)objects.get(i);
//				System.out.println("@@"+c.id);
            }
        }
    }

    public void removeObject(AnimatedObject obj){
        objects.remove(obj);
    }

    public Dimension getWindowDimensions(){
        return layout.getWindowDimensions();
    }

    public List <AnimatedObject> getAllObjects(){
        return new ArrayList <AnimatedObject> (objects);
    }

    public void clean (){
        while (objects.size()>0)
            objects.remove(0);
    }


}

