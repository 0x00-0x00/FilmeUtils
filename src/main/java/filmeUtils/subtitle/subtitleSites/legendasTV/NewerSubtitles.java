package filmeUtils.subtitle.subtitleSites.legendasTV;

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

    @Selector(value = "div.film span.bt_seta_download a.texto", attr = "href")
    public List<String> novaLink;

    @Selector("button.active")
    public String currentPage;

    public List<SubtitleLink> getSubtitlePackageAndLink() {
        return novaLink
                .stream()
                .map(l -> new SubtitleLink(l))
                .collect(Collectors.toList());
    }

    private String nextPageAsString() {
        return Integer.toString(Integer.valueOf(currentPage) + 1);
    }

    public NewerSubtitles nextPage() {
        return Browser.open(NewerSubtitles.class, nextPageAsString());
    }
}
