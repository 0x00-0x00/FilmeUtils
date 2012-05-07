package filmeUtils;

import java.io.IOException;

import filmeUtils.extraction.Extractor;
import filmeUtils.fileSystem.FileSystem;
import filmeUtils.fileSystem.FileSystemImpl;
import filmeUtils.http.MagnetLinkHandler;
import filmeUtils.http.OSMagnetLinkHandler;
import filmeUtils.http.SimpleHttpClient;
import filmeUtils.subtitleSites.LegendasTv;
import filmeUtils.torrentSites.TorrentSearcher;
import filmeUtils.torrentSites.TorrentSearcherImpl;

public class CommandLineClient {
	
	private final ArgumentsParserImpl cli;

	private final SimpleHttpClient httpclient;

	private final LegendasTv legendasTv;
	private final Extractor extract;

	private final OutputListener output;

	public CommandLineClient(final SimpleHttpClient httpclient,final LegendasTv legendasTv,final Extractor extract,final ArgumentsParserImpl cli, final OutputListener output) {
		this.httpclient = httpclient;
		this.legendasTv = legendasTv;
		this.extract = extract;
		this.cli = cli;
		this.output = output;
	}

	public void execute() throws IOException {
		
		final boolean search = cli.search();
		
        final MagnetLinkHandler urlHandler = new OSMagnetLinkHandler();
        final TorrentSearcher torrentSearcher = new TorrentSearcherImpl(httpclient);
        
		final FileSystem fileSystem = new FileSystemImpl();
		final SearchListener searchListener = new SearchListenerImplementation(fileSystem,httpclient,extract,torrentSearcher,urlHandler,legendasTv ,cli,output);
        
		if(search){
        	final String searchTerm = cli.searchTerm();
        	output.outVerbose("Procurando '"+searchTerm+"' ...");
        	legendasTv.search(searchTerm,searchListener);
        }
        
        if(cli.showNewAdditions()){
        	output.outVerbose("Novas legendas:");
        	final int newAdditionsPageCountToShow = cli.newAdditionsPageCountToShow();
        	legendasTv.getNewer(newAdditionsPageCountToShow,searchListener);
        	
        }
        httpclient.close();
	}

}
