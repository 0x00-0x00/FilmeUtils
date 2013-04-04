package filmeUtils.commandLine;

import java.io.File;

import filmeUtils.commons.OutputListener;
import filmeUtils.commons.VerboseSysOut;
import filmeUtils.downloader.Downloader;
import filmeUtils.subtitle.Subtitle;
import filmeUtils.subtitle.subtitleSites.LegendasTv;
import filmeUtils.subtitle.subtitleSites.SubtitleAndLink;
import filmeUtils.subtitle.subtitleSites.SubtitleLinkSearchCallback;
import filmeUtils.torrent.torrentSites.TorrentSearcher;
import filmeUtils.torrent.torrentSites.TorrentSearcherImpl;
import filmeUtils.utils.extraction.Extractor;
import filmeUtils.utils.extraction.ExtractorImpl;
import filmeUtils.utils.fileSystem.FileSystem;
import filmeUtils.utils.fileSystem.FileSystemImpl;
import filmeUtils.utils.http.MagnetLinkHandler;
import filmeUtils.utils.http.OSMagnetLinkHandler;
import filmeUtils.utils.http.SimpleHttpClient;
import filmeUtils.utils.http.SimpleHttpClientImpl;

public class CommandLineClient implements CommandLine {
	
	private final SimpleHttpClient httpclient;
	private final LegendasTv legendasTv;
	private final OutputListener output;
	private Extractor extractor;

	public CommandLineClient(
			final SimpleHttpClient httpclient,
			final LegendasTv legendasTv,
			final Extractor extract,
			final OutputListener output) {
		this.httpclient = httpclient;
		this.legendasTv = legendasTv;
		this.extractor = extract;
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
	public void lt(String subtitleSearchTerm, String regexToApplyOnSubtitlesFiles, File destinantion) {
    	final MagnetLinkHandler magnetLinkHandler = new OSMagnetLinkHandler();
        final TorrentSearcher torrentSearcher = new TorrentSearcherImpl(httpclient);
		final FileSystem fileSystem = new FileSystemImpl();
		LegendasTv legendasTv = new LegendasTv(httpclient, output);
		Downloader downloader = new Downloader(extractor, fileSystem, httpclient, torrentSearcher, magnetLinkHandler, legendasTv, output);
		output.out("Procurando "+subtitleSearchTerm+" aplicando regex "+regexToApplyOnSubtitlesFiles+" salvando em "+destinantion.getAbsolutePath());
		downloader.download(subtitleSearchTerm, destinantion, regexToApplyOnSubtitlesFiles);
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
