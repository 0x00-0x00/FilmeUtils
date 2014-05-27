package filmeUtils.torrent.torrentSites;


import filmeUtils.torrent.torrentSites.piratebay.SearchResult;
import filmeUtils.utils.http.SimpleHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import webGrude.Browser;


public class PirateBaySeWebgrude implements TorrentSite {

	private static final String THEPIRATEBAY_SE_SEARCH_URL = "http://thepiratebay.se/search/"+TorrentSiteUtils.SEARCH_TERM_TOKEN+"/0/7/0";
	private final SimpleHttpClient httpclient;

	public PirateBaySeWebgrude(final SimpleHttpClient httpclient) {
		this.httpclient = httpclient;
	}

	public String getMagnetLinkFirstResultOrNull(final String exactFileName) throws SiteOfflineException{
        SearchResult tpb = Browser.open(SearchResult.class);
        tpb.link.forEach( (e) -> System.out.println(e));
		return null;
	}

	@Override
	public String getSiteName() {
		return "PirateBay.se";
	}
	
}
