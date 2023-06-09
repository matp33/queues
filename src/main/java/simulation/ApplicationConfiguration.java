package simulation;

import spring2.Bean;

@Bean
public class ApplicationConfiguration {

    public static final boolean DEBUG_GRID_ENABLED = true;

    private int numberOfQueues;

    private double simulationTime;

    public double getSimulationTime() {
        return simulationTime;
    }

    public void setSimulationTime(double simulationTime) {
        this.simulationTime = simulationTime;
    }

    public void setNumberOfQueues(int numberOfQueues){
        this.numberOfQueues = numberOfQueues;
    }

    public int getNumberOfQueues() {
        return numberOfQueues;
    }

}
