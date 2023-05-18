package otherFunctions;


import constants.SimulationEventType;
import visualComponents.Client;

public class ClientAction {
    private double time;
    private SimulationEventType  action;
    private Client client;
   

    public ClientAction (double time, SimulationEventType action, Client queueNumber){
        this.time=time;
        this.action=action;
        this.client=queueNumber;
    }

    public double getTime(){
        return time;
    }

    public SimulationEventType getAction (){
        return action;
    }
    public Client getClient(){
        return client;
    }
        
}
