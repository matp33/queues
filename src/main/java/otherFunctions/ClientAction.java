package otherFunctions;


import constants.ClientPositionType;
import visualComponents.Client;

import java.util.Comparator;

public class ClientAction implements Comparable<ClientAction> {
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

    @Override
    public int compareTo(ClientAction o) {
        int timeCompareResult = Double.compare(time, o.getTime());
        return timeCompareResult ==0 ? Integer.compare(client.getId(), o.client.getId()): timeCompareResult;
    }
}
