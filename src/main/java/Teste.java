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

import filmeUtils.utils.http.SimpleHttpClientImpl;


public class Teste {
	
	public static void main(final String[] args) throws ClientProtocolException, IOException {
		final SimpleHttpClientImpl simpleHttpClientImpl = new SimpleHttpClientImpl();
		
		final String postQuery = 
				"view_name=movies_list" 
				+ "&" +
				"view_display_id=page" 
				+ "&" +
				"pager_element=0" 
				+ "&" +
				"page=${PAGENUMBER}"
				;
		
		final String result = simpleHttpClientImpl.post("http://dev.legendas.tv/?q=views/ajax", postQuery.replace("${PAGENUMBER}", "1"));
		
		final Object obj=JSONValue.parse(result);
		final JSONArray array=(JSONArray)obj;
		final JSONObject object = (JSONObject)array.get(2);
		final String newSeriesPage = (String) object.get("data");
		
		final Document parsed = Jsoup.parse(newSeriesPage);
		final Elements links = parsed.select(".tv a");
		System.out.println(links.size());
		for (final Element element : links) {
			final String subtitleLink = element.attr("href");
			
			System.out.println(subtitleLink+": "+ StringUtils.substringAfterLast(subtitleLink, "/"));
		}
		
	}

}
