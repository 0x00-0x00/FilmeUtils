package filmeUtils.subtitle.subtitleSites.legendasTV;

import org.apache.commons.lang.exception.ExceptionUtils;

import filmeUtils.commons.FileSystemUtils;
import filmeUtils.commons.OutputListener;
import filmeUtils.subtitle.subtitleSites.SubtitleLinkSearchCallback;

public class LegendasTv {

    private final OutputListener outputListener;

    public LegendasTv(final OutputListener outputListener) {
        this.outputListener = outputListener;
    }

    public void search(final String searchTerm, final SubtitleLinkSearchCallback searchListener) {
        try {
            Search search = Search.search(searchTerm);
            search.getSubtitlePackageAndLink().forEach(searchListener::process);
            while (search.hasNext()) {
                final Search next = search.next();
                next.getSubtitlePackageAndLink().forEach(searchListener::process);
                search = next;
            }

        } catch (final Exception e) {
            this.outputListener.out(ExceptionUtils.getFullStackTrace(e));
            throw new RuntimeException("Ocorreu um erro na procura: ", e);
        }
    }

    public void getNewer(final SubtitleLinkSearchCallback searchListener) {
        final int howMuchPagesToLoad = FileSystemUtils.getInstance().newerPagesSearchCount();
        this.getNewer(howMuchPagesToLoad, searchListener);
    }

    public void getNewer(final int howMuchPagesToLoad, final SubtitleLinkSearchCallback searchListener) {
        NewerSubtitles newerSubtitles = NewerSubtitles.open();
        while (newerSubtitles.currentPage <= howMuchPagesToLoad) {
            newerSubtitles.getSubtitlePackageAndLink().forEach(sl -> searchListener.process(sl));
            newerSubtitles = newerSubtitles.nextPage();
        }
    }

    public static String getDownloadLink(final String l) {
        return "http://legendas.tv/pages/downloadarquivo/" + l.replaceAll("/download/([0-9a-z]*)/.*", "$1");
    }
}
