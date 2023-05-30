
package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import spring2.Bean;
import spring2.BeanRegistry;
import symulation.Manager;
import symulation.Painter;

@Bean
public class ListenerStopStart implements ActionListener {

    private boolean isSimulationPaused = false;

    @Override
    public void actionPerformed (ActionEvent e){
        Painter painter = BeanRegistry.getBeanByClass(Painter.class);
        if (!isSimulationPaused){
            painter.pause();
            isSimulationPaused = true;

        }
        else{
            painter.resume(false);
            isSimulationPaused = false;
        }


    }

}
