package filmeUtils.downloader;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.zip.ZipException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.ClientProtocolException;

import filmeUtils.commons.FilmeUtilsFolder;
import filmeUtils.commons.OutputListener;
import filmeUtils.subtitle.Subtitle;
import filmeUtils.subtitle.subtitleSites.LegendasTv;
import filmeUtils.torrent.torrentSites.TorrentSearcher;
import filmeUtils.torrent.torrentSites.TorrentSearcherImpl;
import filmeUtils.utils.RegexUtils;
import filmeUtils.utils.extraction.Extractor;
import filmeUtils.utils.fileSystem.FileSystem;
import filmeUtils.utils.http.MagnetLinkHandler;
import filmeUtils.utils.http.SimpleHttpClient;

public class Downloader {
	
	private static final String ZIP = "zip";
	private static final String RAR = "rar";
	private final Extractor extract;
	private final SimpleHttpClient httpclient;
	private final TorrentSearcher torrentSearcher;
	private final LegendasTv legendasTv;
	private final MagnetLinkHandler magnetLinkHandler;
	private final FileSystem fileSystem;
	private OutputListener outputListener;
	private File temporarySubtitleFolder;
	private File subtitlesDestinationFolder;
	
	public Downloader(final Extractor extract,final FileSystem fileSystem,final SimpleHttpClient httpclient,final TorrentSearcher torrentSearcher,final MagnetLinkHandler magnetLinkHandler,final LegendasTv legendasTv, final OutputListener outputListener) {
		this.fileSystem = fileSystem;
		this.httpclient = httpclient;
		this.torrentSearcher = torrentSearcher;
		this.magnetLinkHandler = magnetLinkHandler;
		this.legendasTv = legendasTv;
		this.extract = extract;
		this.setOutputListener(outputListener);
	}

	public void downloadFromNewest(final String zipRegex,final String subtitleRegex,File subtitlesDestinationFolder){
		Subtitle subtitle = new Subtitle(outputListener,httpclient,legendasTv);
		File tmpFolder = createTempDir();
		subtitle.downloadNewer(tmpFolder, zipRegex, subtitleRegex);
		copySubtitlesThatHaveTorrentToDir(tmpFolder, subtitlesDestinationFolder);
	}
	
	public void download(final String searchTerm, File subtitlesDestinationFolder,final String subtitleRegex){
		Subtitle subtitle = new Subtitle(outputListener,httpclient,legendasTv);
		File tmpFolder = createTempDir();
		subtitle.download(searchTerm, tmpFolder, subtitleRegex);
		copySubtitlesThatHaveTorrentToDir(tmpFolder, subtitlesDestinationFolder);
	}

	public boolean download(final String name,final String link,File subtitlesDestinationFolder,final String subtitleRegex){
		this.subtitlesDestinationFolder = subtitlesDestinationFolder;
		createTemporaryFolderForHandlingFiles(name);
		downloadLinkToTempDir(name,link);
		boolean success = downloadTorrentsForSubtitles(subtitleRegex);
		try {
			copySubtitlesToSubtitlesFolder();
		} catch (IOException e) {throw new RuntimeException(e);}
		deleteTemporaryDir();
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

	private void copySubtitlesThatHaveTorrentToDir(File tmpFolder,
			File subtitlesDestinationFolder) {
		TorrentSearcher torrentSearcher = new TorrentSearcherImpl(httpclient);
		File[] subtitles = tmpFolder.listFiles();
		for (File subtitleFile : subtitles) {
			final String filenameWithoutExtension = FilenameUtils.removeExtension(subtitleFile.getName());
			final String magnetLinkForTermOrNull = torrentSearcher.getMagnetLinkForTermOrNull(filenameWithoutExtension, outputListener);
			if(magnetLinkForTermOrNull != null){
				downloadMagnetLink(magnetLinkForTermOrNull);
				try {
					FileUtils.copyFile(subtitleFile, new File(subtitlesDestinationFolder,subtitleFile.getName()));
				} catch (IOException e) {throw new RuntimeException(e);}
			}
		}
		try {
			FileUtils.deleteDirectory(tmpFolder);
		} catch (IOException e) {throw new RuntimeException(e);}
	}

	private void downloadMagnetLink(String magnetLink) {
		magnetLinkHandler.openURL(magnetLink);
	}
	
	private void copySubtitlesToSubtitlesFolder() throws IOException {
		final Iterator<File> iterateFiles = FileUtils.iterateFiles(temporarySubtitleFolder, new String[]{"srt"}, true);
		while(iterateFiles.hasNext()){
			final File subtitleFile = iterateFiles.next();		
			FileUtils.copyFileToDirectory(subtitleFile, subtitlesDestinationFolder);
		}
	}

	private void deleteTemporaryDir() {
		try {
			FileUtils.deleteDirectory(temporarySubtitleFolder);
		}catch(IOException e){/*don't really care*/}
	}

	private void createTemporaryFolderForHandlingFiles(final String name) {
		final String tempDir = System.getProperty("java.io.tmpdir");
		final String validNameForFile = name.replaceAll("[/ \\\\?]", "_");
		temporarySubtitleFolder = new File(tempDir,validNameForFile);
		fileSystem.mkdir(temporarySubtitleFolder);
	}

	private void downloadLinkToTempDir(String name, String link) {
		try {
			downloadLinkToFolder(link, temporarySubtitleFolder);
		} catch (Exception e) {
			outputListener.outVerbose("Erro fazendo download de legenda de "+link);
			FilmeUtilsFolder filmeUtilsFolder = FilmeUtilsFolder.getInstance();
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

	private boolean downloadTorrentsForSubtitles(final String subtitleRegex) {
		boolean successfull = false;
		final Iterator<File> iterateFiles = FileUtils.iterateFiles(temporarySubtitleFolder, new String[]{"srt"}, true);
		while(iterateFiles.hasNext()){
			final File next = iterateFiles.next();
			final boolean success = downloadTorrent(next,subtitleRegex);
			successfull = successfull || success;
		}
		return successfull;
	}
	
	private void extract(final File compressedFile,final File destinationFolder, final String contentType)throws ZipException, IOException {
		outputListener.outVerbose("Extraindo legendas para "+temporarySubtitleFolder.getAbsolutePath());
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
		outputListener.outVerbose("Extraido com sucesso para "+temporarySubtitleFolder.getAbsolutePath());
	}

	private boolean downloadTorrent(final File subtitleFile, final String subtitleRegex) {
		String magnetLinkForFile;
		final String subtitleName = subtitleFile.getName().replaceAll("\\.[Ss][Rr][Tt]", "");
		final boolean shouldRefuse = shouldRefuseSubtitleFile(subtitleName, subtitleRegex);
		if(shouldRefuse){
			return false;
		}
		outputListener.outVerbose("Procurando melhor torrent para "+subtitleName);
		magnetLinkForFile = torrentSearcher.getMagnetLinkForTermOrNull(subtitleName,outputListener);
		if(magnetLinkForFile == null){
			outputListener.outVerbose("Nenhum torrent para "+subtitleName);
			subtitleFile.delete();
			return false;
		}
		outputListener.outVerbose("Abrindo magnet link no cliente: "+magnetLinkForFile);
		magnetLinkHandler.openURL(magnetLinkForFile);
		outputListener.out("Magnet link '"+magnetLinkForFile+"' de "+subtitleName+" enviado ao client de torrent.");
		
		return true;
	}
	
	private boolean shouldRefuseSubtitleFile(final String subtitleName, final String subtitlePattern) {
		return !RegexUtils.matchesCaseInsensitive(subtitleName, subtitlePattern);
	}

}
