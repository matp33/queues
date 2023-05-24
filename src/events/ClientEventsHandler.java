package events;

import constants.ClientPositionType;
import core.ChangeableObject;
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
    public void update(long currentTimeMilliseconds) {
        if (listOfEvents.isEmpty()){
            return;
        }
        ClientAction clientAction=listOfEvents.get(0);
        double actionTime=clientAction.getTime();

        double timePassed = (double) currentTimeMilliseconds / 1000;
        if (timePassed < actionTime){
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
            case EXITING:
                client.moveToExit();
                break;
            case PAUSE:
                painter.pauseSimulationAndAskQuestion();
                break;
        }
    }
}
