package main;

import tests.RegularTests;

public class Start {

	public static void main(String[] args) throws InterruptedException { 
	      		
    	
    	
        if (args.length == 0) {
            
            try{
//            	RegularTests.test1ClientPerQueue(8);
            	RegularTests.testMultipleClientsWithMultipleQueues(6,12);
//            	new Painter(1,10.0,new WindowFrame());

            }
            catch (Exception ex){
                ex.printStackTrace();
            }             

          
}

//        if (args.length>1 || args.length==0){
//            System.out.println("Incorrect argument number. You should input only 1 argument.");
//            //System.exit(1);
//        }


}
	
}
