package filmeUtils;

import java.io.File;

import filmeUtils.http.SimpleHttpClientImpl;
import filmeUtils.subtitleSites.BadLoginException;
import filmeUtils.subtitleSites.LegendasTv;

public class CommandLineClient {

	private final OutputListener output = new OutputListener() {
		
		public void out(final String string) {
			System.out.println(string);
		}
	};
	
	private final ArgumentsParser cli;

	public CommandLineClient(final ArgumentsParser cli) {
		this.cli = cli;
	}

	public void execute() {
		final File subtitlesDestinationFolder = cli.getSubtitlesDestinationFolderOrNull();
    	final boolean showDirectLink = cli.showDirectLinks(); 
    	final boolean showSubtitleIfMagnetWasNotFound = cli.showSubtitleIfMagnetWasNotFound();
    	
        final SimpleHttpClientImpl httpclient = new SimpleHttpClientImpl();
        final SearchListener searchListener = new SearchListenerImplementation(httpclient, showDirectLink,showSubtitleIfMagnetWasNotFound, subtitlesDestinationFolder);
        
        
        final LegendasTv legendasTv = new LegendasTv(httpclient, output);
        output.out("Autenticando...");
        try {
			legendasTv.login(cli.getUser(),cli.getPassword());
		} catch (final BadLoginException e) {
			output.out(e.getMessage());
			throw new RuntimeException(e);
		}
        
        if(cli.search()){
        	final String searchTerm = cli.searchTerm();
        	output.out("Procurando '"+searchTerm+"' ...");
        	legendasTv.search(searchTerm,searchListener);
        }
        
        if(cli.showNewAdditions()){
        	output.out("Novas legendas:");
        	final int newAdditionsPageCountToShow = cli.newAdditionsPageCountToShow();
        	legendasTv.getNewer(newAdditionsPageCountToShow,searchListener);
        	
        }
        httpclient.close();
	}

}
