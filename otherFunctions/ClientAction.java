package otherFunctions;


public class ClientAction <Time,Action,Client> {
    private Time time;
    private Action action;
    private Client client;
   

    public ClientAction (Time time, Action action,Client queueNumber){
        this.time=time;
        this.action=action;
        this.client=queueNumber;
    }

    public Time getTime(){
        return time;
    }

    public Action getAction (){
        return action;
    }
    public Client getClient(){
        return client;
    }
        
}
