package otherFunctions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class AppLogger {

    private PrintWriter printWriter;
    private File logsFile =new File("./src/logs/log.txt");

    private static final String lOG_HEADER="Queue no.\ttime predicted\tarrival time";

    public AppLogger() {
        try{
            if (!logsFile.getParentFile().exists()){
                logsFile.getParentFile().mkdirs();
            }
            printWriter=new PrintWriter(logsFile,"UTF-8");
            printWriter.println(lOG_HEADER);
        }
        catch (FileNotFoundException | UnsupportedEncodingException fg){
            fg.printStackTrace();
        }
    }

    public void saveEvent (int queueNumber, double timePredicted, double arrivalTime){
        printWriter.println(queueNumber+"\t"+timePredicted+"\t"+arrivalTime+"\t");
    }

    public void stopWritingLogs(){
        printWriter.close();
    }



}
