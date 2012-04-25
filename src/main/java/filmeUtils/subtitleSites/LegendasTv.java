package filmeUtils.subtitleSites;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import filmeUtils.OutputListener;
import filmeUtils.SearchListener;
import filmeUtils.http.SimpleHttpClient;

public class LegendasTv {
	
	private static final String BASE_URL = "http://legendas.tv";
	private static final String LOGIN_URL = BASE_URL+"/login_verificar.php";
	private static final String NEW_ADDS_URL = "/destaques.php?start=";
	private static final String SEARCH_ON_PAGE_URL = "/index.php?opcao=buscarlegenda&pagina=";
	
	private final SimpleHttpClient httpclient;
	private final OutputListener outputListener;
	private final String user;
	private final String password;
	
	public LegendasTv(final String user, final String password, final SimpleHttpClient httpclient, final OutputListener outputListener) {
		this.user = user;
		this.password = password;
		this.httpclient = httpclient;
		this.outputListener = outputListener;
	}
	
	public void login(){		
        try {
			final HashMap<String, String> params = new HashMap<String, String>();
			params.put("txtLogin", user);
			params.put("txtSenha", password);
			params.put("chkLogin", "1");
			
			
			outputListener.outVerbose("Autenticando como '"+user+"' ...");
			final String postResults = httpclient.post(LOGIN_URL, params);
			outputListener.out("Autenticado como '"+user+"'");
			
			if(postResults.contains("Dados incorretos")){
				outputListener.out("Login/senha incorretos");
				throw new RuntimeException();
			}
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void search(final String searchTerm, final SearchListener searchListener){
		try {
			searchRecursively(1, searchListener, searchTerm);
		} catch (final Exception e) {
			throw new RuntimeException("Ocorreu um erro na procura: ",e);
		}
	}

	public void getNewer(final int newAddsToShow, final SearchListener searchListener){
		final int startingIndex = 0;
		searchNewAdds(startingIndex, newAddsToShow, searchListener);
	}

	private void searchRecursively(final int page, final SearchListener searchListener, final String searchTerm)
			throws UnsupportedEncodingException, IOException,ClientProtocolException {
		
		String content = search(searchTerm, page);
		
		if(content.contains(" precisa estar logado para acessar essa ")){
			login();
			content = search(searchTerm, page);
		}
		if(content.contains(" temporariamente offline")){
			outputListener.out("Legendas tv temporariamente offline.");
			return;
		}
		extractSubtitlesLinks(content,searchListener);
		
		final int nextPage = page+1;
		
		final boolean pageLinkExists = pageLinkExists(content, nextPage);
		if(pageLinkExists){
			searchRecursively(nextPage, searchListener, searchTerm);
		}
	}

	private String search(final String searchTerm, final int page)
			throws ClientProtocolException, IOException {
		final String postUrl = BASE_URL+SEARCH_ON_PAGE_URL+page;
		final HashMap<String, String> params = new HashMap<String, String>();
		params.put("txtLegenda", searchTerm);
		params.put("selTipo", "1");
		params.put("int_idioma", "1");
		final String content = httpclient.post(postUrl,params);
		return content;
	}
	
	private void extractSubtitlesLinks(final String searchResult,final SearchListener searchListener) {
		final Document parsed = Jsoup.parse(searchResult);
		final Elements subtitleSpans = parsed.select("#conteudodest > div > span");
		for(final Element subtitleSpan : subtitleSpans) {
			final String subtitleName = getSubtitleName(subtitleSpan);
			final String subtitleLink = getSubtitleLink(subtitleSpan);
			searchListener.found(subtitleName, subtitleLink);
		}
	}

	private boolean pageLinkExists(final String content, final int nextPage) {
		final Document parsed = Jsoup.parse(content);
		final Element nextLink = parsed.select("a.paginacao:matches(0?"+nextPage+")").first();
		final boolean pageLinkExists = nextLink != null;
		return pageLinkExists;
	}

	private static String getSubtitleLink(final Element subtitleSpan) {
		Element subtitleLinkSpan = subtitleSpan.getElementsByClass("buscaDestaque").first();
		if(subtitleLinkSpan == null){
			subtitleLinkSpan = subtitleSpan.getElementsByClass("buscaNDestaque").first();
		}
		final String downloadLink = getDownloadFromOnClick(subtitleLinkSpan);
		return downloadLink;		
	}


	private static String getDownloadFromOnClick(final Element subtitleLinkSpan) {
		final String openDownloadJavascript = subtitleLinkSpan.attr("onclick");
		final String downloadLink = StringUtils.substringBetween(openDownloadJavascript, "'");
		return BASE_URL+"/info.php?c=1&d="+downloadLink;
	}

	private static String getSubtitleName(final Element subtitleSpan) {
		final Element subtitleNameSpan = subtitleSpan.getElementsByClass("brls").first();
		final String subtitleName = subtitleNameSpan.text();
		return subtitleName;
	}


	private void searchNewAdds(final int startingIndex, final int howMuchNewAddsToShow, final SearchListener searchListener) {
		final String content = getNewAddsStartingOnIndex(startingIndex);
		int currentIndex = startingIndex;
		final Document parsed = Jsoup.parse(content);
		final Elements subtitleSpans = parsed.select(".Ldestaque");
		for(final Element subtitleDiv : subtitleSpans) {
			if(currentIndex==howMuchNewAddsToShow){
				return;
			}
			currentIndex++;
			String subtitleName = subtitleDiv.attr("onmouseover");
			final String thirdQuotedWordRegex = "[^']*'[^']*','[^']*','([^']*)'.*";
			subtitleName = subtitleName.replaceAll(thirdQuotedWordRegex, "$1");
			final String downloadLink = getDownloadFromOnClick(subtitleDiv);
			searchListener.found(subtitleName, downloadLink);
		}
		if(currentIndex<howMuchNewAddsToShow){
			searchNewAdds(currentIndex, howMuchNewAddsToShow, searchListener);
		}
	}


	private String getNewAddsStartingOnIndex(final int startingIndex) {
		try {
			final String get = BASE_URL+NEW_ADDS_URL+startingIndex;
			return httpclient.get(get);
		} catch (final Exception e) {
			throw new RuntimeException("Ocorreu um erro ao pegar novas legendas: ",e);
		}
	}
	
}
