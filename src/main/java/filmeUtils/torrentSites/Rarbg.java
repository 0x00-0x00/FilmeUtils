package filmeUtils.torrentSites;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import filmeUtils.http.SimpleHttpClient;

public class Rarbg implements TorrentSite {

	private static final String RARBG_URL = "http://rarbg.com";
	private static final String BITSNOOP_SEARCH_URL = RARBG_URL+"/torrents.php?search="+TorrentSiteUtils.SEARCH_TERM_TOKEN;
	
	private final SimpleHttpClient httpclient;
	
	public Rarbg(final SimpleHttpClient httpclient) {
		this.httpclient = httpclient;
	}
	
	public String getMagnetLinkFirstResultOrNull(final String exactFileName) throws SiteOfflineException {
		final String url = TorrentSiteUtils.getUrlFor(BITSNOOP_SEARCH_URL,exactFileName);
		final String searchResult = httpclient.getOrNull(url);
		final Document parsed = Jsoup.parse(searchResult);
		final Elements select = parsed.select("a[href^=/torrents/filmi/download]:not(:has(img))");//href="/torrents/filmi/download/ff09ff348638385bbc092a14357272f762991c99/torrent.html" title="">The.Mentalist.S04E23.HDTV.XviD-AFG</a>
		final Element firstLink = select.first();
		if(firstLink == null) return null;
		return magnetFromLink(firstLink.attr("href"));
	}

	private String magnetFromLink(final String link) throws SiteOfflineException {
		final String url = RARBG_URL + link;
		final String searchResult = httpclient.getOrNull(url);
		if(searchResult == null)
			throw new SiteOfflineException("BitSnoop");
		final Document parsed = Jsoup.parse(searchResult);
		final Elements select = parsed.select("a[href^=magnet]");
		if(select.isEmpty()) return null;
		final String magnetLink = select.attr("href");
		return magnetLink;
	}

	@Override
	public String getSiteName() {
		return "Rarbg";
	}

}
