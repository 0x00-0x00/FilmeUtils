package filmeUtils.subtitle.subtitleSites.legendasTV;

import filmeUtils.subtitle.subtitleSites.SubtitlePackageAndLink;
import webGrude.Browser;
import webGrude.annotations.AfterPageLoad;
import webGrude.annotations.Page;
import webGrude.annotations.Selector;
import webGrude.elements.Link;

import java.util.List;
import java.util.stream.Collectors;

@Page("http://legendas.tv/util/carrega_legendas_busca/{0}/1/-/{1}")
public class Search {

    @Page
    public static class SearchLink{
        @Selector("a") public String text;
        @Selector(value = "a",attr = "href") public String href;

        public SubtitlePackageAndLink getSubtitlePackageAndLink(){
            return new SubtitlePackageAndLink(
                    text.replace(".", " "),
                    LegendasTv.getDownloadLink(href)
            );
        }
    }

    @Selector(".f_left p a[href^=/download") public List<SearchLink> links;

    @Selector(".load_more") public Link<Search> moreToLoad;

    private List<SubtitlePackageAndLink> sls;

    public static Search search(String term) { return Browser.open(Search.class,term,"1"); }

    public List<SubtitlePackageAndLink> getSubtitlePackageAndLink() {
        return sls;
    }

    @AfterPageLoad
    public void after() {
        sls =  links
                .stream()
                .map(l -> l.getSubtitlePackageAndLink())
                .collect(Collectors.toList());
    }

    public boolean hasNext(){ return moreToLoad != null; }

    public Search next(){ return moreToLoad.visit(); }

}
