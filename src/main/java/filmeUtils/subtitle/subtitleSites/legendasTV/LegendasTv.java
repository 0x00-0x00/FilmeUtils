package filmeUtils.subtitle.subtitleSites.legendasTV;


import filmeUtils.commons.FileSystemUtils;
import filmeUtils.commons.OutputListener;
import filmeUtils.subtitle.subtitleSites.SubtitleLinkSearchCallback;
import org.apache.commons.lang.exception.ExceptionUtils;

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

    public static String getDownloadLink(String l) {
        return "http://legendas.tv/pages/downloadarquivo/"+ l.replaceAll("/download/([0-9a-z]*)/.*", "$1");
    }
}
