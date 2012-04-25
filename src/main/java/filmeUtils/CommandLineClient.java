package filmeUtils;

import java.io.File;
import java.io.IOException;

import filmeUtils.extraction.ExtractorImpl;
import filmeUtils.http.BareBonesBrowseLauncher;
import filmeUtils.http.BrowserLauncher;
import filmeUtils.http.SimpleHttpClient;
import filmeUtils.http.SimpleHttpClientImpl;
import filmeUtils.subtitleSites.LegendasTv;
import filmeUtils.torrentSites.TorrentSearcher;
import filmeUtils.torrentSites.TorrentSearcherImpl;

public class CommandLineClient {

	protected boolean verbose = false;

	private final OutputListener output = new OutputListener() {	
		public void out(final String string) {
			System.out.println(string);
		}

		public void outVerbose(final String string) {
			if(verbose){
				System.out.println(string);
			}
		}
	};
	
	private final ArgumentsParserImpl cli;
	private final File filmeUtilsFolder;

	private File cookieFile;

	private SimpleHttpClient httpclient;

	private LegendasTv legendasTv;


	private boolean search;

	public CommandLineClient(final ArgumentsParserImpl cli, final File filmeUtilsFolder) {
		this.cli = cli;
		this.filmeUtilsFolder = filmeUtilsFolder;
	}

	public void execute() throws IOException {
		cookieFile = new File(filmeUtilsFolder,"legendasCookies.serialized");
		httpclient = new SimpleHttpClientImpl(cookieFile);
		legendasTv = new LegendasTv(cli.getUser(),cli.getPassword(),httpclient, output);
		search = cli.search();
		
    	verbose = cli.isVerbose();
        final BrowserLauncher bareBonesBrowserLaunch = new BareBonesBrowseLauncher();
        final TorrentSearcher torrentSearcher = new TorrentSearcherImpl(httpclient);
        final ExtractorImpl extract = new ExtractorImpl();
		final SearchListener searchListener = new SearchListenerImplementation(httpclient,extract,torrentSearcher,bareBonesBrowserLaunch,legendasTv ,cli,output);
        
		if(search){
        	final String searchTerm = cli.searchTerm();
        	output.outVerbose("Procurando '"+searchTerm+"' ...");
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
