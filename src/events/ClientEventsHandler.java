package events;

import constants.ClientPositionType;
import core.ChangeableObject;
import core.MainLoop;
import otherFunctions.ClientAction;
import symulation.ApplicationConfiguration;
import symulation.Painter;
import visualComponents.Client;

import java.util.ArrayList;
import java.util.List;

public class ClientEventsHandler implements ChangeableObject {

    private List<ClientAction> listOfEvents = new ArrayList<>();

    private Painter painter;

    public ClientEventsHandler() {
        painter = ApplicationConfiguration.getInstance().getPainter();
    }

    public void setEventsList(List<ClientAction> listOfEvents) {
        this.listOfEvents = listOfEvents;
    }
    @Override
    public void update(double currentTime) {
        if (listOfEvents.isEmpty()){
            return;
        }
        ClientAction clientAction=listOfEvents.get(0);
        double actionTime=clientAction.getTime();

        if (currentTime < actionTime){
            return;
        }

        listOfEvents.remove(0);

        ClientPositionType action=clientAction.getClientPositionType();
        Client client=clientAction.getClient();

        switch (action){
            case ARRIVAL:
                client.moveToWaitingRoom();
                client.startDrawingMe();
                break;
            case WAITING_IN_QUEUE:
                client.moveToQueue();
                client.startDrawingMe();
                break;
            case PAUSE:
                painter.pauseSimulationAndAskQuestion();
                break;
        }
    }

    public void addToLoop() {
        MainLoop.getInstance().addObject(this);
    }
}
