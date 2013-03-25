import java.io.File;
import java.io.IOException;
import java.util.List;

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
import filmeUtils.subtitleSites.NewSubtitleLinkFoundCallback;
import filmeUtils.subtitleSites.SubtitleAndLink;
import filmeUtils.torrentSites.TorrentSearcher;
import filmeUtils.torrentSites.TorrentSearcherImpl;


public class Daemon {

	private static final FilmeUtilsFolder filmeUtilsFolder = FilmeUtilsFolder.getInstance();
	private static final File cookieFile = filmeUtilsFolder.getCookiesFile();
	private static final int CHECK_INTERVAL_MILLIS = 60000 * 10;
	private static final int VALUES_PER_PAGE = 23;
	private static final int QNTY_OF_SUBTITLES_TO_SCAN = VALUES_PER_PAGE*3;
	private SimpleHttpClient httpclient;
	private VerboseSysOut output;
	private LegendasTv legendasTv;
	private Downloader downloader;

	public static void main(String[] args) throws IOException {
		if(args.length == 0)
			new Daemon(true);
		else
			new Daemon(!args[0].equals("onlyOnce"));
	}
	
	public Daemon(boolean continuousSearch) throws IOException {
		ensurePatternsToDownloadFileExistsOrCry();
		httpclient = new SimpleHttpClientImpl(cookieFile);
		output = new VerboseSysOut();
		
		logonOnSubtitleSite();
		
    	createDownloader();
		
		do{
			try {
				searchAndDownload();
				if(continuousSearch) Thread.sleep(CHECK_INTERVAL_MILLIS);
			} catch (Exception e) {
				FilmeUtilsFolder.getInstance().writeErrorFile(e);// ignore and go on
			}
		}while(continuousSearch);
	}

	private void searchAndDownload() {
		output.out("Procurando novas legendas.");
		
		NewSubtitleLinkFoundCallback searchCallback = new NewSubtitleLinkFoundCallback(){@Override public void processAndReturnIfMatches(SubtitleAndLink subAndLink) {
			List<String> alreadyDownloadedFiles = filmeUtilsFolder.getAlreadyDownloaded();
			List<String> subtitlesToDownloadPatterns = filmeUtilsFolder.getSubtitlesToDownloadPatterns();
			String name = subAndLink.name;
			String link = subAndLink.link;
			
			for (String pattern : subtitlesToDownloadPatterns) {
				if (name.toLowerCase().matches(pattern) && !alreadyDownloadedFiles.contains(name)) {
					output.out("Pattern matched: "+name);
					boolean success = downloader.download(name, link);
					if(success){
						filmeUtilsFolder.addAlreadyDownloaded(name);
					}
				}
			}
		}};
		
		legendasTv.getNewer(QNTY_OF_SUBTITLES_TO_SCAN, searchCallback);
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
		legendasTv.login();
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
