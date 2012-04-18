package filmeUtils.torrentSites;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.client.methods.HttpGet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import filmeUtils.FilmeUtilsHttpClient;


public class PirateBaySe implements TorrentSite {

	private static final String THEPIRATEBAY_SE_SEARCH_URL = "http://thepiratebay.se/search/";
	private static final String MORESEEDS_SEARCH_URL = "/0/7/0";
	private final FilmeUtilsHttpClient httpclient;

	public PirateBaySe(final FilmeUtilsHttpClient httpclient) {
		this.httpclient = httpclient;
	}

	public String getMagnetLinkFirstResultOrNull(final String exactFileName){
		final String encoded;
		try {
			encoded = URLEncoder.encode(exactFileName,"UTF-8");
		} catch (final UnsupportedEncodingException e1) {
			throw new RuntimeException("Never gonna happen",e1);
		}
		final String uri = THEPIRATEBAY_SE_SEARCH_URL+encoded+MORESEEDS_SEARCH_URL;
		System.out.println(uri);
		final HttpGet httpGet = new HttpGet(uri);
		String searchResult;
		try {
			searchResult = httpclient.executeAndGetResponseContents(httpGet);
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
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
