package filmeUtils.subtitle.subtitleSites;


import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import filmeUtils.subtitle.subtitleSites.legendasTV.NewerSubtitles;
import filmeUtils.subtitle.subtitleSites.legendasTV.Search;
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

	private final OutputListener outputListener;
	
	public LegendasTv(final OutputListener outputListener) {
		this.outputListener = outputListener;
	}
	public void search(final String searchTerm, final SubtitleLinkSearchCallback searchListener){
		try {

            Search result = null;
            do{
                result = result == null? Search.search(searchTerm) :result.next();
                result.getSubtitlePackageAndLink().forEach(sl -> searchListener.process(sl));
            }while(result.hasNext());

		} catch (final Exception e) {
			outputListener.out(ExceptionUtils.getFullStackTrace(e));
			throw new RuntimeException("Ocorreu um erro na procura: ",e);
		}
	}

	public void getNewer(final SubtitleLinkSearchCallback searchListener){
		final int howMuchPagesToLoad = FileSystemUtils.getInstance().newerPagesSearchCount();
        getNewer(howMuchPagesToLoad, searchListener);
	}

    public void getNewer(int howMuchPagesToLoad, SubtitleLinkSearchCallback searchListener) {
        NewerSubtitles newerSubtitles = NewerSubtitles.open();
        while (newerSubtitles.currentPage <= howMuchPagesToLoad) {
            newerSubtitles.getSubtitlePackageAndLink().forEach(sl -> searchListener.process(sl));
            newerSubtitles = newerSubtitles.nextPage();
        }
    }
}
