

package listeners;

import constants.RestartOption;
import events.RestartActionObserver;
import spring2.Bean;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Bean
public class RadioListenerFromTime implements ActionListener{

    private RestartActionObserver observer;

    public void setObserver(RestartActionObserver restartActionObserver){
        this.observer= restartActionObserver;
    }

    @Override
    public void actionPerformed(ActionEvent e){
        observer.actionPerformed(RestartOption.FROM_SELECTED_TIME);
    }

}
