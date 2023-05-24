package otherFunctions;


import constants.ClientPositionType;
import visualComponents.Client;

public class ClientAction {
    private double time;
    private ClientPositionType clientPositionType;
    private Client client;
   

    public ClientAction (double time, ClientPositionType clientPositionType, Client client){
        this.time=time;
        this.clientPositionType = clientPositionType;
        this.client=client;
    }

    public double getTime(){
        return time;
    }

    public ClientPositionType getClientPositionType(){
        return clientPositionType;
    }
    public Client getClient(){
        return client;
    }
        
}
