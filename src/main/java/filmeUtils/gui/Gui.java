package filmeUtils.gui;

import filmeUtils.commons.VerboseSysOut;
import filmeUtils.downloader.Downloader;
import filmeUtils.gui.gui.SearchScreen;
import filmeUtils.gui.gui.SearchScreenNeeds;
import filmeUtils.subtitle.Subtitle;
import filmeUtils.subtitle.subtitleSites.legendasTV.LegendasTv;
import filmeUtils.utils.extraction.ExtractorImpl;

public class Gui {

	public void open(){
		final ExtractorImpl extract = new ExtractorImpl();
    	final VerboseSysOut output = new VerboseSysOut();
    	final LegendasTv legendasTv = new LegendasTv( output);
		final Downloader downloader = new Downloader(extract,  legendasTv, output);
		final Subtitle subtitle = new Subtitle(output, legendasTv);
		final SearchScreenNeeds searchScreenNeeds = new SearchScreenNeeds(legendasTv, downloader, subtitle);
		new SearchScreen(searchScreenNeeds);
	}
	
}
