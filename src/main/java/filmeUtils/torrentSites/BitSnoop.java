package filmeUtils.torrentSites;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import filmeUtils.SimpleHttpClient;

public class BitSnoop implements TorrentSite {

	private static final String BITSNOOP_URL = "http://bitsnoop.com";
	private static final String BITSNOOP_SEARCH_URL = BITSNOOP_URL+"/search/all/"+TorrentSiteUtils.SEARCH_TERM_TOKEN;
	
	private final SimpleHttpClient httpclient;
	
	public BitSnoop(final SimpleHttpClient httpclient) {
		this.httpclient = httpclient;
	}
	
	public String getMagnetLinkFirstResultOrNull(final String exactFileName) {
		final String url = TorrentSiteUtils.getUrlFor(BITSNOOP_SEARCH_URL,exactFileName);
		final String searchResult = httpclient.getOrNull(url);
		if(searchResult == null) return null;
		final Document parsed = Jsoup.parse(searchResult);
		final Elements select = parsed.select("#torrents li a");
		final Element firstLink = select.first();
		if(firstLink == null) return null;
		return magnetFromLink(firstLink.attr("href"));
	}

	private String magnetFromLink(final String link) {
		final String url = BITSNOOP_URL + link;
		final String searchResult = httpclient.getOrNull(url);
		if(searchResult == null) return null;
		final Document parsed = Jsoup.parse(searchResult);
		final Elements elementsByClass = parsed.getElementsByClass("dl_mag2");
		if(elementsByClass.isEmpty()) return null;
		final String magnetLink = elementsByClass.attr("href");
		return magnetLink;
	}

}
