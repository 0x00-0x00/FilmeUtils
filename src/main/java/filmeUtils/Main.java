package filmeUtils;

import java.io.File;
import java.io.IOException;

import filmeUtils.swing.SearchScreen;


public class Main {

	public static void main(final String[] args) throws IOException{
		turnJunrarLoggingOff();
		
		final File filmeUtilsFolder = new File(System.getProperty("user.home"),".filmeUtils");
		if(!filmeUtilsFolder.exists()){
			filmeUtilsFolder.mkdir();
		}
		
    	final ArgumentsParser cli = new ArgumentsParser();
    	cli.parse(args);
    	if(cli.isDone()){
    		return;
    	}
    	
    	if(cli.usingGuiMome()){
    		SearchScreen.main(args);
    	}else{    		
    		final CommandLineClient commandLineClient = new CommandLineClient(cli, filmeUtilsFolder);
    		commandLineClient.execute();        
    	}
    	
    }

	private static void turnJunrarLoggingOff() {
		System.setProperty("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog");
	}
}
