

package listeners;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import otherFunctions.ExpressionAnalyzer;
import symulation.Manager;
import symulation.Simulation;


public class ListenerFromTheStart implements ActionListener{

    private Manager manager;
    private boolean isErrorFound;
    private JLabel labelError;
    private JPanel panel;
    private ButtonGroup buttonGroup;
    private JRadioButton btnFromStart;
    private JRadioButton btnFromTime;
    private Dimension dimension=new Dimension(200,100);
    private final String FORMAT_ERROR=
            "<html><font color='red'>Invalid input.<br>" +
            "Please enter a valid number..</font></html>";

    private final String RANGE_ERROR=
            "<html><font color='red'>Please enter a proper number<br>" +   
            "according to symulation time.</font></html>";

    public ListenerFromTheStart(Manager manager){
        
        isErrorFound=false;
        this.manager=manager;
//        this.numberOfQueues=numberOfQueues;

        btnFromStart=new JRadioButton("From the start");
        btnFromTime=new JRadioButton("From the selected time");
        ActionListener lrb=new RadioListenerFromTime(panel);
        ActionListener lbegin=new RadioListenerFromStart();
        btnFromTime.addActionListener(lrb);
        btnFromStart.addActionListener(lbegin);   
    }

    @Override
    public void actionPerformed (ActionEvent e){

//        System.out.println(e.getSource());
        createPanel();
        
        if (isErrorFound==true && !labelError.isVisible() ){
//            System.out.println("dodalem");
            labelError.setVisible(true);
            panel.add(labelError);
        }

        boolean wasRunning=manager.isRunning();
        if (wasRunning){
        	manager.pause();
        }
        
        int optionChoosed=manager.displayWindowWithPanel(panel, Simulation.TITLE_FROM_BEGINNING);       

        if (optionChoosed==JOptionPane.NO_OPTION){
//            System.out.println("cancel");
            setIsErrorTo(false);
            if (wasRunning){
            	manager.resume(false);
            }
            
            return;
        }

        if (optionChoosed==JOptionPane.YES_OPTION ){

            double d=0;
            if (btnFromTime.isSelected()){
                Component [] cc= panel.getComponents();

                JTextField tf = new JTextField();
                for (int i=0; i<cc.length;i++){
                    if (cc[i] instanceof JTextField){
                        tf=(JTextField)cc[i];
                        break;
                    }
                }
                
                boolean succes=ExpressionAnalyzer.analyze(tf.getText());

                if (succes==true){
                    d=Double.parseDouble(tf.getText());
                }
                else{
                    setErrorTextTo(FORMAT_ERROR);
                    setIsErrorTo(true);
                    actionPerformed(e);
	                    if (wasRunning){
	                    	manager.resume(false);
	                    }
                    return;
                }

                if (!manager.isTimeWithinSimulationRange(d)){
                    setErrorTextTo(RANGE_ERROR);
                    setIsErrorTo(true);
                    actionPerformed(e);
	                    if (wasRunning){
	                    	manager.resume(false);
	                    }
                    return;
                }
                
            }

            if (manager.isTimeTableNotEmpty()){

                manager.pause();
//                manager.restartSimulation();
//                manager.simulation=s;
//                manager.initiate(manager.getNumberOfQueues());
                
                try{
                    if (btnFromStart.isSelected()){
                        manager.restart(0.0);
                    }
                    else{
                        manager.restart(d);
                    }
                }
                catch (InterruptedException ex){
                    ex.printStackTrace();
                }

            }
            
            setIsErrorTo(false);
            if (labelError!=null){
                labelError.setVisible(false);
            }

            
        }

                
    }

    private void createPanel (){
        if (isErrorFound==false){
        	
            panel=new JPanel();
            panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));           

            buttonGroup=new ButtonGroup();
            buttonGroup.add(btnFromStart);
            buttonGroup.add(btnFromTime);
            buttonGroup.setSelected(btnFromStart.getModel(),true);

            panel.add(btnFromStart);
            panel.add(btnFromTime);
            panel.setPreferredSize(dimension);
            labelError=new JLabel();
            labelError.setVisible(false);
        }

    }

    private void setIsErrorTo(boolean wartosc){
        if (isErrorFound!=wartosc){
            isErrorFound=wartosc;
        }
    }

    private void setErrorTextTo (String tekst){
        labelError.setText(tekst);
    }

}
