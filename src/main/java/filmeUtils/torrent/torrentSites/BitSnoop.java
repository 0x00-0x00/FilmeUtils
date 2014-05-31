package filmeUtils.torrent.torrentSites;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import filmeUtils.utils.http.SimpleHttpClient;
import webGrude.Browser;
import webGrude.annotations.Page;
import webGrude.annotations.Selector;
import webGrude.elements.Link;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class BitSnoop implements TorrentSite {

    @Page
    public static class SearchResultLinked {
        @Selector(value = "a.dl_mag2[href*=magnet]", attr = "href") public String magnetLink;
    }

    @Page("http://bitsnoop.com/search/all/{0}")
    public static class SearchResult {
        @Selector("#torrents li a") public List<Link<SearchResultLinked>> link;
    }

    @Override
	public String getMagnetLinkFirstResultOrNull(final String exactFileName){
        return Browser.open(SearchResult.class, exactFileName).link.get(0).visit().magnetLink;
	}

    @Override public String getSiteName() { return "BitSnoop"; }

}
