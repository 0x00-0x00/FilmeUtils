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
import filmeUtils.utils.http.URISchemeLinkHandlerImpl;
import filmeUtils.utils.http.SimpleHttpClient;

public class Downloader {
	
	private static final String ZIP = "zip";
	private static final String RAR = "rar";
	private final Extractor extract;
	private final SimpleHttpClient httpclient;
	private final LegendasTv legendasTv;
	private OutputListener outputListener;
	
	public Downloader(final Extractor extract,final SimpleHttpClient httpclient, final LegendasTv legendasTv, final OutputListener outputListener) {
		this.httpclient = httpclient;
		this.legendasTv = legendasTv;
		this.extract = extract;
		this.outputListener = outputListener;
	}

	public void downloadFromNewest(final String zipRegex,final String subtitleRegex,final File subtitlesDestinationFolder){
		final RegexForSubPackageAndSubFile regexForSubPackageAndSubFile = new RegexForSubPackageAndSubFile(zipRegex, subtitleRegex);
		downloadFromNewest(regexForSubPackageAndSubFile, subtitlesDestinationFolder);
	}
	
	public void downloadFromNewest(final RegexForSubPackageAndSubFile regex, final File destinantion) {
		final List<RegexForSubPackageAndSubFile> regexes = new ArrayList<RegexForSubPackageAndSubFile>();
		regexes.add(regex);
		downloadFromNewest(regexes, destinantion);
	}
	
	public void downloadFromNewest(final List<RegexForSubPackageAndSubFile> regexes, final File destinantion) {
		downloadFromNewest(regexes, destinantion, new ArrayList<String>());
	}
	
	public void downloadFromNewest(final List<RegexForSubPackageAndSubFile> regexes, final File destinantion, final List<String> subtitlesPackagesToIgnore) {		
		final Subtitle subtitle = getSubtitleDownloader();
		final File tmpFolder = createTempDir();
		subtitle.downloadNewer(tmpFolder, regexes, subtitlesPackagesToIgnore);
		searchTorrentsForSubtitlesOnFolderAndCopySubtitlesThatHaveTorrentToDestFolderThenDeleteSourceFolder(tmpFolder, destinantion);
	}

	public void download(final String searchTerm, final File subtitlesDestinationFolder,final String subtitleRegex){
		final Subtitle subtitle = getSubtitleDownloader();
		final File tmpFolder = createTempDir();
		subtitle.download(searchTerm, subtitleRegex, tmpFolder);
		final boolean success = searchTorrentsForSubtitlesOnFolderAndCopySubtitlesThatHaveTorrentToDestFolderThenDeleteSourceFolder(tmpFolder, subtitlesDestinationFolder);
		if(!success) outputListener.out("Nenhum torrent para nenhuma legenda encontrada");
	}

	public boolean downloadWithKnownLink(final String name,final String link,final String subtitleRegex, final File subtitlesDestinationFolder){
		final File temporaryFolder = createTempDir();
		downloadLinkToTempDir(name,link,temporaryFolder);
		final boolean success = searchTorrentsForSubtitlesOnFolderAndCopySubtitlesThatHaveTorrentToDestFolderThenDeleteSourceFolder(temporaryFolder, subtitlesDestinationFolder);
		return success;
	}

	public void setOutputListener(final OutputListener outputListener) {
		this.outputListener = outputListener;
	}

	private Subtitle getSubtitleDownloader() {
		final Subtitle subtitle = new Subtitle(outputListener,httpclient,legendasTv);
		return subtitle;
	}

	private File createTempDir() {
		File tmpFolder;
		try {
			tmpFolder = File.createTempFile("FilmeUtils", "FilmeUtils");
			tmpFolder.delete();
			tmpFolder.mkdir();
		} catch (final IOException e) {throw new RuntimeException(e);}
		return tmpFolder;
	}

	private boolean searchTorrentsForSubtitlesOnFolderAndCopySubtitlesThatHaveTorrentToDestFolderThenDeleteSourceFolder(final File tmpFolder, final File subtitlesDestinationFolder) {
		boolean success = false;
		final TorrentSearcher torrentSearcher = new TorrentSearcherImpl(httpclient);
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
				} catch (final IOException e) {throw new RuntimeException(e);}
			}else{
				outputListener.out("Legenda sem torrent não será salva "+filenameWithoutExtension);
			}
		}
		try {
			FileUtils.deleteDirectory(tmpFolder);
		} catch (final IOException e) {throw new RuntimeException(e);}
		return success;
	}

	private void downloadMagnetLink(final String magnetLink) {
		outputListener.outVerbose("Abrindo magnet link no cliente: "+magnetLink);
		new URISchemeLinkHandlerImpl().openURL(magnetLink);
	}

	private void downloadLinkToTempDir(final String name, final String link, final File folder) {
		try {
			downloadLinkToFolder(link, folder);
		} catch (final Exception e) {
			outputListener.outVerbose("Erro fazendo download de legenda de "+link);
			final FileSystemUtils filmeUtilsFolder = FileSystemUtils.getInstance();
			final File errorFile = filmeUtilsFolder.writeErrorFile(e);
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

	private boolean isNotAFile(final String contentType) {
		return contentType.contains("text/html") || contentType.isEmpty();
	}
	
	private void extract(final File compressedFile,final File destinationFolder, final String contentType)throws ZipException, IOException {
		outputListener.outVerbose("Extraindo legendas para "+destinationFolder.getAbsolutePath());
		final boolean isRar = contentType.contains(RAR);
		final boolean isZip = contentType.contains(ZIP);
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
