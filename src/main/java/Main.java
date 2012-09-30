

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import filmeUtils.ArgumentsParserImpl;
import filmeUtils.CommandLineClient;
import filmeUtils.Downloader;
import filmeUtils.FilmeUtilsConstants;
import filmeUtils.OutputListener;
import filmeUtils.VerboseSysOut;
import filmeUtils.extraction.ExtractorImpl;
import filmeUtils.fileSystem.FileSystem;
import filmeUtils.fileSystem.FileSystemImpl;
import filmeUtils.http.MagnetLinkHandler;
import filmeUtils.http.OSMagnetLinkHandler;
import filmeUtils.http.SimpleHttpClient;
import filmeUtils.http.SimpleHttpClientImpl;
import filmeUtils.subtitleSites.LegendasTv;
import filmeUtils.swing.SearchScreen;
import filmeUtils.swing.SearchScreenNeeds;
import filmeUtils.torrentSites.TorrentSearcher;
import filmeUtils.torrentSites.TorrentSearcherImpl;


public class Main {

	public static void main(final String[] args) throws IOException{		

		final ArgumentsParserImpl cli = parseArgs(args);
    	boolean usingGui = cli.usingGui();
    	runFilmeUtils(cli, usingGui);
    	
    }

	static ArgumentsParserImpl parseArgs(final String[] args) {
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
		return cli;
	}

	static void runFilmeUtils(final ArgumentsParserImpl cli,boolean usingGui) throws IOException {
		final File cookieFile = new File(FilmeUtilsConstants.filmeUtilsFolder(),"cookies.serialized");
    	final SimpleHttpClient httpclient = new SimpleHttpClientImpl(cookieFile);
    	final ExtractorImpl extract = new ExtractorImpl();
    	final VerboseSysOut output = new VerboseSysOut();
    	final LegendasTv legendasTv = new LegendasTv(httpclient, output);
    	legendasTv.stopOnFirstMatch(cli.isLazy());
    	
    	final MagnetLinkHandler magnetLinkHandler = new OSMagnetLinkHandler();
        final TorrentSearcher torrentSearcher = new TorrentSearcherImpl(httpclient);
		final FileSystem fileSystem = new FileSystemImpl();
    	
		if(usingGui){
    		final Downloader downloader = new Downloader(extract, fileSystem, httpclient, torrentSearcher, magnetLinkHandler, legendasTv, output);
    		final SearchScreenNeeds searchScreenNeeds = new SearchScreenNeeds(legendasTv, downloader);
    		new SearchScreen(searchScreenNeeds);
    	}else{
    		final Downloader downloader = new Downloader(extract, fileSystem, httpclient, torrentSearcher, magnetLinkHandler, legendasTv, output);
			final CommandLineClient commandLineClient = new CommandLineClient(downloader,httpclient,legendasTv,extract,cli, output);
    		commandLineClient.execute();        
    	}
	}
}
