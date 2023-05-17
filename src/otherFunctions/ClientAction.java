package otherFunctions;


import visualComponents.Client;

public class ClientAction {
    private double time;
    private int action;
    private Client client;
   

    public ClientAction (double time, int action,Client queueNumber){
        this.time=time;
        this.action=action;
        this.client=queueNumber;
    }

    public double getTime(){
        return time;
    }

    public int getAction (){
        return action;
    }
    public Client getClient(){
        return client;
    }
        
}
