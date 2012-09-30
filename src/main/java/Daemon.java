import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import filmeUtils.Downloader;
import filmeUtils.FilmeUtilsConstants;
import filmeUtils.FilmeUtilsOptions;
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
		final FilmeUtilsOptions cli = new FilmeUtilsOptions() {
			
			@Override
			public boolean shouldRefuseNonHD() {
				return false;
			}
			
			@Override
			public boolean shouldRefuseHD() {
				return false;
			}
			
			@Override
			public boolean isVerbose() {
				return true;
			}
			
			@Override
			public boolean isLazy() {
				return false;
			}
			
			@Override
			public boolean isGeedy() {
				return false;
			}
			
			@Override
			public String getUser() {
				return "filmeutils"; 
			}
			
			@Override
			public File getSubtitlesDestinationFolderOrNull() {
				File filmeUtilsFolder = FilmeUtilsConstants.filmeUtilsFolder();
				File file = new File(filmeUtilsFolder,"subtitlefolder");
				try {
					String readFileToString = FileUtils.readFileToString(file);
					System.out.println(readFileToString);
					return new File(readFileToString);
				} catch (IOException e) {
					return null;
				}
			}
			
			@Override
			public String getPassword() {
				return "filmeutilsfilme" ;
			}
		};
		final VerboseSysOut output = new VerboseSysOut();
		LegendasTv legendasTv = new LegendasTv(new SimpleHttpClientImpl(), output);
		legendasTv.login();
		
		final File cookieFile = new File(FilmeUtilsConstants.filmeUtilsFolder(),"cookies.serialized");
    	final SimpleHttpClient httpclient = new SimpleHttpClientImpl(cookieFile);
    	final ExtractorImpl extract = new ExtractorImpl();
    	
    	final MagnetLinkHandler magnetLinkHandler = new OSMagnetLinkHandler();
        final TorrentSearcher torrentSearcher = new TorrentSearcherImpl(httpclient);
		final FileSystem fileSystem = new FileSystemImpl();
    	
		final Downloader downloader = new Downloader(extract, fileSystem, httpclient, torrentSearcher, magnetLinkHandler, legendasTv, output);
		
		File filmeUtilsFolder = FilmeUtilsConstants.filmeUtilsFolder();
		File filesToDownload = new File(filmeUtilsFolder,"downloadThis");
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
									System.out.println(name);
									downloader.download(name, link, cli);
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
