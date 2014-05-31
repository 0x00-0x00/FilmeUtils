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

	public String getMagnetLinkFirstResultOrNull(final String exactFileName) throws SiteOfflineException{
        SearchResult tpb = null;
        try {
            tpb = Browser.open(SearchResult.class, URLEncoder.encode(exactFileName, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return tpb.link.get(0);
	}

	@Override
	public String getSiteName() {
		return "PirateBay.se";
	}

    public static void main(String... args) throws SiteOfflineException {
        PirateBaySe pirateBaySe = new PirateBaySe();
        String ubuntu = pirateBaySe.getMagnetLinkFirstResultOrNull("ubuntu");
        System.out.println(ubuntu);
    }
	
}
