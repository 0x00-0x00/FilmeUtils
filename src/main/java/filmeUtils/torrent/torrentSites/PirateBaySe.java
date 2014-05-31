package filmeUtils.torrent.torrentSites;


import webGrude.Browser;
import webGrude.annotations.Page;
import webGrude.annotations.Selector;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class PirateBaySe implements TorrentSite {

    @Page("http://thepiratebay.se/search/{0}/0/7/0")
    public static class SearchResult {
        @Selector(value = "#searchResult tbody tr td a[href*=magnet]", attr = "href") public List<String> link;
    }

	public String getMagnetLinkFirstResultOrNull(final String exactFileName){
        return  Browser.open(SearchResult.class, exactFileName).link.get(0);
	}

	@Override
	public String getSiteName() {
		return "PirateBay.se";
	}
	
}
