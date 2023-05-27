import symulation.ApplicationConfiguration;
import tests.RegularTests;

import java.io.IOException;

public class Start {

	public static void main(String[] args) throws IOException {
	      		
    	
    	
        if (args.length == 0) {

			ApplicationConfiguration instance = ApplicationConfiguration.getInstance();
			int numberOfQueues = 3;
			instance.setNumberOfQueues(numberOfQueues);
			instance.initialize();
//            	RegularTests.test1ClientPerQueue(8);
            	RegularTests.testMultipleClientsWithMultipleQueues(numberOfQueues,8);
//            	new Painter(1,10.0,new WindowFrame());

//                Painter painter = Painter.getInstance();
//                Manager manager = new Manager(painter);
//                painter.setManager(manager);
//                StoreCheckout storeCheckout = new StoreCheckout(painter, 3);
//
//                Client client = new Client(storeCheckout, 0, painter, 5, manager);
//                client.setPosition(new Point(5, 300));
//                client.moveToQueue();
//                manager.initializeStaticObjects();




          
}

//        if (args.length>1 || args.length==0){
//            System.out.println("Incorrect argument number. You should input only 1 argument.");
//            //System.exit(1);
//        }


}
	
}
