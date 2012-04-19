package filmeUtils;

import java.io.File;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import filmeUtils.http.SimpleHttpClientImpl;
import filmeUtils.subtitleSites.BadLoginException;
import filmeUtils.subtitleSites.LegendasTv;

public class Main {

	public static void main(final String[] args) throws ClientProtocolException, IOException, BadLoginException{
		turnJunrarLoggingOff();
		
    	final MainCLI cli = new MainCLI();
    	cli.parse(args);
    	if(cli.isDone()){
    		return;
    	}
    	
    	final File subtitlesDestinationFolder = cli.getSubtitlesDestinationFolderOrNull();
    	final boolean showDirectLink = cli.showDirectLinks(); 
    	final boolean showSubtitleIfMagnetWasNotFound = cli.showSubtitleIfMagnetWasNotFound();
    	
        final SimpleHttpClientImpl httpclient = new SimpleHttpClientImpl();
        final SearchListener searchListener = new SearchListenerImplementation(httpclient, showDirectLink,showSubtitleIfMagnetWasNotFound, subtitlesDestinationFolder);
        
        
        final LegendasTv legendasTv = new LegendasTv(httpclient, new OutputListener() {
			public void out(final String string) {
				System.out.println(string);
			}
		});
        System.out.println("Autenticando...");
        legendasTv.login(cli.getUser(),cli.getPassword());
        
        if(cli.search()){
        	final String searchTerm = cli.searchTerm();
			System.out.println("Procurando '"+searchTerm+"' ...");
        	
        	legendasTv.search(searchTerm,searchListener);
        }
        
        if(cli.showNewAdditions()){
        	System.out.println("Novas legendas:");
        	final int newAdditionsPageCountToShow = cli.newAdditionsPageCountToShow();
        	legendasTv.getNewer(newAdditionsPageCountToShow,searchListener);
        	
        }
        httpclient.close();        
    }

	private static void turnJunrarLoggingOff() {
		System.setProperty("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog");
	}
}
