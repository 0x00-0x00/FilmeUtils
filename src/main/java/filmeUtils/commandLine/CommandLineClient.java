package filmeUtils.commandLine;

import java.io.File;

import filmeUtils.commons.OutputListener;
import filmeUtils.subtitle.Subtitle;
import filmeUtils.subtitle.subtitleSites.LegendasTv;
import filmeUtils.subtitle.subtitleSites.SubtitleAndLink;
import filmeUtils.subtitle.subtitleSites.SubtitleLinkSearchCallback;
import filmeUtils.utils.extraction.Extractor;
import filmeUtils.utils.http.SimpleHttpClient;

public class CommandLineClient implements CommandLine {
	
	private final SimpleHttpClient httpclient;
	private final LegendasTv legendasTv;
	private final OutputListener output;

	public CommandLineClient(
			final SimpleHttpClient httpclient,
			final LegendasTv legendasTv,
			final Extractor extract,
			final OutputListener output) {
		this.httpclient = httpclient;
		this.legendasTv = legendasTv;
		this.output = output;
	}

	public void search(final String searchTerm){		
//		output.outVerbose("Procurando '"+searchTerm+"' ...");
//		legendasTv.search(searchTerm,searchListener);
//		httpclient.close();
	}
	
	public void showNewAdditions(){		
		output.outVerbose("Novas legendas:");
		legendasTv.getNewer(new SubtitleLinkSearchCallback() {@Override public void process(SubtitleAndLink nameAndlink) {
			output.out(nameAndlink.name);
		}});
		httpclient.close();
	}

	@Override
	public void h() {
		throw new RuntimeException("NOT IMPLEMENTED");
	}

	@Override
	public void lt(String subtitleSearchTerm, File destinantion) {
		throw new RuntimeException("NOT IMPLEMENTED");
	}

	@Override
	public void lt(String subtitleSearchTerm,
			String regexToApplyOnSubtitlesFiles, File destinantion) {
		throw new RuntimeException("NOT IMPLEMENTED");
	}

	@Override
	public void l(String subtitleSearchTerm, File destinantion) {
		throw new RuntimeException("NOT IMPLEMENTED");
	}

	@Override
	public void l(String subtitleSearchTerm,
			String regexToApplyOnSubtitlesFiles, File destinantion) {
		throw new RuntimeException("NOT IMPLEMENTED");
	}

	@Override
	public void t(String torrentSearchTerm) {
		throw new RuntimeException("NOT IMPLEMENTED");
	}

	@Override
	public void n() {
		throw new RuntimeException("NOT IMPLEMENTED");
	}

	@Override
	public void n(String regexToApplyOnSubtitlesPackage,
			String regexToApplyOnSubtitlesFiles, File destinantion) {
		throw new RuntimeException("NOT IMPLEMENTED");
	}

	@Override
	public void f(File regexFile, File destinantion) {
		throw new RuntimeException("NOT IMPLEMENTED");
	}

	@Override
	public void p(String subtitleSearchTerm) {
		Subtitle subtitle = new Subtitle(output,httpclient,legendasTv);
		output.out("Procurando "+subtitleSearchTerm);
		subtitle.search(subtitleSearchTerm);
	}

}
