package filmeUtils.subtitle.subtitleSites.legendasTV;

import java.util.List;
import java.util.stream.Collectors;

import filmeUtils.subtitle.subtitleSites.SubtitlePackageAndLink;
import webGrude.Browser;
import webGrude.annotations.AfterPageLoad;
import webGrude.annotations.Page;
import webGrude.annotations.Selector;
import webGrude.elements.Link;

@Page("http://legendas.tv/util/carrega_legendas_busca/{0}/1/-/{1}")
public class Search {

    @Page
    public static class SearchLink {
        @Selector("a")
        public String text;
        @Selector(value = "a", attr = "href")
        public String href;

        public SubtitlePackageAndLink getSubtitlePackageAndLink() {
            return new SubtitlePackageAndLink(this.text.replace(".", " "), LegendasTv.getDownloadLink(this.href));
        }
    }

    @Selector(".f_left p a[href^=/download")
    public List<SearchLink> links;

    @Selector(".load_more")
    public Link<Search> moreToLoad;

    private List<SubtitlePackageAndLink> sls;

    public static Search search(final String term) {
        return Browser.get(Search.class, term, "1");
    }

    public List<SubtitlePackageAndLink> getSubtitlePackageAndLink() {
        return this.sls;
    }

    @AfterPageLoad
    public void after() {
        this.sls = this.links.stream().map(l -> l.getSubtitlePackageAndLink()).collect(Collectors.toList());
    }

    public boolean hasNext() {
        return this.moreToLoad != null;
    }

    public Search next() {
        return this.moreToLoad.visit();
    }

}
