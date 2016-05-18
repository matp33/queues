

package listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class RadioListenerFromTime implements ActionListener{

    JPanel panel;

    public RadioListenerFromTime (JPanel panel){
        
        this.panel=panel;
    }

    @Override
    public void actionPerformed(ActionEvent e){

        JRadioButton b=(JRadioButton)e.getSource();
        
        JPanel mainPanel=(JPanel)b.getParent(); 
        JTextField j=new JTextField();        
        mainPanel.add(j);
        j.requestFocusInWindow();      

        SwingUtilities.getWindowAncestor((Component) e.getSource()).pack();      


    }

}
