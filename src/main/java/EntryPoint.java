import spring2.Bean;
import spring2.BeanScanner;
import symulation.ApplicationConfiguration;
import symulation.ApplicationInitializer;
import tests.RegularTests;
import view.ApplicationWindow;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@Bean
public class EntryPoint {

	private final ApplicationInitializer applicationInitializer;
	private final ApplicationConfiguration applicationConfiguration;

	private final ApplicationWindow applicationWindow;

	private final  RegularTests regularTests;

	public EntryPoint(ApplicationInitializer applicationInitializer, ApplicationConfiguration applicationConfiguration, ApplicationWindow applicationWindow, RegularTests regularTests) throws IOException {
		this.applicationInitializer = applicationInitializer;
		this.applicationConfiguration = applicationConfiguration;
		this.applicationWindow = applicationWindow;
		this.regularTests = regularTests;
		runSimulation();
	}

	private void runSimulation() {
		int numberOfQueues = 4;
		applicationConfiguration.setNumberOfQueues(numberOfQueues);
		applicationWindow.initializeMainPanel();
		applicationInitializer.initialize();
		applicationWindow.initializeWindow();
		regularTests.testMultipleClientsWithMultipleQueues(numberOfQueues,8);
	}

	public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
	      		
    	new BeanScanner().run();



	}
	
}
