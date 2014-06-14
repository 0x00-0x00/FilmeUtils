package filmeUtils.torrent.torrentSites;

import filmeUtils.Debug;
import webGrude.Browser;
import webGrude.annotations.Page;
import webGrude.annotations.Selector;
import webGrude.elements.Link;

import java.util.List;

public class BitSnoop implements TorrentSite {

    @Page
    public static class SearchResultLinked {
        @Selector(value = "a.dl_mag2[href*=magnet]", attr = "href") public String magnetLink;
    }

    @Page("http://bitsnoop.com/search/all/{0}")
    public static class SearchResult {
        @Selector("#torrents li a") public List<Link<SearchResultLinked>> links;
    }

    @Override
	public String getMagnetLinkFirstResultOrNull(final String exactFileName){
        List<Link<SearchResultLinked>> links = Browser.open(SearchResult.class, exactFileName.replace('.',' ')).links;
        if(Debug.IS_DEBUG) {
            System.out.println("_______________________________");
            System.out.println("Searching on");
            System.out.println(Browser.getCurentUrl());

            System.out.println(Browser.getCurentPage());

            System.out.println("Found:");
            links.forEach(s -> System.out.println(s));
            System.out.println("_______________________________");
        }
        if(links.size()>0)
            return links.get(0).visit().magnetLink;
        return null;
	}

    @Override public String getSiteName() { return "BitSnoop"; }

}
