package filmeUtils.subtitle.subtitleSites.legendasTV;

import filmeUtils.subtitle.subtitleSites.SubtitlePackageAndLink;
import webGrude.Browser;
import webGrude.annotations.Page;
import webGrude.annotations.Selector;

import java.util.List;
import java.util.stream.Collectors;

@Page("http://legendas.tv/util/carrega_destaques/todos/{0}")
public class NewerSubtitles {

    public static NewerSubtitles open(){
        return Browser.open(NewerSubtitles.class, "1");
    }

    @Selector(value = "div.film span.bt_seta_download a.texto", attr = "href") public List<String> novaLink;

    @Selector("button.active") public Integer currentPage;

    public List<SubtitlePackageAndLink> getSubtitlePackageAndLink() {
        return novaLink
                .stream()
                .map(l -> new SubtitlePackageAndLink(l))
                .collect(Collectors.toList());
    }

    public NewerSubtitles nextPage() { return Browser.open(NewerSubtitles.class, nextPageAsString()); }

    private String nextPageAsString() { return Integer.toString(currentPage + 1); }
}
