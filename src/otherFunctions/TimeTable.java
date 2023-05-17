

package otherFunctions;


public class TimeTable {

     public double [][] arrivals;
     public double [][] departures;

     public TimeTable(double [][] arrivals, double [][] departures ){
         this.arrivals=arrivals;
         this.departures=departures;
     }
     
     public TimeTable(){
    	 arrivals=new double [0][0];
    	 departures = new double [0][0];
     }

}
