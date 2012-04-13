package filmeUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LegendasTv {
	
	private static final String BASE_URL = "http://legendas.tv";
	private static final String USER = "greasemonkey";
	private static final String PASSWORD = "greasemonkey";
	private static final String LOGIN_URL = BASE_URL+"/login_verificar.php";
	
	private static final String FIRST_RESULT_URL = "/index.php?opcao=buscarlegenda";
	
	
	public static void login(final DefaultHttpClient httpclient) throws UnsupportedEncodingException, IOException, ClientProtocolException {
		final HttpPost httpost = new HttpPost(LOGIN_URL);
        final List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        nvps.add(new BasicNameValuePair("txtLogin", USER));
        nvps.add(new BasicNameValuePair("txtSenha", PASSWORD));

        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        httpclient.execute(httpost);
	}

	public static void search(final String searchTerm, final DefaultHttpClient httpclient) throws UnsupportedEncodingException, IOException, ClientProtocolException {
		
		final String firstSearch = FIRST_RESULT_URL;
		
		final List<String> searchResults = new ArrayList<String>();
		searchRecursively(httpclient, firstSearch, searchResults, searchTerm);
		
		final Map<String, String> nameToFileLink = extractSubtitlesLinks(searchResults);
		
		final Set<Entry<String, String>> entrySet = nameToFileLink.entrySet();
		for (final Entry<String, String> entry : entrySet) {			
			System.out.println(entry.getKey()+" - "+entry.getValue());
		}
	}

	private static Map<String, String> extractSubtitlesLinks(
			final List<String> searchResults) {
		final Map<String, String> nameToFileLink = new LinkedHashMap<String, String>();
		for (final String searchResult : searchResults) {			
			final Document parsed = Jsoup.parse(searchResult);
			final Elements subtitleSpans = parsed.select("#conteudodest > div > span");
			for(final Element subtitleSpan : subtitleSpans) {
				final String subtitleName = getSubtitleName(subtitleSpan);
				final String subtitleLink = getSubtitleLink(subtitleSpan);
				nameToFileLink.put(subtitleName, subtitleLink);
			}
		}
		return nameToFileLink;
	}

	private static void searchRecursively(final DefaultHttpClient httpclient,final String searchParams, final List<String> searchResults, final String searchTerm)
			throws UnsupportedEncodingException, IOException,ClientProtocolException {
		final HttpPost httpost = new HttpPost(BASE_URL+searchParams);
		
		
	    final List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("txtLegenda", searchTerm));
	    nvps.add(new BasicNameValuePair("selTipo", "1"));
	    nvps.add(new BasicNameValuePair("int_idioma", "1"));
	    
	    httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
	    final HttpResponse response = httpclient.execute(httpost);
	    final HttpEntity entity = response.getEntity();
		final InputStream contentIS = entity.getContent();
		final String content = IOUtils.toString(contentIS);
		contentIS.close();
		searchResults.add(content);
		
		final Document parsed = Jsoup.parse(content);
		final Element nextLink = parsed.select("a.btavvt:matches(Próxima)").first();
		if(nextLink != null){
			final String nextLinkParams = nextLink.attr("href");
			searchRecursively(httpclient, nextLinkParams, searchResults, searchTerm);
		}
	}

	private static String getSubtitleLink(final Element subtitleSpan) {
		Element subtitleLinkSpan = subtitleSpan.getElementsByClass("buscaDestaque").first();
		if(subtitleLinkSpan == null){
			subtitleLinkSpan = subtitleSpan.getElementsByClass("buscaNDestaque").first();
		}
		final String openDownloadJavascript = subtitleLinkSpan.attr("onclick");
		final String downloadLink = StringUtils.substringBetween(openDownloadJavascript, "'");
		return BASE_URL+"/info.php?c=1&d="+downloadLink;		
	}

	private static String getSubtitleName(final Element subtitleSpan) {
		final Element subtitleNameSpan = subtitleSpan.getElementsByClass("brls").first();
		final String subtitleName = subtitleNameSpan.text();
		return subtitleName;
	}
	
}
