package symulation;

import constants.ClientPositionType;
import core.MainLoop;
import events.UIEventQueue;
import events.EventSubscriber;
import interfaces.AnimatedObject;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import listeners.ListenerExtraction;
import listeners.ListenerFromTheStart;
import listeners.ListenerOpenFile;
import listeners.ListenerStopStart;
import spring2.Bean;
import spring2.BeanRegistry;
import visualComponents.Client;
import visualComponents.Door;
import visualComponents.StoreCheckout;

@Bean
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

    private ApplicationConfiguration applicationConfiguration;



    private final UIEventQueue uIEventQueue;

    private ListenerStopStart listenerStopStart;
   private  ListenerOpenFile listenerOpen;
    private ListenerFromTheStart listenerFromStart;
    private ListenerExtraction listenerExtract;

    private MainLoop mainLoop;

    public Painter(ApplicationConfiguration applicationConfiguration, CustomLayout customLayout, UIEventQueue uIEventQueue, ListenerStopStart listenerStopStart, ListenerOpenFile listenerOpen, ListenerFromTheStart listenerFromStart, ListenerExtraction listenerExtract)  {

        this.applicationConfiguration = applicationConfiguration;
        this.uIEventQueue = uIEventQueue;
        this.listenerStopStart = listenerStopStart;
        this.listenerOpen = listenerOpen;
        this.listenerFromStart = listenerFromStart;
        this.listenerExtract = listenerExtract;
        objects= new ArrayList <>();
        window = new JFrame();
        decFormat = new DecimalFormat("0.00");

        setLayout(null);
        setFont(getFont().deriveFont(25f));

        double maxFontWidth=getFontMetrics(getFont()).stringWidth(MAX_VISIBLE_TIME_VALUE);
        double maxFontHeight=getFontMetrics(getFont()).getHeight();
        maxTextDimensions=new Dimension((int)maxFontWidth,(int)maxFontHeight);
        bottomPanel=new JPanel();
        layout=customLayout;

    }

    public Door getDoor (){
        return objects.stream().filter(Door.class::isInstance).map(Door.class::cast).findAny().orElseThrow(()-> new IllegalArgumentException("No door inside objects"));
    }


    public void initiateWindow() {
        layout.initialize(applicationConfiguration.getNumberOfQueues(), bottomPanel);
        initiateButtons();
        initiate();

        window.pack();
        window.setLocationRelativeTo(null);
        window.setResizable(false);
        window.setVisible(true);
        mainLoop = BeanRegistry.getBeanByClass(MainLoop.class);
    }


    public void addEventsSubscriber(EventSubscriber eventSubscriber){
        uIEventQueue.addSubscriber(eventSubscriber);
    }


    private void initiateButtons() {

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


        btnExtract.addActionListener(listenerExtract);
        btnPause.addActionListener(listenerStopStart);
        btnOpenFile.addActionListener(listenerOpen);
        btnRestart.addActionListener(listenerFromStart);
        btnRestart.setEnabled(false);

    }


    public void initiate() {

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

        layout.calculateWindowSize(applicationConfiguration.getNumberOfQueues());
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


    public void stopSprites() {

        for (int i=0; i<objects.size(); i++){
            objects.get(i).interrupt(mainLoop.getTimePassedSeconds());
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


        if (fromZero) mainLoop.setTimePassed(0);
        mainLoop.resume();
        repaint();
        setButtonStopToPaused();

    }

    public void pauseSimulationAndAskQuestion (){
        pause();
        SwingUtilities.invokeLater(()->{
            boolean b=askQuestion(Simulation.NO_MORE_ARRIVALS,
                    Simulation.TITLE_NO_MORE_ARRIVALS);
            if (!b){
                finishSimulation(true);
            }
            else{
                resume(false);
            }
        });
    }

    public boolean pause() {
        setButtonStopToResume();
        stopSprites();
        boolean wasPaused = mainLoop.isPaused();
        mainLoop.pause();
        return wasPaused;
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

    public StoreCheckout getQueue(int queueNumber) {
        return objects.stream().filter(StoreCheckout.class::isInstance).map(StoreCheckout.class::cast).filter(checkout-> checkout.getCheckoutIndex()==queueNumber).findAny().orElseThrow(() -> new IllegalArgumentException("Queue with index not found: "+queueNumber));
    }
}

