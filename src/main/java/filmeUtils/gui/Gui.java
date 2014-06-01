package filmeUtils.gui;
import java.io.File;

import filmeUtils.commons.FileSystemUtils;
import filmeUtils.commons.VerboseSysOut;
import filmeUtils.downloader.Downloader;
import filmeUtils.gui.gui.SearchScreen;
import filmeUtils.gui.gui.SearchScreenNeeds;
import filmeUtils.subtitle.Subtitle;
import filmeUtils.subtitle.subtitleSites.LegendasTv;
import filmeUtils.utils.extraction.ExtractorImpl;
import filmeUtils.utils.http.SimpleHttpClient;
import filmeUtils.utils.http.SimpleHttpClientImpl;


public class Gui {

	public void open(){
		final ExtractorImpl extract = new ExtractorImpl();
		final File cookieFile = FileSystemUtils.getInstance().getCookiesFile();
    	final SimpleHttpClient httpclient = new SimpleHttpClientImpl(cookieFile);
    	final VerboseSysOut output = new VerboseSysOut();
    	final LegendasTv legendasTv = new LegendasTv( output);
		final Downloader downloader = new Downloader(extract, httpclient,  legendasTv, output);
		final Subtitle subtitle = new Subtitle(output, httpclient, legendasTv);
		final SearchScreenNeeds searchScreenNeeds = new SearchScreenNeeds(legendasTv, downloader, subtitle);
		new SearchScreen(searchScreenNeeds);
	}
	
}
