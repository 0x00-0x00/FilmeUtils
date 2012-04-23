package filmeUtils;

import java.io.File;
import java.io.IOException;

import filmeUtils.http.SimpleHttpClient;
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
	private final File filmeUtilsFolder;

	private File cookieFile;

	private SimpleHttpClient httpclient;

	private LegendasTv legendasTv;

	private File subtitlesDestinationFolder;

	public CommandLineClient(final ArgumentsParser cli, final File filmeUtilsFolder) {
		this.cli = cli;
		this.filmeUtilsFolder = filmeUtilsFolder;
	}

	public void execute() throws IOException {
		cookieFile = new File(filmeUtilsFolder,"legendasCookies.serialized");
		httpclient = new SimpleHttpClientImpl(cookieFile);
		legendasTv = new LegendasTv(httpclient, output);
		
		subtitlesDestinationFolder = cli.getSubtitlesDestinationFolderOrNull();
    	final boolean showDirectLink = cli.showDirectLinks(); 
    	final boolean showSubtitleIfMagnetWasNotFound = cli.showSubtitleIfMagnetWasNotFound();
    	
        final SearchListener searchListener = new SearchListenerImplementation(httpclient, showDirectLink,showSubtitleIfMagnetWasNotFound, subtitlesDestinationFolder);
        
        loginIfNeeded();
        
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

	private void loginIfNeeded() {
		if(subtitlesDestinationFolder == null)return;
		if(cookieFile.exists() && !cli.forceLogin()) return;
		try {
			legendasTv.login(cli.getUser(),cli.getPassword());
		} catch (final BadLoginException e) {
			output.out(e.getMessage());
			throw new RuntimeException(e);
		}
	}

}
