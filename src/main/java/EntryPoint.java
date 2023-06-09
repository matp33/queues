import navmesh.GridHandler;
import spring2.Bean;
import spring2.BeanScanner;
import simulation.ApplicationConfiguration;
import simulation.ApplicationInitializer;
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

	private final GridHandler gridHandler;

	public EntryPoint(ApplicationInitializer applicationInitializer, ApplicationConfiguration applicationConfiguration, ApplicationWindow applicationWindow, RegularTests regularTests, GridHandler gridHandler) throws IOException {
		this.applicationInitializer = applicationInitializer;
		this.applicationConfiguration = applicationConfiguration;
		this.applicationWindow = applicationWindow;
		this.regularTests = regularTests;
		this.gridHandler = gridHandler;
		runSimulation();
	}

	private void runSimulation() {
		int numberOfQueues = 4;
		applicationConfiguration.setNumberOfQueues(numberOfQueues);
		applicationWindow.initializeMainPanel();
		applicationInitializer.initialize();
		applicationWindow.initializeWindow();
		gridHandler.initialize(applicationWindow.getSimulationPanelDimension());
		regularTests.testMultipleClientsWithMultipleQueues(numberOfQueues,12);
	}

	public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
	      		
    	new BeanScanner().run();



	}
	
}
