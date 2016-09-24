package filmeUtils.torrent.torrentSites;

import java.util.List;
import java.util.Optional;

import filmeUtils.Debug;
import webGrude.Browser;
import webGrude.annotations.Page;
import webGrude.annotations.Selector;

public class PirateBaySe implements TorrentSite {

    @Page("http://thepiratebay.se/search/{0}/0/7/0")
    public static class SearchResult {
        @Selector(value = "#searchResult tbody tr td a[href*=magnet]", attr = "href")
        public List<String> link;
    }

    @Override
    public Optional<String> getMagnetLinkFirstResult(final String exactFileName) {
        final SearchResult searchResult = Browser.get(SearchResult.class, exactFileName);
        final List<String> links = searchResult.link;
        this.printDebug(links);
        if (links.size() == 0) {
            return Optional.empty();
        }
        return Optional.of(links.get(0));
    }

    private void printDebug(final List<String> links) {
        Debug.log("_______________________________");
        Debug.log("Searching on");
        Debug.log(Browser.getCurentUrl());
        Debug.log(Browser.getCurentPageContents());
        Debug.log("Found:");
        links.forEach(s -> Debug.log(s.toString()));
        Debug.log("_______________________________");
    }

    @Override
    public String getSiteName() {
        return "PirateBay.se";
    }

}
