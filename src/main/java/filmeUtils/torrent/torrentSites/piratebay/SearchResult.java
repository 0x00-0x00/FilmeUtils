package filmeUtils.torrent.torrentSites.piratebay;

import org.jsoup.nodes.Element;
import webGrude.Browser;
import webGrude.annotations.Page;
import webGrude.annotations.Selector;
import webGrude.elements.Link;

import java.util.List;

@Page("http://thepiratebay.se/search/ubuntu/0/7/0")
public class SearchResult {

    @Selector(value = "#searchResult tbody tr td a[href*=magnet]",attr = "href") public List<String> link;

    public static void main(String... args){
        SearchResult tpb = Browser.open(SearchResult.class);
        tpb.link.forEach( (e) -> System.out.println(e));
    }
}
