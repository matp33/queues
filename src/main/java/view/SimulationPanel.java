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

    private List<AnimatedObject> objects = new ArrayList<>();
    private AppLayoutManager layout;

    public SimulationPanel(AppLayoutManager layout) {
        this.layout = layout;
        setFont(new Font("Times new roman", Font.PLAIN, 25));
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

    @Override
    public Dimension getPreferredSize() {
        return layout.getWindowDimensions();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2=(Graphics2D) g;

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
