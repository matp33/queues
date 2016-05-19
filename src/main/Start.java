package main;

import java.io.IOException;

import tests.RegularTests;

public class Start {

	public static void main(String[] args) throws InterruptedException, IOException, Exception{ 
	      		
    	
    	
        if (args.length > 0) {
            try {
            try{
//            	RegularTests.test1ClientPerQueue(6);
            	RegularTests.test(7, 24);
//            	new Painter(1,10.0,new WindowFrame());

            }
            catch (Exception ex){
                ex.printStackTrace();
            }             

            } catch (NumberFormatException e) {
//            System.err.println("Liczba kas powinna byc calkowita. "+e);
            //System.exit(1);
}
}

        if (args.length>1 || args.length==0){
            System.out.println("Incorrect argument number. You should input only 1 argument.");
            //System.exit(1);
        }


}
	
}
