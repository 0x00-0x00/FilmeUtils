package filmeUtils.subtitle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import filmeUtils.commons.FileSystemUtils;
import filmeUtils.commons.OutputListener;
import filmeUtils.subtitle.subtitleSites.SubtitleLinkSearchCallback;
import filmeUtils.subtitle.subtitleSites.SubtitlePackageAndLink;
import filmeUtils.subtitle.subtitleSites.legendasTV.LegendasTv;
import filmeUtils.utils.RegexForSubPackageAndSubFile;
import filmeUtils.utils.RegexUtils;
import filmeUtils.utils.extraction.ExtractorImpl;
import filmeUtils.utils.http.FilmeUtilsHttpClient;

public class Subtitle {
	
	private final LegendasTv legendasTv;
	private OutputListener output;

	public Subtitle(final OutputListener output,final LegendasTv legendasTv) {
		this.output = output;
		this.legendasTv = legendasTv;
	}

	public void search(final String searchTerm) {
		search(searchTerm,".*");
	}
	
	public void search(final String searchTerm, final String subtitleRegex) {
		legendasTv.search(
				searchTerm, 
				nameAndlink -> {
					output.out(nameAndlink.name);
					final File tempDirWithSubtitles = downloadAndExtractToTempDirReturnUnzippedDir(nameAndlink.link);
					final String[] subtitlesFilenames = tempDirWithSubtitles.list();
					for (final String subtitlesFilename : subtitlesFilenames) {
						if(RegexUtils.matchesCaseInsensitive(subtitlesFilename, subtitleRegex))
							output.out(" -"+subtitlesFilename);
					}
				}
		);
	}

	public void download(final String searchTerm, final File destDir) {
		download(searchTerm,".*",destDir);
	}
	
	public void downloadNewer(final File destDir,final List<RegexForSubPackageAndSubFile> regexes, final List<String> ignoredPackages,final Map<String,List<String>> outSuccessfullPackagesFiles) {
		legendasTv.getNewer(nameAndlink -> {
				final String packageName = nameAndlink.name;
				if(ignoredPackages.contains(packageName)) return;
				final RegexForSubPackageAndSubFile regexMatchingPackageOrNull = RegexUtils.getRegexMatchingPackageOrNull(packageName,regexes);
				if(regexMatchingPackageOrNull == null) return;
                List<String> outDownloadedSubtitles = downloadSubtitlesMatchingRegexToDir(destDir, regexMatchingPackageOrNull.fileRegex , nameAndlink);
                outSuccessfullPackagesFiles.put(packageName, outDownloadedSubtitles);
		});
	}
	
	public void download(final String searchTerm, final String subtitleRegex,final File destDir) {
		legendasTv.search(searchTerm, nameAndlink -> downloadSubtitlesMatchingRegexToDir(destDir, subtitleRegex,nameAndlink));
	}

	private List<String> downloadSubtitlesMatchingRegexToDir(final File destDir, final String subtitleRegex,final SubtitlePackageAndLink nameAndlink) {
        final List<String> outDownloadedSubtitles = new ArrayList<>();
		output.out("Fazendo download de pacote de legendas "+nameAndlink.name);
		final String link = nameAndlink.link;
		final File unzippedTempDestination = downloadAndExtractToTempDirReturnUnzippedDir(link);		
		final List<String> filesThatMatches = FileSystemUtils.copyFilesMatchingRegexAndDeleteSourceDir(unzippedTempDestination, destDir, subtitleRegex);
		filesThatMatches.forEach( file -> {
            outDownloadedSubtitles.add(file);
			output.out("Legenda "+file+" copiada para "+destDir.getAbsolutePath());
		});
        return outDownloadedSubtitles;
	}

	public void listNewSubtitles() {
		SubtitleLinkSearchCallback searchListener = nameAndlink -> output.out(nameAndlink.name);
		listNewSubtitles(searchListener);
	}

	public void listNewSubtitles(SubtitleLinkSearchCallback searchListener) {
		legendasTv.getNewer(searchListener );
	}

	private File downloadAndExtractToTempDirReturnUnzippedDir(final String link) {
		final File unzippedTempDestination;
		try {
			final File zipTempDestination = File.createTempFile("Filmeutils", "Filmeutils");
			unzippedTempDestination = File.createTempFile("Filmeutils", "Filmeutils");
			unzippedTempDestination.delete();
			unzippedTempDestination.mkdir();
			FilmeUtilsHttpClient.getToFile(link, zipTempDestination);
			output.out("Download de pacote de legendas de "+link+" para "+zipTempDestination+" terminado.\nVerificando tipo de arquivo...");
			final ExtractorImpl extractor = new ExtractorImpl();

			try {
				extractor.unrar(zipTempDestination, unzippedTempDestination);
				output.out("Arquivo rar.");
			} catch (Exception e1) {
				try {
					extractor.unzip(zipTempDestination, unzippedTempDestination);
					output.out("Arquivo zip.");
				} catch (Exception e2) {
					output.out("Arquivo inválido, zip ou rar esperado");
					FileInputStream fileInputStream = new FileInputStream(zipTempDestination);
					try{
						String fileContents = IOUtils.toString(fileInputStream);
						output.out("Conteúdo do arquivo");
						output.out(fileContents);
					}finally{
						fileInputStream.close();
					}
					
					return unzippedTempDestination;
				}
			}

			output.out("Pacote de legendas descompactado.");
			zipTempDestination.delete();
		}catch(final IOException e){throw new RuntimeException(e);}
		
		return unzippedTempDestination;
	}

	public void setOutputListener(final OutputListener outputListener) {
		output = outputListener;
	}

}
