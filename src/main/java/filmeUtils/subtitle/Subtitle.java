package filmeUtils.subtitle;

import java.io.File;
import java.io.IOException;
import java.util.List;

import filmeUtils.commons.FileSystemUtils;
import filmeUtils.commons.OutputListener;
import filmeUtils.subtitle.subtitleSites.LegendasTv;
import filmeUtils.subtitle.subtitleSites.SubtitleLinkSearchCallback;
import filmeUtils.subtitle.subtitleSites.SubtitlePackageAndLink;
import filmeUtils.utils.RegexForSubPackageAndSubFile;
import filmeUtils.utils.RegexUtils;
import filmeUtils.utils.extraction.ExtractorImpl;
import filmeUtils.utils.http.SimpleHttpClient;

public class Subtitle {
	
	private final LegendasTv legendasTv;
	private final OutputListener output;
	private final SimpleHttpClient httpclient;

	public Subtitle(final OutputListener output,final SimpleHttpClient httpclient,final LegendasTv legendasTv) {
		this.output = output;
		this.httpclient = httpclient;
		this.legendasTv = legendasTv;
	}

	public void search(String searchTerm) {
		search(searchTerm,".*");
	}
	
	public void search(String searchTerm, final String subtitleRegex) {
		legendasTv.search(searchTerm, new SubtitleLinkSearchCallback(){@Override public void process(SubtitlePackageAndLink nameAndlink) {
				output.out(nameAndlink.name);
				File tempDirWithSubtitles = downloadAndExtractToTempDir(nameAndlink.link);
				String[] subtitlesFilenames = tempDirWithSubtitles.list();
				for (String subtitlesFilename : subtitlesFilenames) {
					if(RegexUtils.matchesCaseInsensitive(subtitlesFilename, subtitleRegex))
						output.out(" -"+subtitlesFilename);
				}
		}});
	}

	public void download(String searchTerm, final File destDir) {
		download(searchTerm,".*",destDir);
	}
	
	public void downloadNewer(final File destDir,final List<RegexForSubPackageAndSubFile> regexes, final List<String> ignoredPackages) {
		SubtitleLinkSearchCallback searchListener = new SubtitleLinkSearchCallback() {	
			@Override
			public void process(SubtitlePackageAndLink nameAndlink) {
				String packageName = nameAndlink.name;
				if(ignoredPackages.contains(packageName)) return;
				final RegexForSubPackageAndSubFile regexMatchingPackageOrNull = RegexUtils.getRegexMatchingPackageOrNull(packageName,regexes);
				if(regexMatchingPackageOrNull == null) return;
				downloadSubtitlesMatchingRegexToDir(destDir, regexMatchingPackageOrNull.fileRegex , nameAndlink);
				FileSystemUtils.getInstance().addAlreadyDownloaded(packageName);
			}
		};
		legendasTv.getNewer(searchListener);
	}
	
	public void download(String searchTerm, final String subtitleRegex,final File destDir) {
		SubtitleLinkSearchCallback searchListener = new SubtitleLinkSearchCallback() {	
			@Override
			public void process(SubtitlePackageAndLink nameAndlink) {
				downloadSubtitlesMatchingRegexToDir(destDir, subtitleRegex,nameAndlink);
			}
		};
		legendasTv.search(searchTerm, searchListener);
	}

	private void downloadSubtitlesMatchingRegexToDir(final File destDir, final String subtitleRegex,final SubtitlePackageAndLink nameAndlink) {
		output.out("Abrindo zip de legendas "+nameAndlink.name);
		String link = nameAndlink.link;
		
		final File unzippedTempDestination = downloadAndExtractToTempDir(link);
		FileSystemUtils.copyFilesMatchingRegexAndDeleteSourceDir(unzippedTempDestination,destDir, subtitleRegex);
	}

	public void listNewSubtitles() {
		legendasTv.getNewer(new SubtitleLinkSearchCallback(){@Override public void process(SubtitlePackageAndLink nameAndlink) {
				output.out(nameAndlink.name);
		}});
	}

	private File downloadAndExtractToTempDir(String link) {
		final File unzippedTempDestination;
		try {
			final File zipTempDestination = File.createTempFile("Filmeutils", "Filmeutils");
			unzippedTempDestination = File.createTempFile("Filmeutils", "Filmeutils");
			unzippedTempDestination.delete();
			unzippedTempDestination.mkdir();
			String contentType = httpclient.getToFile(link, zipTempDestination);
			ExtractorImpl extractor = new ExtractorImpl();
			if(contentType.contains("rar")){
				extractor.unrar(zipTempDestination, unzippedTempDestination);
			}
			if(contentType.contains("zip")){
				extractor.unzip(zipTempDestination, unzippedTempDestination);
			}
			zipTempDestination.delete();
		}catch(IOException e){throw new RuntimeException(e);}
		
		return unzippedTempDestination;
	}

}
