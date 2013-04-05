package filmeUtils.commons;

import filmeUtils.subtitle.subtitleSites.SubtitlePackageAndLink;
import filmeUtils.subtitle.subtitleSites.SubtitleLinkSearchCallback;

public final class PrintSubtitlesZipNames implements SubtitleLinkSearchCallback {
	
	private final OutputListener outputListener;

	public PrintSubtitlesZipNames(final OutputListener outputListener) {		
		this.outputListener = outputListener;
	}

	public void process(final SubtitlePackageAndLink subAndLink) {
		String name = subAndLink.name;
		outputListener.out(name);
	}
}