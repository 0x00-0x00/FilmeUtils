package filmeUtils.subtitle.subtitleSites.legendasTV;

import java.util.List;
import java.util.stream.Collectors;

import filmeUtils.subtitle.subtitleSites.SubtitlePackageAndLink;
import webGrude.Browser;
import webGrude.annotations.AfterPageLoad;
import webGrude.annotations.Page;
import webGrude.annotations.Selector;

@Page("http://legendas.tv/util/carrega_destaques/todos/{0}")
public class NewerSubtitles {

    public static NewerSubtitles open() {
        return openOnPage(0);
    }

    private static NewerSubtitles openOnPage(final int page) {
        final NewerSubtitles newer = Browser.get(NewerSubtitles.class, Integer.toString(page));
        newer.currentPage = page;
        return newer;
    }

    @Selector(value = "div.film span.bt_seta_download a.texto", attr = "href")
    public List<String> novaLink;

    public int currentPage;

    private List<SubtitlePackageAndLink> sls;

    @AfterPageLoad
    public void after() {
        this.sls = this.novaLink.stream()
                .map(l -> new SubtitlePackageAndLink(l.replaceAll(".*/(.*)", "$1").replace("_", " "),
                        LegendasTv.getDownloadLink(l)))
                .collect(Collectors.toList());
    }

    public List<SubtitlePackageAndLink> getSubtitlePackageAndLink() {
        return this.sls;
    }

    public NewerSubtitles nextPage() {
        return openOnPage(this.currentPage + 1);
    }
}
