import java.io.File;
import java.io.IOException;
import java.util.List;

import filmeUtils.commons.FilmeUtilsFolder;
import filmeUtils.commons.VerboseSysOut;
import filmeUtils.downloader.Downloader;
import filmeUtils.subtitle.SubtitleRegexUtils;
import filmeUtils.subtitle.subtitleSites.LegendasTv;
import filmeUtils.subtitle.subtitleSites.SubtitleAndLink;
import filmeUtils.subtitle.subtitleSites.SubtitleLinkSearchCallback;
import filmeUtils.torrent.torrentSites.TorrentSearcher;
import filmeUtils.torrent.torrentSites.TorrentSearcherImpl;
import filmeUtils.utils.extraction.ExtractorImpl;
import filmeUtils.utils.fileSystem.FileSystem;
import filmeUtils.utils.fileSystem.FileSystemImpl;
import filmeUtils.utils.http.MagnetLinkHandler;
import filmeUtils.utils.http.OSMagnetLinkHandler;
import filmeUtils.utils.http.SimpleHttpClient;
import filmeUtils.utils.http.SimpleHttpClientImpl;


public class Daemon {

	private static final FilmeUtilsFolder filmeUtilsFolder = FilmeUtilsFolder.getInstance();
	private static final File cookieFile = filmeUtilsFolder.getCookiesFile();
	private SimpleHttpClient httpclient;
	private VerboseSysOut output;
	private LegendasTv legendasTv;
	private Downloader downloader;

	public static void main(String[] args) throws IOException {
		new Daemon();
	}
	
	public Daemon() throws IOException {
		ensurePatternsToDownloadFileExistsOrCry();
		httpclient = new SimpleHttpClientImpl(cookieFile);
		output = new VerboseSysOut();
		logonOnSubtitleSite();
    	createDownloader();
		searchAndDownload();
	}

	private void searchAndDownload() {
		output.out("Procurando novas legendas.");
		
		SubtitleLinkSearchCallback searchCallback = new SubtitleLinkSearchCallback(){@Override public void process(SubtitleAndLink subAndLink) {
			List<String> alreadyDownloadedFiles = filmeUtilsFolder.getAlreadyDownloaded();
			List<String> subtitlesToDownloadPatterns = filmeUtilsFolder.getSubtitlesToDownloadPatterns();
			String name = subAndLink.name;
			String link = subAndLink.link;
			
			for (String pattern : subtitlesToDownloadPatterns) {
				if (name.toLowerCase().matches(SubtitleRegexUtils.getSubtitlesZipRegex(pattern)) && !alreadyDownloadedFiles.contains(name)) {
					output.out("Pattern matched: "+name);
					boolean success = downloader.download(name, link,filmeUtilsFolder.getSubtitlesDestination(), SubtitleRegexUtils.getSubtitleRegex(pattern));
					if(success){
						filmeUtilsFolder.addAlreadyDownloaded(name);
					}
				}
			}
		}};
		
		legendasTv.getNewer(searchCallback);
	}

	private void createDownloader() {
		final ExtractorImpl extractor = new ExtractorImpl();
    	final MagnetLinkHandler magnetLinkHandler = new OSMagnetLinkHandler();
        final TorrentSearcher torrentSearcher = new TorrentSearcherImpl(httpclient);
		final FileSystem fileSystem = new FileSystemImpl();
		downloader = new Downloader(extractor, fileSystem, httpclient, torrentSearcher, magnetLinkHandler, legendasTv, output);
	}

	private void logonOnSubtitleSite() {
		legendasTv = new LegendasTv(httpclient, output);
	}

	private void ensurePatternsToDownloadFileExistsOrCry() {
		String subtitlesToDownloadFile = filmeUtilsFolder.getRegexFileWithPatternsToDownloadPath();
		if(!filmeUtilsFolder.subtitlesToDownloadPatternFileExists()){
			System.err.println("O arquivo "+subtitlesToDownloadFile+" tem que existir.");
			System.err.println("Esse arquivo deve ter uma regex por linha.");
			System.err.println("Quando uma nova legenda aparecer no legendas tv, o programa vai \n" +"baixar a legenda em " +filmeUtilsFolder.getSubtitlesDestination()+" e adicionar o torrent no cliente registrado.");
			throw new RuntimeException(subtitlesToDownloadFile+" not found.");
		}
	}	
}
