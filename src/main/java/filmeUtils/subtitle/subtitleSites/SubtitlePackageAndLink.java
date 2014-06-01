package filmeUtils.subtitle.subtitleSites;

public class SubtitlePackageAndLink{
	public String name;
	public String link;

	public SubtitlePackageAndLink(final String name, final String link) {
		this.name = name;
		this.link = link;
	}
	
	@Override
	public String toString() {
		return name + " -> " + link ;
	}
}