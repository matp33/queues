package dto;


import constants.ClientPositionType;
import visualComponents.Client;

public class ClientActionDTO implements Comparable<ClientActionDTO> {
    private double time;
    private ClientPositionType clientPositionType;
    private Client client;
   

    public ClientActionDTO(double time, ClientPositionType clientPositionType, Client client){
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
    public int compareTo(ClientActionDTO o) {
        int timeCompareResult = Double.compare(time, o.getTime());
        if (client == null || o.getClient()==null){
            return timeCompareResult;
        }
        return timeCompareResult ==0 ? Integer.compare(client.getId(), o.client.getId()): timeCompareResult;
    }
}
