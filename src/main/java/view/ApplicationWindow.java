package view;

import spring2.Bean;

import javax.swing.*;
import java.awt.*;

@Bean
public class ApplicationWindow {

    private JFrame window;

    private SimulationPanel simulationPanel;

    private NavigationPanel navigationPanel;

    private JPanel mainPanel;

    private Dimension simulationPanelDimension;

    public ApplicationWindow(SimulationPanel simulationPanel, NavigationPanel navigationPanel) {
        this.simulationPanel = simulationPanel;
        this.navigationPanel = navigationPanel;
    }


    public void initializeWindow() {
        window = new JFrame();
        window.add(mainPanel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setResizable(false);
        window.setVisible(true);
        window.setTitle("Queue simulator");
        window.setLayout(null);
        simulationPanelDimension = simulationPanel.getSize();
    }

    public Dimension getSimulationPanelDimension() {
        return simulationPanelDimension;
    }

    public void initializeMainPanel (){
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(simulationPanel);
        JPanel navigationPanel = this.navigationPanel.initializePanel();
        mainPanel.add(navigationPanel, BorderLayout.SOUTH);
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        this.mainPanel = mainPanel;
    }

}
