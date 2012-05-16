package filmeUtils.torrentSites;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import filmeUtils.http.SimpleHttpClient;


public class PirateBaySe implements TorrentSite {

	
	private static final String THEPIRATEBAY_SE_SEARCH_URL = "http://thepiratebay.se/search/"+TorrentSiteUtils.SEARCH_TERM_TOKEN+"/0/7/0";
	private final SimpleHttpClient httpclient;

	public PirateBaySe(final SimpleHttpClient httpclient) {
		this.httpclient = httpclient;
	}

	public String getMagnetLinkFirstResultOrNull(final String exactFileName){
		final String url = TorrentSiteUtils.getUrlFor(THEPIRATEBAY_SE_SEARCH_URL,exactFileName);
		final String searchResult = httpclient.getOrNull(url);
		if(searchResult == null)
			return null;
		final Document parsed = Jsoup.parse(searchResult);
		final Elements select = parsed.select("#searchResult tbody tr td a");
		for (final Element element : select) {
			final String href = element.attr("href");
			if(href.contains("magnet:")){
				return href;
			}
		}
		return null;
	}
	
}
