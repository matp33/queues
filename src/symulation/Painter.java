package symulation;

import constants.ClientPositionType;
import constants.SimulationEventType;
import core.MainLoop;
import events.UIEventQueue;
import events.EventSubscriber;
import interfaces.AnimatedObject;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import listeners.ListenerExtraction;
import listeners.ListenerFromTheStart;
import listeners.ListenerOpenFile;
import listeners.ListenerStopStart;
import otherFunctions.ClientAction;
import otherFunctions.TimeTable;
import visualComponents.Client;

public class Painter extends JPanel {

    private static final long serialVersionUID = 1L;

    public static final String BUTTON_PAUSE="Pause";
    public static final String BUTTON_RESUME="Resume";
    public static final String BUTTON_OPEN="Open file";
    public static final String BUTTON_FROM_START="Restart";
    public static final String BUTTON_EXTRACT="Extract queue";
    private static final String MAX_VISIBLE_TIME_VALUE="+99";

    private TimeTable timeTable = new TimeTable();
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



    private static int NUMBER_OF_QUEUES = 1;

    private static Painter instance = null;
    private List<ClientAction> listOfEvents;
    private Thread clientMovementThread;

    private final UIEventQueue uIEventQueue = new UIEventQueue();

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
        initiateWindow();

    }

    public boolean isTimeWithinSimulationRange(double time){
        return timeTable.departures.length>0 && time<=timeTable.departures[timeTable.departures.length-1][0];
    }

    private void initiateWindow() throws IOException {
        initiateButtons();
        initiate();

        window.pack();
        window.setLocationRelativeTo(null);
        window.setResizable(false);
        window.setVisible(true);
    }

    public static Painter initialize (int numberOfQueues) throws Exception {
        NUMBER_OF_QUEUES = numberOfQueues;
        if (instance != null){
            if (!MainLoop.getInstance().isPaused()){
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

    public void addEventsSubscriber(EventSubscriber eventSubscriber){
        uIEventQueue.addSubscriber(eventSubscriber);
    }

    public void setTimeTable(double [][] arrivals, double [][] departures){
        timeTable.arrivals=arrivals;
        timeTable.departures=departures;
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

        ActionListener listenerStopStart=new ListenerStopStart(this);
        ActionListener listenerOpen = new ListenerOpenFile(this, uIEventQueue);
        ActionListener listenerFromStart=new ListenerFromTheStart(this,uIEventQueue);
        ActionListener listenerExtract=new ListenerExtraction (this, uIEventQueue);

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


    public void stopSprites() throws Exception {

        for (int i=0; i<objects.size(); i++){
            objects.get(i).interrupt();
        }
    }

    public int displayWindowWithPanel(Component panel, String title){

        return JOptionPane.showOptionDialog(this, panel, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, new Object[]{"Ok","Cancel"}, "Ok");

    }


    public void displayMessage (String text){
        JOptionPane.showMessageDialog(this, text);
    }

    public void resume(boolean fromZero){


        try {
            MainLoop instance = MainLoop.getInstance();
            if (fromZero) instance.setTimePassed(0);
            instance.resume();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        repaint();
        startThreadForClientPositionUpdates();
        resumeSprites();
        setButtonStopToPaused();

    }

    public void pause() throws Exception {


        setButtonStopToResume();
        stopSprites();
        try {
            MainLoop.getInstance().pause();
        } catch (Exception e) {
            throw new RuntimeException(e);
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


    public void setEventsList(List<ClientAction> listOfEvents) {
        this.listOfEvents = listOfEvents;
    }

    public void startThreadForClientPositionUpdates() {

        Runnable r = new Runnable (){
            @Override
            public void run (){
                try {
                    loop();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }

            private void loop() throws Exception {
                MainLoop mainLoop = MainLoop.getInstance();
                while (!listOfEvents.isEmpty() && !mainLoop.isPaused()){

                    ClientAction clientAction=listOfEvents.get(0);
                    double actionTime=clientAction.getTime();

                    synchronized (listOfEvents){
                        double timePassed = (double) mainLoop.getTimePassedMilliseconds() / 1000;
                        if (timePassed < actionTime){
                            Thread.sleep((long)actionTime *1000-(long)timePassed * 1000);
                        }
                    }
                    System.out.print("###### ");
                    Thread.getAllStackTraces().keySet().stream().map(Thread::getName).sorted().peek(s-> System.out.print(", ")).forEach(System.out::print);
                    System.out.println();
                    if (mainLoop.isPaused()){
                        System.out.println("returned");
                        return;
                    }

                    listOfEvents.remove(0);

                    SimulationEventType action=clientAction.getAction();
                    Client client=clientAction.getClient();

                    switch (action){
                        case ARRIVAL:
//		                     	    System.out.println("arrival");
                            client.moveToWaitingRoom();
                            client.startDrawingMe();
                            break;
                        case APPEAR_IN_POSITION:
                            client.moveToQueue();
                            client.startDrawingMe();
//		                     	   System.out.println("appear");
                            break;
                        case DEPARTURE:
//		                     	    System.out.println("exit "+client.abc);
                            client.moveToExit();
                            break;
                        case PAUSE:
                            pause();
                            boolean b=askQuestion(Simulation.NO_MORE_ARRIVALS,
                                    Simulation.TITLE_NO_MORE_ARRIVALS);
                            if (b==false){
                                finishSimulation(true);
                            }
                            else{
                                resume(false);
                                return;
                            }
                            break;
                    }

//		                    System.out.println("delete 1; left: "+listOfEvents.size());

                }
            }
        };

        System.out.println("calle"+listOfEvents.size());
        double[] eventsTimes = new double [listOfEvents.size()];

        for (int i=0; i<listOfEvents.size();i++){
            eventsTimes[i]=listOfEvents.get(i).getTime();
            if (listOfEvents.get(i).getClient()!=null)
                System.out.println("!"+listOfEvents.get(i).getClient().id);
        }

        clientMovementThread =new Thread(r);
        clientMovementThread.start();

    }

    public void finishSimulation(boolean skipMsg){
        setButtonStopActiveness(false);
        if (!skipMsg)
            displayMessage(Simulation.SIMULATION_FINISHED);
    }

    public boolean askQuestion (String question, String title){

        int chosenOption=JOptionPane.showOptionDialog(null, question, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, new Object[]{"Yes","No"}, "Yes");

        if (chosenOption==JOptionPane.YES_OPTION){
            return true;
        }
        else{
            return false;
        }

    }

}

