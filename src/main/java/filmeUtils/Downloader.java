package filmeUtils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.zip.ZipException;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.ClientProtocolException;

import filmeUtils.extraction.Extractor;
import filmeUtils.fileSystem.FileSystem;
import filmeUtils.http.MagnetLinkHandler;
import filmeUtils.http.SimpleHttpClient;
import filmeUtils.subtitleSites.LegendasTv;
import filmeUtils.torrentSites.TorrentSearcher;

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
	
	public boolean download(String name, String link, FilmeUtilsOptions options) {
		return download(name, link,options.getSubtitlesDestinationFolderOrNull(),options.isLazy(),options.subtitleRegex());
	}
	
	public boolean download(String name, String link, boolean stopOnFirstSuccesfullTorrent, String regex) {
		throw new RuntimeException("NOT IMPLEMENTED");
	}

	public boolean download(final String name,final String link,File subtitlesDestinationFolder,boolean stopOnFirstSuccesfullTorrent,final String subtitleRegex){
		this.subtitlesDestinationFolder = subtitlesDestinationFolder;
		createTemporaryFolderForHandlingFiles(name);
		downloadSubtitlesToTempDir(name,link);
		boolean success = downloadTorrentsForSubtitles(stopOnFirstSuccesfullTorrent,subtitleRegex);
		try {
			copySubtitlesToSubtitlesFolder();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		deleteTemporaryDir();
		return success;
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
		} catch (IOException e) {
			//don't really care
		}
	}

	private void createTemporaryFolderForHandlingFiles(final String name) {
		final String tempDir = System.getProperty("java.io.tmpdir");
		final String validNameForFile = name.replaceAll("[/ \\\\?]", "_");
		temporarySubtitleFolder = new File(tempDir,validNameForFile);
		fileSystem.mkdir(temporarySubtitleFolder);
	}

	private void downloadSubtitlesToTempDir(String name, String link) {
		try {
			downloadLinkToFolder(link, temporarySubtitleFolder);
		} catch (Exception e) {
			getOutputListener().outVerbose("Erro fazendo download de legenda de "+link);
			FilmeUtilsFolder filmeUtilsFolder = FilmeUtilsFolder.getInstance();
			File errorFile = filmeUtilsFolder.writeErrorFile(e);
			getOutputListener().outVerbose("Mais detalhes em "+errorFile);
		}
	}

	private void downloadLinkToFolder(final String link, final File folder) throws IOException, ClientProtocolException, ZipException {
		String contentType = "";
		final File destFile = File.createTempFile("filmeUtils", "filmeUtils");
		
		while(isNotAFile(contentType)){
			getOutputListener().out("Nao esta logado, tentando logar...");
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

	private boolean downloadTorrentsForSubtitles(final boolean stopOnFirstSuccesfullTorrent,final String subtitleRegex) {
		boolean successfull = false;
		final Iterator<File> iterateFiles = FileUtils.iterateFiles(temporarySubtitleFolder, new String[]{"srt"}, true);
		while(iterateFiles.hasNext()){
			final File next = iterateFiles.next();
			final boolean success = downloadTorrent(next,subtitleRegex);
			if(stopOnFirstSuccesfullTorrent && success){
				return true;
			}
			successfull = successfull || success;
		}
		return successfull;
	}
	
	private void extract(final File compressedFile,final File destinationFolder, final String contentType)throws ZipException, IOException {
		getOutputListener().outVerbose("Extraindo legendas para "+temporarySubtitleFolder.getAbsolutePath());
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
		getOutputListener().outVerbose("Extraido com sucesso para "+temporarySubtitleFolder.getAbsolutePath());
	}

	private boolean downloadTorrent(final File subtitleFile, final String subtitleRegex) {
		String magnetLinkForFile;
		final String subtitleName = subtitleFile.getName().replaceAll("\\.[Ss][Rr][Tt]", "");
		final boolean shouldRefuse = shouldRefuseSubtitleFile(subtitleName, subtitleRegex);
		if(shouldRefuse){
			return false;
		}
		getOutputListener().outVerbose("Procurando melhor torrent para "+subtitleName);
		magnetLinkForFile = torrentSearcher.getMagnetLinkForTermOrNull(subtitleName,getOutputListener());
		if(magnetLinkForFile == null){
			getOutputListener().outVerbose("Nenhum torrent para "+subtitleName);
			subtitleFile.delete();
			return false;
		}
		getOutputListener().outVerbose("Abrindo magnet link no cliente: "+magnetLinkForFile);
		magnetLinkHandler.openURL(magnetLinkForFile);
		getOutputListener().out("Magnet link '"+magnetLinkForFile+"' de "+subtitleName+" enviado ao client de torrent.");
		
		return true;
	}
	
	private boolean shouldRefuseSubtitleFile(final String subtitleName, final String subtitlePattern) {
		return subtitleName.toLowerCase().matches(subtitlePattern);
	}

	public OutputListener getOutputListener() {
		return outputListener;
	}

	public void setOutputListener(final OutputListener outputListener) {
		this.outputListener = outputListener;
	}

}
