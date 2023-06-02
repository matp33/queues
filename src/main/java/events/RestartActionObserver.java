package events;

import constants.RestartOption;

public interface RestartActionObserver {
    void actionPerformed (RestartOption fromBeginning);
}
