package filmeUtils.subtitle.subtitleSites;


import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

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
	
	private static final String PAGE_TOKEN = "$PAGE$";
	private static final String SEARCH_TERM_TOKEN = "$SEARCH_TERM$";
	private static final String HASH_TOKEN = "$HASH$";
	private static final String BASE_URL = "http://legendas.tv";
	private static final String NEW_ADDS_URL = BASE_URL+"/util/carrega_destaques/todos/"+PAGE_TOKEN;
    private static final String PT_BR = "1";
    private static final String SEARCH_ON_PAGE_URL = BASE_URL+"/util/carrega_legendas_busca/" + SEARCH_TERM_TOKEN + "/" +PT_BR + "/-/" + PAGE_TOKEN;
	private static final String DOWNLOAD_URL = BASE_URL+"/pages/downloadarquivo/"+HASH_TOKEN;
	
	private final SimpleHttpClient httpclient;
	private final OutputListener outputListener;
	
	public LegendasTv(final SimpleHttpClient httpclient, final OutputListener outputListener) {
		this.httpclient = httpclient;
		this.outputListener = outputListener;
	}
	public void search(final String searchTerm, final SubtitleLinkSearchCallback searchListener){
		try {
			searchRecursively(1, searchListener, searchTerm);
		} catch (final Exception e) {
			outputListener.out(ExceptionUtils.getFullStackTrace(e));
			throw new RuntimeException("Ocorreu um erro na procura: ",e);
		}
	}

	public void getNewer(final SubtitleLinkSearchCallback searchListener){
		final int howMuchPagesToLoad = FileSystemUtils.getInstance().newerPagesSearchCount();
		getNewer(howMuchPagesToLoad, searchListener);
	}
	
	public void getNewer(final int howMuchPagesToLoad,final SubtitleLinkSearchCallback searchListener){
		searchNewAdds(searchListener, howMuchPagesToLoad);
	}
	
	private void searchRecursively(final int page, final SubtitleLinkSearchCallback searchCallback, final String searchTerm) throws IOException{
		final String content = search(searchTerm, page);
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

	private String search(final String searchTerm, final int page) throws ClientProtocolException, IOException {
		final String getUrl = SEARCH_ON_PAGE_URL.replace(SEARCH_TERM_TOKEN, searchTerm.replace(" ", "%20") ).replace(PAGE_TOKEN, page+"");
		final String content = httpclient.get(getUrl);
		return content;
	}

	private boolean pageLinkExists(final String content, final int nextPage) {
		final Document parsed = Jsoup.parse(content);
		parsed.select("div.pagination button.ajax [text="+nextPage+"]");
		final Elements pages = parsed.select("div.pagination button.ajax");
		for (final Element element : pages) {
			if(element.text().equals(nextPage+""))
				return true;
		}
		return false;
	}

	private static String getSubtitleLink(final Element subtitleSpan) {
		final String subtitleHash = subtitleSpan.attr("href").replaceAll("/download/([0-9a-z]*)/.*", "$1");
		return getDownloadUrlForHash(subtitleHash);		
	}
	private static String getDownloadUrlForHash(final String hash) {
		return DOWNLOAD_URL.replace(HASH_TOKEN,hash);
	}

	private static String getSubtitleName(final Element subtitleSpan) {
		return subtitleSpan.text().replace(".", " ");
	}

	private void searchNewAdds(final SubtitleLinkSearchCallback searchListener, final int howMuchPagesToLoad) {
		final int firstPage = 1;
		searchNewAddsRecursivelly(firstPage, howMuchPagesToLoad, searchListener);
	}
	
	private void searchNewAddsRecursivelly(final int page, final int howMuchPagesToLoad, final SubtitleLinkSearchCallback searchListener) {
		final String content = getNewAddsOnPage(page);
		final Document parsed = Jsoup.parse(content);
		final Elements subtitleSpans = parsed.select("div.film span.bt_seta_download a.texto");
		for(final Element subtitleButton : subtitleSpans) {
			final String downloadPreviewLink = subtitleButton.attr("href");
			final String hash = downloadPreviewLink.replaceAll("/download/([0-9a-z]*)/.*", "$1");
			final String subtitleName = downloadPreviewLink.replaceAll(".*/(.*)", "$1").replace("_", " ");
			final SubtitlePackageAndLink nameAndlink = new SubtitlePackageAndLink(subtitleName, getDownloadUrlForHash(hash));
			searchListener.process(nameAndlink);
		}
		if(page<howMuchPagesToLoad){
			searchNewAddsRecursivelly(page+1, howMuchPagesToLoad, searchListener);
		}
	}


	private String getNewAddsOnPage(final int page) {
		try {
			final String get = NEW_ADDS_URL.replace(PAGE_TOKEN, page+"");
			return httpclient.get(get);
		}catch(final SocketTimeoutException timeout){
			throw new RuntimeException("Legendas tv muito lento ou fora do ar: ",timeout);
		} catch (final Exception e) {
			throw new RuntimeException("Ocorreu um erro ao pegar novas legendas: ",e);
		}
	}	
}
