package filmeUtils.torrent.torrentSites;


import filmeUtils.Debug;
import filmeUtils.commons.VerboseSysOut;
import webGrude.Browser;
import webGrude.annotations.Page;
import webGrude.annotations.Selector;

import java.util.List;

public class PirateBaySe implements TorrentSite {

    @Page("http://thepiratebay.se/search/{0}/0/7/0")
    public static class SearchResult {
        @Selector(value = "#searchResult tbody tr td a[href*=magnet]", attr = "href") public List<String> link;
    }

	public String getMagnetLinkFirstResultOrNull(final String exactFileName){
        List<String> links = Browser.open(SearchResult.class, exactFileName).link;
        if(Debug.IS_DEBUG) {
            System.out.println("_______________________________");
            System.out.println("Searching on");
            System.out.println(Browser.getCurentUrl());

            System.out.println(Browser.getCurentPage());

            System.out.println("Found:");
            links.forEach(s -> System.out.println(s));
            System.out.println("_______________________________");
        }
        if(links.size() > 0)
            return  links.get(0);
        return  null;
	}

	@Override
	public String getSiteName() {
		return "PirateBay.se";
	}
	
}
