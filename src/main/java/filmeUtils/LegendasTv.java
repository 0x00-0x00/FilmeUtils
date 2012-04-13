package filmeUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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

	public static void search(final DefaultHttpClient httpclient) throws UnsupportedEncodingException, IOException, ClientProtocolException {
		
		final String firstSearch = FIRST_RESULT_URL;
		//<a class="btavvt" href="/index.php?opcao=buscarlegenda&pagina=10">Próxima</a>
		
		final HttpPost httpost = new HttpPost(BASE_URL+firstSearch);
		
	    final List <NameValuePair> nvps = new ArrayList <NameValuePair>();
	    nvps.add(new BasicNameValuePair("txtLegenda", "house"));
	    nvps.add(new BasicNameValuePair("selTipo", "1"));
	    nvps.add(new BasicNameValuePair("int_idioma", "1"));
	    
	    httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
	    final HttpResponse response = httpclient.execute(httpost);
	    final HttpEntity entity = response.getEntity();
		final InputStream contentIS = entity.getContent();
		final String content = IOUtils.toString(contentIS);
		contentIS.close();
		
		final Document parsed = Jsoup.parse(content);
		final Elements subtitleSpans = parsed.select("#conteudodest > div > span");
		for(final Element subtitleSpan : subtitleSpans) {
			final String subtitleName = getSubtitleName(subtitleSpan);
			final String subtitleLink = getSubtitleLink(subtitleSpan);
			System.out.println(subtitleName+" - "+subtitleLink);
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
