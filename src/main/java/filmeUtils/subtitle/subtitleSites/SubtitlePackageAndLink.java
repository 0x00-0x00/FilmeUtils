package filmeUtils.subtitle.subtitleSites;

public class SubtitlePackageAndLink{
	public String name;
	public String link;

    public SubtitlePackageAndLink(final String linkUrl) {
        this.name = linkUrl.replaceAll(".*/(.*)", "$1").replace("_", " ");
        final String hash = linkUrl.replaceAll("/download/([0-9a-z]*)/.*", "$1");
        this.link = "http://legendas.tv/pages/downloadarquivo/" + hash;
    }

	public SubtitlePackageAndLink(final String name, final String link) {
		this.name = name;
		this.link = link;
	}
	
	@Override
	public String toString() {
		return name + " -> " + link ;
	}
}