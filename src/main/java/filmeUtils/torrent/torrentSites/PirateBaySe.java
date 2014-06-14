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

        Debug.log("_______________________________");
        Debug.log("Searching on");
        Debug.log(Browser.getCurentUrl());
        Debug.log(Browser.getCurentPage());
        Debug.log("Found:");
        links.forEach(s -> Debug.log(s.toString()));
        Debug.log("_______________________________");

        if(links.size() > 0)
            return  links.get(0);
        return  null;
	}

	@Override
	public String getSiteName() {
		return "PirateBay.se";
	}
	
}
