package events;

import constants.UIEventType;

public class UIEvent<T> {

    private UIEventType uiEventType;
    private T data;

    public UIEvent(UIEventType uiEventType, T data) {
        this.uiEventType = uiEventType;
        this.data = data;
    }

    public UIEventType getUiEventType() {
        return uiEventType;
    }

    public T getData() {
        return data;
    }

}
