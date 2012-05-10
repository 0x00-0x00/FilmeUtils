package filmeUtils;

import java.io.IOException;

import filmeUtils.extraction.Extractor;
import filmeUtils.http.SimpleHttpClient;
import filmeUtils.subtitleSites.LegendasTv;

public class CommandLineClient {
	
	private final ArgumentsParserImpl cli;

	private final SimpleHttpClient httpclient;

	private final LegendasTv legendasTv;

	private final OutputListener output;

	private final Downloader downloader;

	public CommandLineClient(final Downloader downloader,final SimpleHttpClient httpclient,final LegendasTv legendasTv,final Extractor extract,final ArgumentsParserImpl cli, final OutputListener output) {
		this.downloader = downloader;
		this.httpclient = httpclient;
		this.legendasTv = legendasTv;
		this.cli = cli;
		this.output = output;
	}

	public void execute() throws IOException {
		final SearchListener searchListener = new SearchListenerImplementation(downloader,cli,output);
        
		final boolean search = cli.search();
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
