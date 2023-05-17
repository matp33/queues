

package listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class RadioListenerFromStart implements ActionListener{

    @Override
    public void actionPerformed(ActionEvent e){

        JRadioButton b=(JRadioButton)e.getSource();
        JPanel mainPanel=(JPanel)b.getParent();
        Component [] d=mainPanel.getComponents();    

        for (int i=0;i<d.length;i++){
            if (d[i] instanceof JTextField){
                
                mainPanel.remove((JTextField)d[i]);
                mainPanel.repaint();
                System.out.println("to delete! ");
                
            }
            if (d[i] instanceof JLabel){
                JLabel lab = (JLabel) d[i];
                lab.setVisible(false);
            }
        }

        SwingUtilities.getWindowAncestor((Component) e.getSource()).pack();



    }

}
