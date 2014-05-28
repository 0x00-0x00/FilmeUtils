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

	public String getMagnetLinkFirstResultOrNull(final String exactFileName) throws SiteOfflineException{
        final String url = TorrentSiteUtils.getUrlFor(THEPIRATEBAY_SE_SEARCH_URL,exactFileName);
        SearchResult tpb = Browser.open(url, SearchResult.class);
        return tpb.link.get(0);
	}

	@Override
	public String getSiteName() {
		return "PirateBay.se";
	}
	
}
