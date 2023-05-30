import spring2.Bean;
import spring2.BeanRegistry;
import spring2.BeanScanner;
import symulation.ApplicationConfiguration;
import symulation.ApplicationInitializer;
import tests.RegularTests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@Bean
public class Start {

	private final ApplicationInitializer applicationInitializer;
	private final ApplicationConfiguration applicationConfiguration;

	private final  RegularTests regularTests;

	public Start(ApplicationInitializer applicationInitializer, ApplicationConfiguration applicationConfiguration, RegularTests regularTests) throws IOException {
		this.applicationInitializer = applicationInitializer;
		this.applicationConfiguration = applicationConfiguration;
		this.regularTests = regularTests;
		start();
	}

	private void start () throws IOException {
		int numberOfQueues = 4;
		applicationConfiguration.setNumberOfQueues(numberOfQueues);
		applicationInitializer.initialize();
		regularTests.testMultipleClientsWithMultipleQueues(numberOfQueues,8);
	}

	public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
	      		
    	new BeanScanner().run();



	}
	
}
