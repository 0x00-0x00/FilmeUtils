package filmeUtils;

import filmeUtils.subtitleSites.SubtitleAndLink;
import filmeUtils.subtitleSites.SubtitleLinkSearchCallback;



final class SearchListenerImplementation implements SubtitleLinkSearchCallback {
	
	private final OutputListener outputListener;
	private final FilmeUtilsOptions cli;
	private final Downloader downloader;
	private final boolean extractContents;

	SearchListenerImplementation(final Downloader downloader, final FilmeUtilsOptions cli, final OutputListener outputListener) {		
		this.downloader = downloader;
		this.cli = cli;
		this.outputListener = outputListener;
		this.extractContents = cli.getSubtitlesDestinationFolderOrNull()!= null;
	}

	public boolean processAndReturnIfMatches(final SubtitleAndLink subAndLink) {
		String name = subAndLink.name;
		String link = subAndLink.link;
		if(shouldExtractSubtitles()){
			return downloader.download(name, link, cli);
		}else{			
			final boolean shouldRefuse = shouldRefuseSubtitleFile(name);
			if (!shouldRefuse) {
				outputListener.out(name);
			}
			return true;
		}
	}

	private boolean shouldExtractSubtitles() {
		return extractContents;
	}

	private boolean shouldRefuseSubtitleFile(final String subtitleName) {
		return subtitleName.toLowerCase().matches(cli.subtitleRegex());
	}
}