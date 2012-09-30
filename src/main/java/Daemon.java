import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
		new Daemon();
	}
	
	public Daemon() throws IOException {
		File filmeUtilsFolder = FilmeUtilsFolder.get();
		File filesToDownload = new File(filmeUtilsFolder,"downloadThis");
		if(!filesToDownload.exists()){
			System.err.println("O arquivo "+filesToDownload.getAbsolutePath()+" tem que existir.");
			System.err.println("Esse arquivo deve ter uma regex por linha.");
			System.err.println("Quando uma nova legenda aparecer no legendas tv, o programa vai \n" +
					"baixar a legenda em " +FilmeUtilsFolder.getSubtitlesDestinationOrNull()+" e adicionar o torrent no cliente registrado.");
			throw new RuntimeException(filesToDownload.getAbsolutePath()+" not found.");
		}
		
		final VerboseSysOut output = new VerboseSysOut();
		LegendasTv legendasTv = new LegendasTv(new SimpleHttpClientImpl(), output);
		legendasTv.login();
		
		final File cookieFile = new File(FilmeUtilsFolder.get(),"cookies.serialized");
    	final SimpleHttpClient httpclient = new SimpleHttpClientImpl(cookieFile);
    	final ExtractorImpl extract = new ExtractorImpl();
    	
    	final MagnetLinkHandler magnetLinkHandler = new OSMagnetLinkHandler();
        final TorrentSearcher torrentSearcher = new TorrentSearcherImpl(httpclient);
		final FileSystem fileSystem = new FileSystemImpl();
    	
		final Downloader downloader = new Downloader(extract, fileSystem, httpclient, torrentSearcher, magnetLinkHandler, legendasTv, output);
		@SuppressWarnings("unchecked")
		final List<String> readLines = FileUtils.readLines(filesToDownload);
		
		final ArrayList<String> alreadyChecked = new ArrayList<String>();
		while(true){
			try {
				int checkInterval = 60000 * 10;
				legendasTv.getNewer(23, new NewSubtitleLinkFoundCallback() {
					@Override
					public void processAndReturnIfMatches(SubtitleAndLink subAndLink) {
						String name = subAndLink.name;
						String link = subAndLink.link;
						if (!alreadyChecked.contains(name)) {
							for (String pattern : readLines) {
								if (name.toLowerCase().matches(pattern)) {
									output.out("Pattern matched: "+name);
									downloader.download(name, link);
								}
							}
							alreadyChecked.add(name);
						}
					}
				});
				Thread.sleep(checkInterval);
			} catch (Exception e) {
				e.printStackTrace();// ignore and go on
			}
		}
	}	
}
