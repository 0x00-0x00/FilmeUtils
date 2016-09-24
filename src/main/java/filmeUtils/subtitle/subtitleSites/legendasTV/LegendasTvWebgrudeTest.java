package filmeUtils.subtitle.subtitleSites.legendasTV;

public class LegendasTvWebgrudeTest {

    public static void main(final String... args) {
        System.out.println("Newer");
        newer();
        System.out.println("##########################################");
        System.out.println("Search house");
        search();
    }

    private static void search() {
        final String term = "house m.d.";

        Search search = Search.search(term);
        search.getSubtitlePackageAndLink().forEach(System.out::println);
        while (search.hasNext()) {
            final Search next = search.next();
            next.getSubtitlePackageAndLink().forEach(System.out::println);
            search = next;
        }
    }

    private static void newer() {
        NewerSubtitles newerSubtitles = NewerSubtitles.open();
        while (newerSubtitles.currentPage <= 3) {
            System.out.println(newerSubtitles.currentPage);
            newerSubtitles.getSubtitlePackageAndLink().forEach(System.out::println);
            newerSubtitles = newerSubtitles.nextPage();
        }
    }
}
