package view;

import spring2.Bean;
import symulation.ApplicationConfiguration;
import symulation.CustomLayout;

import javax.swing.*;
import java.awt.*;

@Bean
public class ApplicationWindow {

    private JFrame window;

    private SimulationPanel simulationPanel;

    private NavigationPanel navigationPanel;

    private CustomLayout customLayout;

    private ApplicationConfiguration applicationConfiguration;

    public ApplicationWindow(SimulationPanel simulationPanel, NavigationPanel navigationPanel, CustomLayout customLayout, ApplicationConfiguration applicationConfiguration) {
        this.simulationPanel = simulationPanel;
        this.navigationPanel = navigationPanel;
        this.customLayout = customLayout;
        this.applicationConfiguration = applicationConfiguration;
    }

    public void initialize (){
        JPanel mainPanel = initializeMainPanel();
        initializeWindow(mainPanel);
    }

    private void initializeWindow(JPanel mainPanel) {
        window = new JFrame();
        window.add(mainPanel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setResizable(false);
        window.setVisible(true);
        window.setTitle("Queue simulator");
        window.setLayout(null);
    }

    private JPanel initializeMainPanel (){
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(simulationPanel);
        JPanel navigationPanel = this.navigationPanel.initializePanel();
        mainPanel.add(navigationPanel, BorderLayout.SOUTH);
        customLayout.initialize(applicationConfiguration.getNumberOfQueues(), navigationPanel);
        customLayout.calculateWindowSize(applicationConfiguration.getNumberOfQueues());
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        return mainPanel;
    }

}
