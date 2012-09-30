import java.io.File;
import java.io.IOException;

import filmeUtils.Downloader;
import filmeUtils.FilmeUtilsFolder;
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


public class Gui {

	public static void main(String[] args) throws IOException {
		final ExtractorImpl extract = new ExtractorImpl();
		final FileSystem fileSystem = new FileSystemImpl();
		final File cookieFile = new File(FilmeUtilsFolder.get(),"cookies.serialized");
    	final SimpleHttpClient httpclient = new SimpleHttpClientImpl(cookieFile);
    	final TorrentSearcher torrentSearcher = new TorrentSearcherImpl(httpclient);
    	final MagnetLinkHandler magnetLinkHandler = new OSMagnetLinkHandler();
    	final VerboseSysOut output = new VerboseSysOut();
    	final LegendasTv legendasTv = new LegendasTv(httpclient, output);
		final Downloader downloader = new Downloader(extract, fileSystem, httpclient, torrentSearcher, magnetLinkHandler, legendasTv, output);
		final SearchScreenNeeds searchScreenNeeds = new SearchScreenNeeds(legendasTv, downloader);
		new SearchScreen(searchScreenNeeds);
	}
	
}
