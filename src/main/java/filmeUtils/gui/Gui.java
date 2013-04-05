package filmeUtils.gui;
import java.io.File;

import filmeUtils.commons.FileSystemUtils;
import filmeUtils.commons.VerboseSysOut;
import filmeUtils.downloader.Downloader;
import filmeUtils.gui.gui.SearchScreen;
import filmeUtils.gui.gui.SearchScreenNeeds;
import filmeUtils.subtitle.subtitleSites.LegendasTv;
import filmeUtils.utils.extraction.ExtractorImpl;
import filmeUtils.utils.fileSystem.FileSystem;
import filmeUtils.utils.fileSystem.FileSystemImpl;
import filmeUtils.utils.http.SimpleHttpClient;
import filmeUtils.utils.http.SimpleHttpClientImpl;


public class Gui {

	public void open(){
		final ExtractorImpl extract = new ExtractorImpl();
		final FileSystem fileSystem = new FileSystemImpl();
		final File cookieFile = FileSystemUtils.getInstance().getCookiesFile();
    	final SimpleHttpClient httpclient = new SimpleHttpClientImpl(cookieFile);
    	final VerboseSysOut output = new VerboseSysOut();
    	final LegendasTv legendasTv = new LegendasTv(httpclient, output);
		final Downloader downloader = new Downloader(extract, fileSystem, httpclient,  legendasTv, output);
		final SearchScreenNeeds searchScreenNeeds = new SearchScreenNeeds(legendasTv, downloader);
		new SearchScreen(searchScreenNeeds);
	}
	
}
