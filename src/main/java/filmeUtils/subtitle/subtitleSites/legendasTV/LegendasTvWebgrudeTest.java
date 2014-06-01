package filmeUtils.subtitle.subtitleSites.legendasTV;

import filmeUtils.subtitle.subtitleSites.SubtitlePackageAndLink;

public class LegendasTvWebgrudeTest {

    public static void main(String... args) {
        newer();
        search();
    }

    private static void search() {
        String term = "house m.d.";
        Search result = Search.search(term);
        result.links.forEach(l -> System.out.println(l));
        while(result.hasNext()){
            result = result.next();
            result.links.forEach(l -> System.out.println(l));
        }
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
