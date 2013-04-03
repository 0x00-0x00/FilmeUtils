import java.io.File;

import filmeUtils.commons.VerboseSysOut;
import filmeUtils.downloader.Downloader;
import filmeUtils.subtitle.subtitleSites.LegendasTv;
import filmeUtils.torrent.torrentSites.TorrentSearcher;
import filmeUtils.torrent.torrentSites.TorrentSearcherImpl;
import filmeUtils.utils.extraction.ExtractorImpl;
import filmeUtils.utils.fileSystem.FileSystem;
import filmeUtils.utils.fileSystem.FileSystemImpl;
import filmeUtils.utils.http.MagnetLinkHandler;
import filmeUtils.utils.http.OSMagnetLinkHandler;
import filmeUtils.utils.http.SimpleHttpClient;
import filmeUtils.utils.http.SimpleHttpClientImpl;


public class DownloaderMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final VerboseSysOut output = new VerboseSysOut();
		final SimpleHttpClient httpclient = new SimpleHttpClientImpl();
		final ExtractorImpl extractor = new ExtractorImpl();
    	final MagnetLinkHandler magnetLinkHandler = new OSMagnetLinkHandler();
        final TorrentSearcher torrentSearcher = new TorrentSearcherImpl(httpclient);
		final FileSystem fileSystem = new FileSystemImpl();
		LegendasTv legendasTv = new LegendasTv(httpclient, output);
		Downloader downloader = new Downloader(extractor, fileSystem, httpclient, torrentSearcher, magnetLinkHandler, legendasTv, output);
//		downloader.download("Game of thrones S03e01", new File("/home/lucas/tmp"), ".*720.*");
		downloader.downloadFromNewest(".*game.*", ".*720.*",new File("/home/lucas/tmp"));
	}

}
