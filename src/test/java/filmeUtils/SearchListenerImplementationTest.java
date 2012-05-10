package filmeUtils;

import org.junit.Ignore;
import org.junit.Test;

import filmeUtils.extraction.Extractor;
import filmeUtils.fileSystem.FileSystem;
import filmeUtils.http.MagnetLinkHandler;
import filmeUtils.http.SimpleHttpClient;
import filmeUtils.subtitleSites.LegendasTv;
import filmeUtils.torrentSites.TorrentSearcher;

public class SearchListenerImplementationTest {

	@Ignore
	@Test
	public void test(){
		final FileSystem fileSystem = null;
		final SimpleHttpClient httpclient = null;
		final Extractor extract = null;
		final TorrentSearcher torrentSearcher = null;
		final MagnetLinkHandler magnetLinkHandler = null;
		final LegendasTv legendasTv = null;
		final FilmeUtilsOptions cli = null;
		final OutputListener outputListener = null;
//		final SearchListenerImplementation subject = new SearchListenerImplementation(fileSystem, httpclient, extract, torrentSearcher, magnetLinkHandler, legendasTv, cli, outputListener);
//		subject.foundReturnIfShouldStopLooking("fooSubs", "fooLink");
	}
	
}
