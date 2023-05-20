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

    private static int NUMBER_OF_QUEUES = 1;

    private static Painter instance = null;

    public static Painter getInstance() throws IOException {
        return instance;
    }

    private Painter() throws IOException {

        objects= new ArrayList <AnimatedObject>();
        window = new JFrame();
        decFormat = new DecimalFormat("0.00");

        setLayout(null);
        setFont(getFont().deriveFont(25f));

        double maxFontWidth=getFontMetrics(getFont()).stringWidth(MAX_VISIBLE_TIME_VALUE);
        double maxFontHeight=getFontMetrics(getFont()).getHeight();
        maxTextDimensions=new Dimension((int)maxFontWidth,(int)maxFontHeight);
        bottomPanel=new JPanel();
        layout=new CustomLayout(NUMBER_OF_QUEUES, bottomPanel);

    }

    private void initiateWindow() throws IOException {
        initiateButtons();
        initiate();

        window.pack();
        window.setLocationRelativeTo(null);
        window.setResizable(false);
        window.setVisible(true);
    }

    public static Painter initialize (int numberOfQueues) throws IOException {
        NUMBER_OF_QUEUES = numberOfQueues;
        if (instance != null){
            if (instance.manager.isRunning()){
                try {

                    throw new IllegalStateException("Stop the simulation first before changing queue numbers");
                }
                catch (IllegalStateException ex){
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
        }
        instance = new Painter();
        return instance;
    }


    public static int getNumberOfQueues() {
        return NUMBER_OF_QUEUES;
    }

    public void setManager (Manager manager) throws IOException {
        this.manager = manager;
        initiateWindow();
    }

    private void initiateButtons() throws IOException {

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


    public void initiate() throws IOException {

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

        layout.calculateWindowSize(NUMBER_OF_QUEUES);
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
            AnimatedObject animatedObject = objects.get(i);
            if (animatedObject.getPosition() != null){
                animatedObject.paintComponent(g2);
            }
        }

    }

    public void paintClient(Client c){
        repaint();
    }



    public Point calculateClientDestinationCoordinates(int clientNumber, int queueNumber, ClientPositionType position){
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


    public Point getCheckoutPosition(int checkoutIndex){
        return layout.calculateCheckoutPosition(checkoutIndex);
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
            objects.get(i).scheduleMoving();
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

