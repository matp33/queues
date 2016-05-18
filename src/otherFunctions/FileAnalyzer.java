

package otherFunctions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileAnalyzer {

    public static TimeTable analyze(File file) throws IOException, NumberFormatException{
        
        Scanner s=new Scanner(file);

        ArrayList <double[]> timeTable= new ArrayList <double[]> ();
        if (!s.hasNextLine()){
        	s.close();
            throw new NumberFormatException ("Choosen file is not a text file or is empty..");
        }
        

        while (s.hasNextLine()){

            String nextLine=s.nextLine();
            String word="";
            double words[]=new double[3];
            int i=0;

            for (int j=0; j<words.length; j++){

            word="";

                while (i<nextLine.length() && nextLine.charAt(i)!=',' ){
                    word=word.concat(""+nextLine.charAt(i));
                    i++;
                }

            i++; // pomijamy przecinek               
            words[j]=Double.parseDouble(word);           
                
            }

            timeTable.add(words);

//            System.out.println("p; "+wynik[0]+" k; "+wynik[1]+" o; "+wynik[2]);
        }

        int amount=timeTable.size();
        double [][] arrivals = new double [amount][2];
        double [][] departures = new double [amount][2];

        for (int i=0; i<amount;i++){            
            double [] words =  timeTable.get(i);
            arrivals[i][0] =words[0];
            arrivals[i][1]=words[1];
            departures[i][0]=words[2];
            departures[i][1]=words[1];
        }

        s.close();
        return new TimeTable (arrivals,departures);
        
        
    }

//    public static TimeTable extract(File file, int queueNumber)
//                                throws IOException, NumberFormatException{
    	
//    	TimeTable timeTable=analyze(file);
    	
    	
//        Scanner s=new Scanner(file);
//
//        ArrayList <double[]> timeTable= new ArrayList <double[]> ();
//        if (!s.hasNextLine()){
//        	s.close();
//            throw new NumberFormatException ("Chosen file is not a text file or is empty.");
//        }
//
//        int numberOfQueues=0;
//        while (s.hasNextLine()){
//
//            String nextLine=s.nextLine();
//            String word="";
//            double words[]=new double[3];
//            int i=0;
//
//            for (int j=0; j<words.length; j++){
//
//            word="";
//
//                while (i<nextLine.length() && nextLine.charAt(i)!=',' ){
//                    word=word.concat(""+nextLine.charAt(i));
//                    i++;
//                }
//
//            
//
//            i++; // we skip comma
//            words[j]=Double.parseDouble(word);
//
//            }
//            if (words[1]>numberOfQueues){
//                numberOfQueues=(int)words[1];
//            }
//            if (words[1]!=queueNumber){
//                continue;
//            }
//            
//            timeTable.add(words);
//
//            System.out.println("p; "+words[0]+" k; "+words[1]+" o; "+words[2]);
//        }
//        double [] d={timeTable.get(timeTable.size()-1)[0],numberOfQueues,timeTable.get(timeTable.size()-1)[2]};
//        timeTable.add(d);
//        
//        int amount=timeTable.size();
//        double [][] arrivals = new double [amount][2];
//        double [][] departures = new double [amount][2];
//
//        for (int i=0; i<amount;i++){
//            double [] table =  timeTable.get(i);
//            arrivals[i][0] =table[0];
//            arrivals[i][1]=table[1];
//            departures[i][0]=table[2];
//            departures[i][1]=table[1];
//        }
//
//        s.close();
//        return new TimeTable (arrivals,departures);
//    }

}
