package filmeUtils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import filmeUtils.extraction.ExtractorImpl;
import filmeUtils.http.SimpleHttpClient;
import filmeUtils.http.SimpleHttpClientImpl;
import filmeUtils.subtitleSites.LegendasTv;
import filmeUtils.swing.SearchScreen;


public class Main {

	public static void main(final String[] args) throws IOException{
		turnJunrarLoggingOff();
		
		final File filmeUtilsFolder = new File(System.getProperty("user.home"),".filmeUtils");
		if(!filmeUtilsFolder.exists()){
			filmeUtilsFolder.mkdir();
		}
		
    	final ArgumentsParserImpl cli = new ArgumentsParserImpl(new OutputListener() {
			
			public void outVerbose(final String string) {
				System.out.println(string);
			}
			
			public void out(final String string) {
				System.out.println(string);
			}

			public void printHelp(final String applicationName, final Options options) {
				final HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(applicationName, options );
			}
		});
    	cli.parse(args);
    	if(cli.isDone()){
    		return;
    	}
    	
    	if(cli.usingGuiMome()){
    		SearchScreen.main(args);
    	}else{    		
    		final File cookieFile = new File(filmeUtilsFolder,"cookies.serialized");
    		final SimpleHttpClient httpclient = new SimpleHttpClientImpl(cookieFile);
    		
    		final ExtractorImpl extract = new ExtractorImpl();
    		final SysOut output = new SysOut(cli);
    		final LegendasTv legendasTv = new LegendasTv(cli,httpclient, output);
			
			final CommandLineClient commandLineClient = new CommandLineClient(httpclient,legendasTv,extract,cli, output);
    		commandLineClient.execute();        
    	}
    	
    }

	private static void turnJunrarLoggingOff() {
		System.setProperty("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog");
	}
}
