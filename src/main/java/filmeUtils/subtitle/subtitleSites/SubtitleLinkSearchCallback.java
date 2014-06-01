package filmeUtils.subtitle.subtitleSites;

@FunctionalInterface
public interface SubtitleLinkSearchCallback {
	void process(SubtitlePackageAndLink nameAndlink);
}
