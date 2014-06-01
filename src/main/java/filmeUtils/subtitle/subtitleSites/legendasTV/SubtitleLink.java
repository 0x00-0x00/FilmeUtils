package filmeUtils.subtitle.subtitleSites.legendasTV;

public class SubtitleLink {
    public String name;
    public String link;

    public SubtitleLink(final String linkUrl) {
        this.name = linkUrl.replaceAll(".*/(.*)", "$1").replace("_", " ");
        final String hash = linkUrl.replaceAll("/download/([0-9a-z]*)/.*", "$1");
        this.link = "http://legendas.tv/pages/downloadarquivo/" + hash;
    }
}