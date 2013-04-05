package filmeUtils.downloader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.ClientProtocolException;

import filmeUtils.commons.FileSystemUtils;
import filmeUtils.commons.OutputListener;
import filmeUtils.subtitle.Subtitle;
import filmeUtils.subtitle.subtitleSites.LegendasTv;
import filmeUtils.torrent.torrentSites.TorrentSearcher;
import filmeUtils.torrent.torrentSites.TorrentSearcherImpl;
import filmeUtils.utils.RegexForSubPackageAndSubFile;
import filmeUtils.utils.extraction.Extractor;
import filmeUtils.utils.fileSystem.FileSystem;
import filmeUtils.utils.http.OSMagnetLinkHandler;
import filmeUtils.utils.http.SimpleHttpClient;

public class Downloader {
	
	private static final String ZIP = "zip";
	private static final String RAR = "rar";
	private final Extractor extract;
	private final SimpleHttpClient httpclient;
	private final LegendasTv legendasTv;
	private OutputListener outputListener;
	
	public Downloader(final Extractor extract,final FileSystem fileSystem,final SimpleHttpClient httpclient, final LegendasTv legendasTv, final OutputListener outputListener) {
		this.httpclient = httpclient;
		this.legendasTv = legendasTv;
		this.extract = extract;
		this.setOutputListener(outputListener);
	}

	public void downloadFromNewest(final String zipRegex,final String subtitleRegex,File subtitlesDestinationFolder){
		final RegexForSubPackageAndSubFile regexForSubPackageAndSubFile = new RegexForSubPackageAndSubFile(zipRegex, subtitleRegex);
		downloadFromNewest(regexForSubPackageAndSubFile, subtitlesDestinationFolder);
	}
	
	public void downloadFromNewest(RegexForSubPackageAndSubFile regex, File destinantion) {
		List<RegexForSubPackageAndSubFile> regexes = new ArrayList<RegexForSubPackageAndSubFile>();
		regexes.add(regex);
		downloadFromNewest(regexes, destinantion);
	}
	
	public void downloadFromNewest(List<RegexForSubPackageAndSubFile> regexes, File destinantion) {
		Subtitle subtitle = new Subtitle(outputListener,httpclient,legendasTv);
		File tmpFolder = createTempDir();
		subtitle.downloadNewer(tmpFolder, regexes);
		searchTorrentsForSubtitlesOnFolderAndCopySubtitlesThatHaveTorrentToDestFolderThenDeleteSourceFolder(tmpFolder, destinantion);
	}

	public void download(final String searchTerm, File subtitlesDestinationFolder,final String subtitleRegex){
		Subtitle subtitle = new Subtitle(outputListener,httpclient,legendasTv);
		File tmpFolder = createTempDir();
		subtitle.download(searchTerm, subtitleRegex, tmpFolder);
		searchTorrentsForSubtitlesOnFolderAndCopySubtitlesThatHaveTorrentToDestFolderThenDeleteSourceFolder(tmpFolder, subtitlesDestinationFolder);
	}

	public boolean downloadWithKnownLink(final String name,final String link,final String subtitleRegex, final File subtitlesDestinationFolder){
		File temporaryFolder = createTempDir();
		downloadLinkToTempDir(name,link,temporaryFolder);
		boolean success = searchTorrentsForSubtitlesOnFolderAndCopySubtitlesThatHaveTorrentToDestFolderThenDeleteSourceFolder(temporaryFolder, subtitlesDestinationFolder);
		return success;
	}

	public void setOutputListener(final OutputListener outputListener) {
		this.outputListener = outputListener;
	}

	private File createTempDir() {
		File tmpFolder;
		try {
			tmpFolder = File.createTempFile("FilmeUtils", "FilmeUtils");
			tmpFolder.delete();
			tmpFolder.mkdir();
		} catch (IOException e) {throw new RuntimeException(e);}
		return tmpFolder;
	}

	private boolean searchTorrentsForSubtitlesOnFolderAndCopySubtitlesThatHaveTorrentToDestFolderThenDeleteSourceFolder(File tmpFolder, File subtitlesDestinationFolder) {
		boolean success = false;
		TorrentSearcher torrentSearcher = new TorrentSearcherImpl(httpclient);
		final Iterator<File> iterateFiles = FileUtils.iterateFiles(tmpFolder, new String[]{"srt"}, true);
		while(iterateFiles.hasNext()){
			final File subtitleFile = iterateFiles.next();
			final String filenameWithoutExtension = FilenameUtils.removeExtension(subtitleFile.getName());
			final String magnetLinkForTermOrNull = torrentSearcher.getMagnetLinkForTermOrNull(filenameWithoutExtension, outputListener);
			if(magnetLinkForTermOrNull != null){
				downloadMagnetLink(magnetLinkForTermOrNull);
				try {
					outputListener.out("Salvando legenda em "+subtitlesDestinationFolder.getAbsolutePath());
					FileUtils.copyFile(subtitleFile, new File(subtitlesDestinationFolder,subtitleFile.getName()));
					success = true;
				} catch (IOException e) {throw new RuntimeException(e);}
			}
		}
		try {
			FileUtils.deleteDirectory(tmpFolder);
		} catch (IOException e) {throw new RuntimeException(e);}
		return success;
	}

	private void downloadMagnetLink(String magnetLink) {
		outputListener.outVerbose("Abrindo magnet link no cliente: "+magnetLink);
		new OSMagnetLinkHandler().openURL(magnetLink);
	}

	private void downloadLinkToTempDir(String name, String link, File folder) {
		try {
			downloadLinkToFolder(link, folder);
		} catch (Exception e) {
			outputListener.outVerbose("Erro fazendo download de legenda de "+link);
			FileSystemUtils filmeUtilsFolder = FileSystemUtils.getInstance();
			File errorFile = filmeUtilsFolder.writeErrorFile(e);
			outputListener.outVerbose("Mais detalhes em "+errorFile);
		}
	}

	private void downloadLinkToFolder(final String link, final File folder) throws IOException, ClientProtocolException, ZipException {
		String contentType = "";
		final File destFile = File.createTempFile("filmeUtils", "filmeUtils");
		
		while(isNotAFile(contentType)){
			outputListener.out("Nao esta logado, tentando logar...");
			legendasTv.login(); 
			destFile.delete();
			contentType = httpclient.getToFile(link, destFile);
		}
		extract(destFile, folder, contentType);
		destFile.delete();
	}

	private boolean isNotAFile(String contentType) {
		return contentType.contains("text/html") || contentType.isEmpty();
	}
	
	private void extract(final File compressedFile,final File destinationFolder, final String contentType)throws ZipException, IOException {
		outputListener.outVerbose("Extraindo legendas para "+destinationFolder.getAbsolutePath());
		boolean isRar = contentType.contains(RAR);
		boolean isZip = contentType.contains(ZIP);
		if(!isRar && !isZip)
			throw new RuntimeException("Tipo desconhecido: "+contentType);
		
		if(isRar){
			extract.unrar(compressedFile, destinationFolder);
		}
		if(isZip){
			extract.unzip(compressedFile, destinationFolder);
		}
		outputListener.outVerbose("Extraido com sucesso para "+destinationFolder.getAbsolutePath());
	}

}
