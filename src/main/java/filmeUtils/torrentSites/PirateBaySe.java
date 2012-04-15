package filmeUtils.torrentSites;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import filmeUtils.FilmeUtilsHttpClient;


public class PirateBaySe {

	private static final String THEPIRATEBAY_SE_SEARCH_URL = "http://thepiratebay.se/search/";
	private static final String MORESEEDS_SEARCH_URL = "/0/7/0";
	private final FilmeUtilsHttpClient httpclient;

	public PirateBaySe(final FilmeUtilsHttpClient httpclient) {
		this.httpclient = httpclient;
	}

	public String getMagnetLinkForFile(final String exactFileName) throws ClientProtocolException, IOException{
		final HttpGet httpGet = new HttpGet(THEPIRATEBAY_SE_SEARCH_URL+exactFileName+MORESEEDS_SEARCH_URL);
		final String searchResult = httpclient.executeAndGetResponseContents(httpGet);
		final Document parsed = Jsoup.parse(searchResult);
		final Elements select = parsed.select("#searchResult tbody tr td a");
		for (final Element element : select) {
			final String href = element.attr("href");
			if(href.contains("magnet:")){
				return href;
			}
		}
		return "magnet link not found";
	}
	
}
