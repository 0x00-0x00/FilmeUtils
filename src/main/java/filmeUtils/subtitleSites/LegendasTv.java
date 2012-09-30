package filmeUtils.subtitleSites;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import filmeUtils.FilmeUtilsOptions;
import filmeUtils.OutputListener;
import filmeUtils.SubtitleLinkCallback;
import filmeUtils.http.SimpleHttpClient;

public class LegendasTv {
	
	private static final String BASE_URL = "http://legendas.tv";
	private static final String LOGIN_URL = BASE_URL+"/login_verificar.php";
	private static final String NEW_ADDS_URL = "/destaques.php?start=";
	private static final String SEARCH_ON_PAGE_URL = "/index.php?opcao=buscarlegenda&pagina=";
	
	private static final String USER = "filmeutils";
	private static final String PASSWORD = "filmeutilsfilme";
	
	private final SimpleHttpClient httpclient;
	private final OutputListener outputListener;
	
	public LegendasTv(final FilmeUtilsOptions cli, final SimpleHttpClient httpclient, final OutputListener outputListener) {
		this.httpclient = httpclient;
		this.outputListener = outputListener;
	}
	
	public void login(){		
        try {
			final HashMap<String, String> params = new HashMap<String, String>();
			params.put("txtLogin", USER);
			params.put("txtSenha", PASSWORD);
			params.put("chkLogin", "1");
			
			
			outputListener.outVerbose("Entrando no legendas.tv...");
			final String postResults = httpclient.post(LOGIN_URL, params);
			
			if(postResults.contains("Dados incorretos")){
				outputListener.out("Login/senha incorretos");
				throw new RuntimeException();
			}
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void search(final String searchTerm, final SubtitleLinkCallback searchListener){
		try {
			searchRecursively(1, searchListener, searchTerm);
		} catch (final Exception e) {
			throw new RuntimeException("Ocorreu um erro na procura: ",e);
		}
	}

	public void getNewer(final int newAddsToShow, final SubtitleLinkCallback searchListener){
		final int startingIndex = 0;
		searchNewAdds(startingIndex, newAddsToShow, searchListener);
	}

	class SubtitleAndLink{
		public String name;
		public String link;
		public SubtitleAndLink(String name, String link) {
			this.name = name;
			this.link = link;
		}
	}
	
	private void searchRecursively(final int page, final SubtitleLinkCallback searchCallback, final String searchTerm) throws IOException{
		
		String content = search(searchTerm, page);
		
		if(isOffline(content)){
			outputListener.out("Legendas tv temporariamente offline.");
			return;
		}
		if(onMaintenance(content)){
			outputListener.out("Legendas tv em manuntenção.");
			return;
		}
		
		if(isNotLogged(content)){
			login();
			content = search(searchTerm, page);
		}
	
		ArrayList<SubtitleAndLink> subtitleLinks = getSubtitleLinks(content);
		
		for (SubtitleAndLink link : subtitleLinks) {
			final boolean matches = searchCallback.processAndReturnIfMatches(link.name, link.link);
			if(stopOnFirstMatch() && matches){
				return;
			}
		}
		
		searchNextPage(page, searchCallback, searchTerm, content);
	}

	private ArrayList<SubtitleAndLink> getSubtitleLinks(String content) {
		ArrayList<SubtitleAndLink> links = new ArrayList<SubtitleAndLink>();
		final Document parsed = Jsoup.parse(content);
		final Elements subtitleSpans = parsed.select("#conteudodest > div > span");
		for(final Element subtitleSpan : subtitleSpans) {
			final String subtitleName = getSubtitleName(subtitleSpan);
			final String subtitleLink = getSubtitleLink(subtitleSpan);
			SubtitleAndLink subtitleAndLink = new SubtitleAndLink(subtitleName,subtitleLink);
			links.add(subtitleAndLink);
		}
		return links;
	}

	private void searchNextPage(final int page, final SubtitleLinkCallback searchListener, final String searchTerm, String content) throws IOException {
		final int nextPage = page+1;
		
		final boolean pageLinkExists = pageLinkExists(content, nextPage);
		if(pageLinkExists){
			searchRecursively(nextPage, searchListener, searchTerm);
		}
	}

	private boolean onMaintenance(String content) {
		return content.contains("Estamos realizando manuten");
	}

	private boolean isOffline(String content) {
		return content.contains(" temporariamente offline");
	}

	private boolean isNotLogged(String content) {
		return content.contains(" precisa estar logado para acessar essa ");
	}

	private boolean stopOnFirstMatch() {
		return false;
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


	private void searchNewAdds(final int startingIndex, final int howMuchNewAddsToShow, final SubtitleLinkCallback searchListener) {
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
			searchListener.processAndReturnIfMatches(subtitleName, downloadLink);
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
