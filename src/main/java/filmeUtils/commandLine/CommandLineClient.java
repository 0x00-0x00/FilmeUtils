package filmeUtils.commandLine;

import filmeUtils.commons.OutputListener;
import filmeUtils.subtitle.subtitleSites.LegendasTv;
import filmeUtils.subtitle.subtitleSites.SubtitleAndLink;
import filmeUtils.subtitle.subtitleSites.SubtitleLinkSearchCallback;
import filmeUtils.utils.extraction.Extractor;
import filmeUtils.utils.http.SimpleHttpClient;

public class CommandLineClient {
	
	private final SimpleHttpClient httpclient;
	private final LegendasTv legendasTv;
	private final OutputListener output;
	private final SubtitleLinkSearchCallback searchListener;

	public CommandLineClient(
			final SimpleHttpClient httpclient,
			final LegendasTv legendasTv,
			final Extractor extract,
			final OutputListener output,
			final SubtitleLinkSearchCallback searchListener) {
		this.httpclient = httpclient;
		this.legendasTv = legendasTv;
		this.output = output;
		this.searchListener = searchListener;
	}

	public void search(final String searchTerm){		
		output.outVerbose("Procurando '"+searchTerm+"' ...");
		legendasTv.search(searchTerm,searchListener);
		httpclient.close();
	}
	
	public void showNewAdditions(){		
		output.outVerbose("Novas legendas:");
		legendasTv.getNewer(new SubtitleLinkSearchCallback() {@Override public void process(SubtitleAndLink nameAndlink) {
			output.out(nameAndlink.name);
		}});
		httpclient.close();
	}

}
