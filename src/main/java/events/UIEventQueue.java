package events;

import constants.UIEventType;
import spring2.Bean;

import java.util.*;

@Bean
public class UIEventQueue {

    private Map<UIEventType, Set<UIEventHandler>> subscribers = new HashMap<>();

    public void subscribeToEvents (UIEventHandler UIEventHandler, UIEventType... uiEventTypes){
        for (UIEventType uiEventType : uiEventTypes) {
            subscribers.putIfAbsent(uiEventType, new HashSet<>());
            subscribers.get(uiEventType).add(UIEventHandler);
        }
    }

    public <T> void publishNewEvent (UIEvent<T> uiEvent){
        subscribers.get(uiEvent.getUiEventType()).forEach(subscriber->subscriber.handleEvent(uiEvent));
    }

}
