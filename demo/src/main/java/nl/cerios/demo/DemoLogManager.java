package nl.cerios.demo;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class DemoLogManager {

	private static final LogManager logManager = LogManager.getLogManager();
	private static final Logger LOGGER = Logger.getLogger("confLogger");
	static {
		try {
			logManager.readConfiguration(DemoLogManager.class.getResourceAsStream("/log.properties"));
		} catch (IOException exception) {
			LOGGER.log(Level.SEVERE, "Error in loading configuration",exception);
		}
	}
	public static void initialise()
	{
		
	}
	public static void main(String[] args) {
		LOGGER.fine("Fine message logged");
	}
}