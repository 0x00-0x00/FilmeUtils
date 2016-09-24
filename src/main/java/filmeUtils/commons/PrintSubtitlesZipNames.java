package filmeUtils.commons;

import filmeUtils.subtitle.subtitleSites.SubtitleLinkSearchCallback;
import filmeUtils.subtitle.subtitleSites.SubtitlePackageAndLink;

public final class PrintSubtitlesZipNames implements SubtitleLinkSearchCallback {

    private final OutputListener outputListener;

    public PrintSubtitlesZipNames(final OutputListener outputListener) {
        this.outputListener = outputListener;
    }

    @Override
    public void process(final SubtitlePackageAndLink subAndLink) {
        final String name = subAndLink.name;
        outputListener.out(name);
    }
}