package filmeUtils.subtitle.subtitleSites.legendasTV;

import webGrude.Browser;
import webGrude.annotations.Page;
import webGrude.annotations.Selector;
import webGrude.elements.Link;

import java.util.List;

@Page("http://legendas.tv/util/carrega_legendas_busca/{0}/1/-/{1}")
public class Search {

    @Selector(".f_left p a[href^=/download") public List<String> links;

    @Selector(".load_more") public Link<Search> moreToLoad;

    public static Search search(String term) { return Browser.open(Search.class,term,"1"); }

    public boolean hasNext(){ return moreToLoad != null; }

    public Search next(){ return moreToLoad.visit(); }

}
