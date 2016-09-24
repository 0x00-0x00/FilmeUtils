package filmeUtils.torrent.torrentSites;

import java.util.List;
import java.util.Optional;

import filmeUtils.Debug;
import webGrude.Browser;
import webGrude.annotations.Page;
import webGrude.annotations.Selector;
import webGrude.elements.Link;

public class BitSnoop implements TorrentSite {

    @Page
    public static class SearchResultLinked {
        @Selector(value = "a.dl_mag2[href*=magnet]", attr = "href")
        public String magnetLink;
    }

    @Page("http://bitsnoop.com/search/all/{0}")
    public static class SearchResult {
        @Selector("#torrents li a")
        public List<Link<SearchResultLinked>> links;
    }

    @Override
    public Optional<String> getMagnetLinkFirstResult(final String exactFileName) {

        final SearchResult searchResult = Browser.get(SearchResult.class, exactFileName.replace('.', ' '));
        final List<Link<SearchResultLinked>> links = searchResult.links;
        this.printDebug(links);
        if (links.size() == 0) {
            return Optional.empty();
        }
        return Optional.of(links.get(0).visit().magnetLink);
    }

    private void printDebug(final List<Link<SearchResultLinked>> links) {
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
        return "BitSnoop";
    }

}
