package filmeUtils.subtitle.subtitleSites;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import filmeUtils.utils.http.SimpleHttpClient;


public class DevLegendasTv {
	
	private final SimpleHttpClient httpclient;
	private final String devSite = "http://dev.legendas.tv";
	private final String getQuery = "/?q=views/ajax";
	private final String pageNumberToken = "${PAGENUMBER}";
	private final String postQuery = 
					"view_name=movies_list" 
					+ "&" +
					"view_display_id=page" 
					+ "&" +
					"pager_element=0" 
					+ "&" +
					"page="+pageNumberToken
					;

	public DevLegendasTv(final SimpleHttpClient httpclient) {
		this.httpclient = httpclient;
	} 

	public void searchNewAdds(final SubtitleLinkSearchCallback searchListener) throws ClientProtocolException, IOException {
		final int pagesToSearch = 10;
		for (int page = 0; page < pagesToSearch; page++) {
			searchNewAdds(searchListener, page);
		}
	}

	private void searchNewAdds(final SubtitleLinkSearchCallback searchListener,
			final int page) throws ClientProtocolException, IOException {
		final String pageAsString = page+"";
		final String result = httpclient.post(devSite + getQuery, postQuery.replace(pageNumberToken, pageAsString));
		final Object obj=JSONValue.parse(result);
		final JSONArray array=(JSONArray)obj;
		final JSONObject object = (JSONObject)array.get(2);
		final String newSeriesPage = (String) object.get("data");
		
		final Document parsed = Jsoup.parse(newSeriesPage);
		final Elements links = parsed.select(".tv a");
		for (final Element element : links) {
			final String subtitleLink = element.attr("href");
			searchListener.process(new SubtitlePackageAndLink(StringUtils.substringAfterLast(subtitleLink, "/"), subtitleLink));
		}
	}

}
