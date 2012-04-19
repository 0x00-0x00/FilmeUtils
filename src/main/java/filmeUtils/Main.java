package filmeUtils;

import filmeUtils.swing.SearchScreen;


public class Main {

	public static void main(final String[] args){
		turnJunrarLoggingOff();
		
    	final ArgumentsParser cli = new ArgumentsParser();
    	cli.parse(args);
    	if(cli.isDone()){
    		return;
    	}
    	
    	if(cli.usingGuiMome()){
    		SearchScreen.main(args);
    	}else{    		
    		final CommandLineClient commandLineClient = new CommandLineClient(cli);
    		commandLineClient.execute();        
    	}
    	
    }

	private static void turnJunrarLoggingOff() {
		System.setProperty("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog");
	}
}
