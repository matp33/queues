package clienthandling;

import constants.PositionInQueueToExit;
import dto.ClientToExitDTO;
import dto.PointWithTimeAndQueueIndexDTO;
import dto.PointWithTimeDTO;
import events.ObjectsManager;
import otherFunctions.ClientMovement;
import spring2.Bean;
import symulation.Painter;
import visualComponents.Client;

import java.util.*;
import java.util.stream.Collectors;

@Bean
public class ExitQueueManager {

    private ClientMovement clientMovement;

    private PositionInQueueToExit currentDirectionToShiftQueueToExit = PositionInQueueToExit.RIGHT;

    private ObjectsManager objectsManager;

    public ExitQueueManager(ClientMovement clientMovement, ObjectsManager objectsManager, Painter painter) {
        this.clientMovement = clientMovement;
        this.objectsManager = objectsManager;
    }

    public void moveClientToExit(Client client, NavigableSet<ClientToExitDTO> clientsMovingToExit){
        Set<ClientToExitDTO> clientsModified = new LinkedHashSet<>();
        ClientToExitDTO newClientData = createClientData(client, clientsMovingToExit);
        Set<ClientToExitDTO> shiftedClients = shiftClients(newClientData, clientsMovingToExit);
        clientsMovingToExit.add(newClientData);
        clientsModified.add(newClientData);
        clientsModified.addAll(shiftedClients);
        clientsModified.forEach(clientDTO-> {
            Client clientModified = clientDTO.getClient();
            clientMovement.calculateAndSetClientTrajectory(clientModified, clientDTO.getDestinationPoint());
        });

    }

    private Set<ClientToExitDTO> shiftClients(ClientToExitDTO newClientData, NavigableSet<ClientToExitDTO> clientsMovingToExit){
        Set<ClientToExitDTO> clientsModified = new LinkedHashSet<>();

        switch (newClientData.getPositionInQueueToExit()){
            case AT_DOOR:
                if (clientsMovingToExit.isEmpty() ){
                    Set<ClientToExitDTO> shiftedClients = shiftClientsAfterTheInsertedClientInOneSideOfQueue(clientsMovingToExit, newClientData);
                    clientsModified.addAll(shiftedClients);
                } else if (newClientData.getEstimatedTimeAtDestination() < clientsMovingToExit.first().getEstimatedTimeAtDestination()) {
                    ClientToExitDTO clientThatWasFirst = clientsMovingToExit.first();
                    clientsMovingToExit.remove(clientThatWasFirst);

                    PositionInQueueToExit position = pickQueueCloserToClientCheckFirstInQueue(clientThatWasFirst.getClient(),
                            clientsMovingToExit);
                    int indexInQueue = 1;
                    PointWithTimeDTO pointWithTimeDTO = clientMovement.calculateTimeToGetToPosition(clientThatWasFirst.getClient(), indexInQueue, position);
                    clientThatWasFirst.setPositionInQueueToExit(position);
                    clientThatWasFirst.setEstimatedTimeAtDestination(pointWithTimeDTO.getTime());
                    clientThatWasFirst.setIndexInPosition(indexInQueue);
                    clientThatWasFirst.setDestinationPoint(pointWithTimeDTO.getPoint());


                    Set<ClientToExitDTO> shiftedClients = shiftClientsAfterTheInsertedClientInOneSideOfQueue(clientsMovingToExit, clientThatWasFirst);
                    clientsMovingToExit.add(clientThatWasFirst);
                    clientsModified.add(clientThatWasFirst);
                    clientsModified.addAll(shiftedClients);

                }
                break;
            case LEFT:
            case RIGHT:
                shiftClientsAfterTheInsertedClientInOneSideOfQueue(clientsMovingToExit, newClientData);
                break;

        }
        return clientsModified;
    }

    private ClientToExitDTO createClientData (Client client, NavigableSet<ClientToExitDTO> clientsMovingToExit){
        PointWithTimeDTO positionAndTime = clientMovement.calculateTimeToGetToDoor(client);
        ClientToExitDTO newClientData = null;
        if (clientsMovingToExit.isEmpty() ){
            newClientData = new ClientToExitDTO(client, PositionInQueueToExit.AT_DOOR,positionAndTime.getTime(), 0, positionAndTime.getPoint());
        } else if (positionAndTime.getTime() < clientsMovingToExit.first().getEstimatedTimeAtDestination()) {
            newClientData = new ClientToExitDTO(client, PositionInQueueToExit.AT_DOOR, positionAndTime.getTime(), 0, positionAndTime.getPoint());
        } else{
            boolean addedToCollection = false;
            for (ClientToExitDTO clientMovingToExit : clientsMovingToExit) {
                positionAndTime = clientMovement.calculateTimeToGetToPosition(client, clientMovingToExit.getIndexInPosition(), clientMovingToExit.getPositionInQueueToExit());
                if (positionAndTime.getTime() < clientMovingToExit.getEstimatedTimeAtDestination()){
                    newClientData = new ClientToExitDTO(client, clientMovingToExit.getPositionInQueueToExit(), positionAndTime.getTime(), clientMovingToExit.getIndexInPosition(), positionAndTime.getPoint());
                    addedToCollection = true;
                    break;
                }
            }
            if (!addedToCollection){
                newClientData = pickQueueCloserToClient(client, clientsMovingToExit);
            }
        }
        return newClientData;

    }

    private PositionInQueueToExit pickQueueCloserToClientCheckFirstInQueue(Client client, NavigableSet<ClientToExitDTO> clientsMovingToExit) {
        PointWithTimeDTO leftQueuePointAndTime = clientMovement.calculateTimeToGetToPosition(client, 1, PositionInQueueToExit.LEFT);
        PointWithTimeDTO rightQueuePointAndTime = clientMovement.calculateTimeToGetToPosition(client, 1, PositionInQueueToExit.RIGHT);
        Double timeAtLeft = clientsMovingToExit.stream().filter(c -> c.getPositionInQueueToExit().equals(PositionInQueueToExit.LEFT)).findFirst().map(ClientToExitDTO::getEstimatedTimeAtDestination).orElse(Double.MAX_VALUE);
        Double timeAtRight = clientsMovingToExit.stream().filter(c -> c.getPositionInQueueToExit().equals(PositionInQueueToExit.RIGHT)).findFirst().map(ClientToExitDTO::getEstimatedTimeAtDestination).orElse(Double.MAX_VALUE);
        if (leftQueuePointAndTime.getTime()<timeAtLeft){
            return PositionInQueueToExit.LEFT;
        }
        else if (rightQueuePointAndTime.getTime()<timeAtRight) {
            return PositionInQueueToExit.RIGHT;
        }
        else{
            throw new IllegalStateException("Should not happen. Client can't go left or right of queue to exit");
        }
    }

    private ClientToExitDTO pickQueueCloserToClient(Client client, NavigableSet<ClientToExitDTO> clientsMovingToExit) {
        PointWithTimeAndQueueIndexDTO positionAndTimeToGetToLeftSide = getPositionAndTimeToGetToQueue(client, clientsMovingToExit, PositionInQueueToExit.LEFT);
        PointWithTimeAndQueueIndexDTO positionAndTimeToGetToRightSide = getPositionAndTimeToGetToQueue(client, clientsMovingToExit, PositionInQueueToExit.RIGHT);
        ClientToExitDTO clientToExitDTO;
        if (positionAndTimeToGetToLeftSide.getTime()< positionAndTimeToGetToRightSide.getTime()){
            clientToExitDTO = createClientToExitDTO(client, positionAndTimeToGetToLeftSide, PositionInQueueToExit.LEFT);
        }
        else{
            clientToExitDTO = createClientToExitDTO(client, positionAndTimeToGetToRightSide, PositionInQueueToExit.RIGHT);
        }
        return clientToExitDTO;
    }

    private Set<ClientToExitDTO> shiftClientsAfterTheInsertedClientInOneSideOfQueue(NavigableSet<ClientToExitDTO> clientsMovingToExit, ClientToExitDTO clientAfterWhichWeShouldShift) {
        Set<ClientToExitDTO> clientsModified = new LinkedHashSet<>();

        clientsMovingToExit.stream().filter(client -> client.getPositionInQueueToExit().equals(clientAfterWhichWeShouldShift.getPositionInQueueToExit()))
                .filter(clientToShift->clientToShift.getIndexInPosition() >= clientAfterWhichWeShouldShift.getIndexInPosition()).sorted(Comparator.comparing(ClientToExitDTO::getIndexInPosition))
                .forEach(clientToShift->{
                    clientToShift.setIndexInPosition(clientToShift.getIndexInPosition()+1);
                    PointWithTimeDTO destinationPositionAndTime = clientMovement.calculateTimeToGetToPosition(clientToShift.getClient(), clientToShift.getIndexInPosition(), clientToShift.getPositionInQueueToExit());
                    clientToShift.setEstimatedTimeAtDestination(destinationPositionAndTime.getTime());
                    clientToShift.setDestinationPoint(destinationPositionAndTime.getPoint());
                    clientsModified.add(clientToShift);
                });
        return clientsModified;
    }

    private ClientToExitDTO createClientToExitDTO(Client client, PointWithTimeAndQueueIndexDTO positionAndTime, PositionInQueueToExit positionInQueueToExit) {
        return new ClientToExitDTO(client, positionInQueueToExit, positionAndTime.getTime(), positionAndTime.getIndexInQueue(), positionAndTime.getPoint());
    }

    private PointWithTimeAndQueueIndexDTO getPositionAndTimeToGetToQueue(Client client, NavigableSet<ClientToExitDTO> clientsMovingToExit, PositionInQueueToExit positionInQueueToExit) {
        Optional<ClientToExitDTO> lastInQueueToLeftSide = clientsMovingToExit.stream().filter(clientDTO -> clientDTO.getPositionInQueueToExit().equals(positionInQueueToExit)).max(Comparator.naturalOrder());
        PointWithTimeDTO positionWithTime;
        int indexInPosition;
        if (lastInQueueToLeftSide.isPresent()){
            indexInPosition = lastInQueueToLeftSide.get().getIndexInPosition();
            indexInPosition++;
            positionWithTime = clientMovement.calculateTimeToGetToPosition(client,indexInPosition, positionInQueueToExit);
        }
        else{
            indexInPosition = 1;
            positionWithTime = clientMovement.calculateTimeToGetToPosition(client,indexInPosition, positionInQueueToExit);
        }
        return new PointWithTimeAndQueueIndexDTO(positionWithTime.getPoint(), positionWithTime.getTime(), indexInPosition);
    }

    public void handleClientWentOutsideView(Client client){
        objectsManager.removeClientFromQueueToExit(client);
        Set<ClientToExitDTO> clientsToShiftOldDirection = objectsManager.getClientsMovingToExit().stream().filter(clientDTO -> clientDTO.getPositionInQueueToExit().equals(currentDirectionToShiftQueueToExit)).collect(Collectors.toSet());
        toggleDirectionForMovingFromQueueToExit();
        Set<ClientToExitDTO> clientsToShiftNewDirection = objectsManager.getClientsMovingToExit().stream().filter(clientDTO -> clientDTO.getPositionInQueueToExit().equals(currentDirectionToShiftQueueToExit)).collect(Collectors.toSet());

        if (!clientsToShiftNewDirection.isEmpty()){
            shiftAllClientsFromOneSideOfQueue(client, clientsToShiftNewDirection);
        }
        else{
            shiftAllClientsFromOneSideOfQueue(client, clientsToShiftOldDirection);
        }
    }

    private void toggleDirectionForMovingFromQueueToExit() {
        if (currentDirectionToShiftQueueToExit.equals(PositionInQueueToExit.LEFT)){
            currentDirectionToShiftQueueToExit = PositionInQueueToExit.RIGHT;
        }
        else{
            currentDirectionToShiftQueueToExit = PositionInQueueToExit.LEFT;
        }
    }

    private void shiftAllClientsFromOneSideOfQueue(Client client, Set<ClientToExitDTO> clientsInOneSideOfQueue) {
        for (ClientToExitDTO clientToShift : clientsInOneSideOfQueue) {
            int indexInPosition = clientToShift.getIndexInPosition();
            indexInPosition --;
            clientToShift.setIndexInPosition(indexInPosition);
            if (indexInPosition == 0){
                clientToShift.setPositionInQueueToExit(PositionInQueueToExit.AT_DOOR);
                PointWithTimeDTO positionDoorWithTimeToGetThere = clientMovement.calculateTimeToGetToDoor(client);
                clientToShift.setEstimatedTimeAtDestination(positionDoorWithTimeToGetThere.getTime());
                Client clientShifted = clientToShift.getClient();
                clientMovement.calculateAndSetClientTrajectory(clientShifted, positionDoorWithTimeToGetThere.getPoint());
            }
            else{
                PointWithTimeDTO positionAndTime = clientMovement.calculateTimeToGetToPosition(client, indexInPosition, clientToShift.getPositionInQueueToExit());
                clientToShift.setEstimatedTimeAtDestination(positionAndTime.getTime());
                clientMovement.calculateAndSetClientTrajectory(clientToShift.getClient(), positionAndTime.getPoint());

            }
        }
    }

}
