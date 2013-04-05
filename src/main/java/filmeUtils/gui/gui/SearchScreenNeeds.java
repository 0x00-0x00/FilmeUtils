package filmeUtils.gui.gui;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import filmeUtils.commons.FileSystemUtils;
import filmeUtils.commons.OutputListener;
import filmeUtils.downloader.Downloader;
import filmeUtils.subtitle.subtitleSites.LegendasTv;
import filmeUtils.subtitle.subtitleSites.SubtitlePackageAndLink;
import filmeUtils.subtitle.subtitleSites.SubtitleLinkSearchCallback;

public class SearchScreenNeeds {

	private static final String LOW   = "Resolu\u00E7\u00E3o normal";
	private static final String HIGH  = "Alta defini\u00E7\u00E3o";
	private static final String ALL   = "Todas as resolu\u00E7\u00F5es";
	private String regex = ".*";
	private final LegendasTv legendasTv;
	private final Downloader downloader;

	public SearchScreenNeeds(final LegendasTv legendasTv, final Downloader downloader) {
		this.legendasTv = legendasTv;
		this.downloader = downloader;
	}
	
	public void download(final String item, final DownloadCallback callback) {
		new Thread(){
			@Override
			public void run() {
				final AtomicBoolean torrentWasFound = new AtomicBoolean(false);
				legendasTv.search(item, new SubtitleLinkSearchCallback() {
					@Override
					public void process(final SubtitlePackageAndLink subAndLink) {
						String name = subAndLink.name;
						String link = subAndLink.link;
						final boolean success = downloader.downloadWithKnownLink(name, link,regex, FileSystemUtils.getInstance().getSubtitlesDestination());
						torrentWasFound.set(success);
					}
				});
				callback.done(torrentWasFound.get());
			}
		}.start();
	}

	public void setSubtitleFolder(final File folder) {
		FileSystemUtils.getInstance().setSubtitleDestinationFolder(folder.getAbsolutePath());
	}

	public String getSubtitleFolder() {
		return FileSystemUtils.getInstance().getSubtitlesDestination().getAbsolutePath();
	}

	public void getNewAddsList(final GUISearchCallback callback) {
		
		new Thread(){
			@Override
			public void run() {				
				legendasTv.getNewer(new SubtitleLinkSearchCallback(){
					@Override
					public void process(final SubtitlePackageAndLink subAndLink) {
						String name = subAndLink.name;
						callback.found(name);
					}
				});
				callback.done();
			};
		}.start();
	}

	public void getResultsFor(final String text, final GUISearchCallback callback) {
		
		new Thread(){
			@Override
			public void run() {				
				legendasTv.search(text, new SubtitleLinkSearchCallback(){
					@Override
					public void process(final SubtitlePackageAndLink subAndLink) {
						String name = subAndLink.name;
						callback.found(name);
					}
					
				});
				callback.done();
			}
		}.start();
	}

	public void setResolution(final String resolution) {
		if(resolution.equals(ALL)){
			regex = ".*";
		}
		if(resolution.equals(HIGH)){
			regex = ".*(720|1080).*";
		}
		if(resolution.equals(LOW)){
			regex = "^((?!(720|1080)).)*$";
		}
	}

	public String allResolutionsString() {
		return ALL;
	}

	public String highResolutionString() {
		return HIGH;
	}

	public String lowResolutionString() {
		return LOW;
	}

	public void setOutputListener(final OutputListener outputListener) {
		downloader.setOutputListener(outputListener);
	}

}
