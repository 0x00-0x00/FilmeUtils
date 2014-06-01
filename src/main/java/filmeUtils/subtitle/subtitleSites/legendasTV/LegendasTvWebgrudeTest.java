package filmeUtils.subtitle.subtitleSites.legendasTV;

import webGrude.Browser;

public class LegendasTvWebgrudeTest {

    public static void main(String... args) {
        //newer();
        search();
    }

    private static void search() {
        Search result = Search.search("house");
        result.links.forEach(l -> System.out.println(l));
    }

    private static void newer() {
        NewerSubtitles newerSubtitles = NewerSubtitles.open();
        newerSubtitles = newerSubtitles.nextPage();
        System.out.println(newerSubtitles.currentPage);
        for (SubtitleLink subtitlePackageAndLink : newerSubtitles.getSubtitlePackageAndLink()) {
            System.out.println("name " + subtitlePackageAndLink.name);
            System.out.println("link " + subtitlePackageAndLink.link);
        }
    }
}
