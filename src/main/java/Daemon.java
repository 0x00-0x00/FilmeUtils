import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

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

	public static void main(String[] args) throws IOException {
		if(args.length == 0)
			new Daemon(true);
		else
			new Daemon(!args[0].equals("onlyOnce"));
	}
	
	public Daemon(boolean continuousSearch) throws IOException {
		final FilmeUtilsFolder filmeUtilsFolder = FilmeUtilsFolder.getInstance();
		File subtitlesToDownloadFile = filmeUtilsFolder.getRegexFileWithPatternsToDownload();
		if(!subtitlesToDownloadFile.exists()){
			System.err.println("O arquivo "+subtitlesToDownloadFile.getAbsolutePath()+" tem que existir.");
			System.err.println("Esse arquivo deve ter uma regex por linha.");
			System.err.println("Quando uma nova legenda aparecer no legendas tv, o programa vai \n" +
					"baixar a legenda em " +filmeUtilsFolder.getSubtitlesDestination()+" e adicionar o torrent no cliente registrado.");
			throw new RuntimeException(subtitlesToDownloadFile.getAbsolutePath()+" not found.");
		}
		
		final File cookieFile = filmeUtilsFolder.getCookiesFile();
		final SimpleHttpClient httpclient = new SimpleHttpClientImpl(cookieFile);
		
		final VerboseSysOut output = new VerboseSysOut();
		LegendasTv legendasTv = new LegendasTv(httpclient, output);
		legendasTv.login();
		
    	final ExtractorImpl extract = new ExtractorImpl();
    	
    	final MagnetLinkHandler magnetLinkHandler = new OSMagnetLinkHandler();
        final TorrentSearcher torrentSearcher = new TorrentSearcherImpl(httpclient);
		final FileSystem fileSystem = new FileSystemImpl();
    	
		final Downloader downloader = new Downloader(extract, fileSystem, httpclient, torrentSearcher, magnetLinkHandler, legendasTv, output);
		
		
		final List<String> alreadyDownloadedFiles = filmeUtilsFolder.getAlreadyDownloaded();
		
		do{
			final List<String> subsToDownload = FileUtils.readLines(subtitlesToDownloadFile);
			output.out("Procurando novas legendas.");
			try {
				int checkInterval = 60000 * 10;
				int valuesPerPage = 23;
				
				NewSubtitleLinkFoundCallback searchCallback = new NewSubtitleLinkFoundCallback(){@Override public void processAndReturnIfMatches(SubtitleAndLink subAndLink) {
					String name = subAndLink.name;
					String link = subAndLink.link;
					for (String pattern : subsToDownload) {
						if (name.toLowerCase().matches(pattern) && !alreadyDownloadedFiles.contains(name)) {
							output.out("Pattern matched: "+name);
							boolean success = downloader.download(name, link);
							if(success){
								alreadyDownloadedFiles.add(name);
								filmeUtilsFolder.addAlreadyDownloaded(name);
							}
						}
					}
				}};
				
				legendasTv.getNewer(valuesPerPage*3, searchCallback);
				if(continuousSearch)
					Thread.sleep(checkInterval);
			} catch (Exception e) {
				e.printStackTrace();// ignore and go on
			}
		}while(continuousSearch);
	}	
}
