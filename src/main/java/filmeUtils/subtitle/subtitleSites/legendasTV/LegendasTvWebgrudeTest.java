package filmeUtils.subtitle.subtitleSites.legendasTV;

import filmeUtils.subtitle.subtitleSites.SubtitlePackageAndLink;

public class LegendasTvWebgrudeTest {

    public static void main(String... args) {
        System.out.println("Newer");
        newer();
        System.out.println("##########################################");
        System.out.println("Search house");
        search();
    }

    private static void search() {
        String term = "house m.d.";
        Search result = null;
        do{
            result = result == null? Search.search(term) :result.next();
            result.getSubtitlePackageAndLink().forEach(sl -> System.out.println(sl));
        }while(result.hasNext());
    }

    private static void newer() {
        NewerSubtitles newerSubtitles = NewerSubtitles.open();
        while (newerSubtitles.currentPage <= 3) {
            System.out.println(newerSubtitles.currentPage);
            newerSubtitles.getSubtitlePackageAndLink().forEach(sl -> System.out.println(sl));
            newerSubtitles = newerSubtitles.nextPage();
        }
    }
}
