package filmeUtils.subtitle.subtitleSites;


import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.client.ClientProtocolException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import filmeUtils.commons.FileSystemUtils;
import filmeUtils.commons.OutputListener;
import filmeUtils.utils.http.SimpleHttpClient;

public class LegendasTv {
	
	private static final String BASE_URL = "http://legendas.tv";
	private static final String LOGIN_URL = BASE_URL+"/login_verificar.php";
	private static final String NEW_ADDS_URL = "/destaques.php?start=";
	private static final String SEARCH_ON_PAGE_URL = BASE_URL+"/busca?q=";
	private static final String DOWNLOAD_URL = BASE_URL+"/pages/downloadarquivo/";
	
	private static final boolean USING_DEV_VERSION = false;
	
	private final SimpleHttpClient httpclient;
	private final OutputListener outputListener;
	private final DevLegendasTv devLegendasTv;
	
	public LegendasTv(final SimpleHttpClient httpclient, final OutputListener outputListener, final boolean login) {
		this.httpclient = httpclient;
		this.outputListener = outputListener;
		devLegendasTv = new DevLegendasTv(httpclient);
		if(login)
			login();
	}
	
	public LegendasTv(final SimpleHttpClient httpclient, final OutputListener outputListener) {
		this(httpclient, outputListener, true);
	}
	
	public void login(){	
		if(USING_DEV_VERSION){
			outputListener.out("Login desabilitado enquanto o legendas tv não voltar com a versão oficial.");
			return;
		}
        try {
			final HashMap<String, String> params = new HashMap<String, String>();
			final FileSystemUtils instance = FileSystemUtils.getInstance();
			final String user = instance.getUser();
			final String password = instance.getPassword();
			params.put("txtLogin", user);
			params.put("txtSenha", password);
			params.put("chkLogin", "1");
			
			
			outputListener.outVerbose("Entrando no legendas.tv...");
			final String postResults = httpclient.post(LOGIN_URL, params);
			
			if(postResults == null){
				outputListener.out("Legendas tv não está respondendo");
				throw new RuntimeException();
			}
			
			if(postResults.isEmpty()){
				outputListener.out("Legendas tv não está respondendo");
				throw new RuntimeException();
			}
			
			if(postResults.contains("Dados incorretos")){
				outputListener.out("Login/senha incorretos");
				throw new RuntimeException();
			}
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void search(final String searchTerm, final SubtitleLinkSearchCallback searchListener){
		if(USING_DEV_VERSION){
			outputListener.out("Procura desabilitada enquanto o legendas tv não voltar com a versão oficial.");
			return;
		}
		try {
			searchRecursively(1, searchListener, searchTerm);
		} catch (final Exception e) {
			outputListener.out(ExceptionUtils.getFullStackTrace(e));
			throw new RuntimeException("Ocorreu um erro na procura: ",e);
		}
	}

	public void getNewer(final SubtitleLinkSearchCallback searchListener){
		if(USING_DEV_VERSION){
			try {
				devLegendasTv.searchNewAdds(searchListener);
			} catch (final Exception e) {
				throw new RuntimeException("Não foi possível achar novas legendas no site:\n"+e);
			}
		}else{
			searchNewAdds(searchListener);
		}
	}
	
	private void searchRecursively(final int page, final SubtitleLinkSearchCallback searchCallback, final String searchTerm) throws IOException{
		
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
	
		final ArrayList<SubtitlePackageAndLink> subtitleLinks = getSubtitleLinks(content);
		
		for (final SubtitlePackageAndLink link : subtitleLinks) {
			searchCallback.process(link);
		}
		
		searchNextPage(page, searchCallback, searchTerm, content);
	}

	private ArrayList<SubtitlePackageAndLink> getSubtitleLinks(final String content) {
		final ArrayList<SubtitlePackageAndLink> links = new ArrayList<SubtitlePackageAndLink>();
		final Document parsed = Jsoup.parse(content);
		final Elements subtitleSpans = parsed.select(".f_left p a[href^=/download");
		for(final Element subtitleSpan : subtitleSpans) {
			final String subtitleName = getSubtitleName(subtitleSpan);
			final String subtitleLink = getSubtitleLink(subtitleSpan);
			final SubtitlePackageAndLink subtitleAndLink = new SubtitlePackageAndLink(subtitleName,subtitleLink);
			links.add(subtitleAndLink);
		}
		return links;
	}

	private void searchNextPage(final int page, final SubtitleLinkSearchCallback searchListener, final String searchTerm, final String content) throws IOException {
		final int nextPage = page+1;
		
		final boolean pageLinkExists = pageLinkExists(content, nextPage);
		if(pageLinkExists){
			searchRecursively(nextPage, searchListener, searchTerm);
		}
	}

	private boolean onMaintenance(final String content) {
		return content.contains("Estamos realizando manuten") || content.contains("temporariamente fora do ar");
	}

	private boolean isOffline(final String content) {
		return content.contains(" temporariamente offline");
	}

	private boolean isNotLogged(final String content) {
		return content.contains(" precisa estar logado para acessar essa ");
	}

	private String search(final String searchTerm, final int page)
			throws ClientProtocolException, IOException {
		final String getUrl = SEARCH_ON_PAGE_URL+searchTerm;
		final String content = httpclient.get(getUrl);
		return content;
	}

	private boolean pageLinkExists(final String content, final int nextPage) {
		final Document parsed = Jsoup.parse(content);
		final Element nextLink = parsed.select("a.paginacao:matches(0?"+nextPage+")").first();
		final boolean pageLinkExists = nextLink != null;
		return pageLinkExists;
	}

	private static String getSubtitleLink(final Element subtitleSpan) {
		final String getSubtitleHash = subtitleSpan.attr("href").replaceAll("/download/([0-9a-z]*)/.*", "$1");
		return DOWNLOAD_URL+getSubtitleHash;		
	}


	private static String getDownloadFromOnClick(final Element subtitleLinkSpan) {
		final String openDownloadJavascript = subtitleLinkSpan.attr("onclick");
		final String downloadLink = StringUtils.substringBetween(openDownloadJavascript, "'");
		return BASE_URL+"/info.php?c=1&d="+downloadLink;
	}

	private static String getSubtitleName(final Element subtitleSpan) {
		return subtitleSpan.text();
	}


	private void searchNewAdds(final SubtitleLinkSearchCallback searchListener) {
		final int newAddsToShow = 23*3;
		searchNewAddsRecursivelly(0, newAddsToShow, searchListener);
	}
	
	private void searchNewAddsRecursivelly(final int startingIndex, final int howMuchNewAddsToShow, final SubtitleLinkSearchCallback searchListener) {
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
			final SubtitlePackageAndLink nameAndlink = new SubtitlePackageAndLink(subtitleName, downloadLink);
			searchListener.process(nameAndlink);
		}
		if(currentIndex==0){
			throw new RuntimeException("Não foi possível achar novas legendas no site:\n"+content);
		}
		if(currentIndex<howMuchNewAddsToShow){
			searchNewAddsRecursivelly(currentIndex, howMuchNewAddsToShow, searchListener);
		}
	}


	private String getNewAddsStartingOnIndex(final int startingIndex) {
		try {
			final String get = BASE_URL+NEW_ADDS_URL+startingIndex;
			return httpclient.get(get);
		}catch(final SocketTimeoutException timeout){
			throw new RuntimeException("Legendas tv muito lento ou fora do ar: ",timeout);
		} catch (final Exception e) {
			throw new RuntimeException("Ocorreu um erro ao pegar novas legendas: ",e);
		}
	}	
}
