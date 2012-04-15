package filmeUtils;

import java.io.File;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import filmeUtils.subtitleSites.LegendasTv;

public class Main {

	public static void main(final String[] args) throws ClientProtocolException, IOException{
		
		turnJunrarLoggingOff();
		
    	final MainCLI cli = new MainCLI();
    	cli.parse(args);
    	if(cli.isDone()){
    		return;
    	}
    	
        final FilmeUtilsHttpClient httpclient = new FilmeUtilsHttpClient();
        
        
        final LegendasTv legendasTv = new LegendasTv(httpclient);
        System.out.println("Autenticando...");
        legendasTv.login();
        
        final File subtitlesDestinationFolder = cli.getSubtitlesDestinationFolder();
        final boolean showCompressedContents = cli.showCompressedContents();
        final SearchListener searchListener = new SearchListenerImplementation(httpclient, showCompressedContents, subtitlesDestinationFolder);
        
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
