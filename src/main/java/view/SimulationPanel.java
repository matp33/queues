package view;

import visualComponents.AnimatedObject;
import spring2.Bean;
import simulation.AppLayoutManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Bean
public class SimulationPanel extends JPanel {

    public static final String FINISHED_SIMULATION = "Simulation finished";
    public static final Font FONT = new Font("Jokerman", Font.BOLD, 40);
    public static final Color TEXT_COLOR = new Color(36, 85, 191);
    private List<AnimatedObject> objects = new ArrayList<>();
    private AppLayoutManager layout;

    private Point simulationFinishedPosition;

    private boolean simulationFinished = false;

    public SimulationPanel(AppLayoutManager layout) {
        this.layout = layout;
        setFont(new Font("Times new roman", Font.PLAIN, 25));
    }

    public void initialize (){
        simulationFinishedPosition = layout.calculateSimulationFinishedPosition();
    }

    public void addObject (AnimatedObject a){
        objects.add(a);
    }

    public void removeObjects(){
        objects.clear();
    }

    public void stopSprites() {

        for (int i=0; i<objects.size(); i++){
            objects.get(i).interrupt();
        }
    }

    public void removeObject(AnimatedObject obj){
        objects.remove(obj);
    }

    public void toggleSimulationFinished(){
        simulationFinished = !simulationFinished;
    }

    @Override
    public Dimension getPreferredSize() {
        return layout.getWindowDimensions();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2=(Graphics2D) g;
        if (simulationFinished){
            g.setFont(FONT);
            g.setColor(TEXT_COLOR);
            int stringWidth = g.getFontMetrics().stringWidth(FINISHED_SIMULATION);
            g2.drawString(FINISHED_SIMULATION, simulationFinishedPosition.x - stringWidth/2, simulationFinishedPosition.y);
        }

        for (int i=0; i<objects.size(); i++){
            AnimatedObject animatedObject = objects.get(i);
            if (animatedObject.getPosition() != null){
                animatedObject.paintComponent(g2);
            }
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
}
