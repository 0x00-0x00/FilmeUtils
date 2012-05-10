package filmeUtils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.zip.ZipException;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectionPoolTimeoutException;

import filmeUtils.extraction.Extractor;
import filmeUtils.fileSystem.FileSystem;
import filmeUtils.http.MagnetLinkHandler;
import filmeUtils.http.SimpleHttpClient;
import filmeUtils.http.SimpleHttpClientImpl;
import filmeUtils.subtitleSites.LegendasTv;
import filmeUtils.torrentSites.TorrentSearcher;

public class Downloader {
	
	private final Extractor extract;
	private final SimpleHttpClient httpclient;
	private final TorrentSearcher torrentSearcher;
	private final LegendasTv legendasTv;
	private final MagnetLinkHandler magnetLinkHandler;
	private final FileSystem fileSystem;
	private final OutputListener outputListener;
	private FilmeUtilsOptions options;
	
	public Downloader(final Extractor extract,final FileSystem fileSystem,final SimpleHttpClient httpclient,final TorrentSearcher torrentSearcher,final MagnetLinkHandler magnetLinkHandler,final LegendasTv legendasTv, final OutputListener outputListener) {
		this.fileSystem = fileSystem;
		this.httpclient = httpclient;
		this.torrentSearcher = torrentSearcher;
		this.magnetLinkHandler = magnetLinkHandler;
		this.legendasTv = legendasTv;
		this.extract = extract;
		this.outputListener = outputListener;
	}

	public boolean download(final String name,final String link, final FilmeUtilsOptions options){
		this.options = options;
		return unzipSearchMagnetsAndReturnSuccess(name,link);
	}
	
	
	private boolean unzipSearchMagnetsAndReturnSuccess(final String name,final String link){
		try {
			final String validNameForFile = name.replaceAll("[/ \\\\?]", "_");
			final File subtitlesDestinationFolderOrNull = options.getSubtitlesDestinationFolderOrNull();
			final File currentSubtitleFolder = new File(subtitlesDestinationFolderOrNull, validNameForFile);
			
			fileSystem.mkdir(currentSubtitleFolder);
			outputListener.outVerbose("Extraindo legendas para "+currentSubtitleFolder.getAbsolutePath());
			
			downloadLinkToFolder(link, currentSubtitleFolder);
			final boolean success = openTorrentsAndReturnSuccess(currentSubtitleFolder);
			FileUtils.deleteDirectory(currentSubtitleFolder);
			return success;
		} catch(final ConnectionPoolTimeoutException e){
			outputListener.out("Tempo máximo de requisição atingido ("+SimpleHttpClientImpl.TIMEOUT+" segundos)");
			return false;
		}catch (final IOException e) {
			outputListener.out(e.getMessage()+"\n"+e.getStackTrace().toString());
			return false;
		}
	}

	private void downloadLinkToFolder(final String link, final File folder) throws IOException, ClientProtocolException, ZipException {
		String contentType;
		final File destFile = createFileToBeWritten(folder);
		
		contentType = httpclient.getToFile(link, destFile);
		
		if(contentType.contains("text/html")){
			legendasTv.login(); 
		}
		contentType = httpclient.getToFile(link, destFile);
		extract(destFile, folder, contentType);
		destFile.delete();
	}
	
	private File createFileToBeWritten(final File currentSubtitleCollection) throws IOException {
		final File destFile = new File(currentSubtitleCollection,"compressedSubs");
		fileSystem.createNewFile(destFile);
		destFile.createNewFile();
		return destFile;
	}

	private boolean openTorrentsAndReturnSuccess(final File currentSubtitleFolder) {
		boolean successfull = false;
		@SuppressWarnings("unchecked")
		final Iterator<File> iterateFiles = FileUtils.iterateFiles(currentSubtitleFolder, new String[]{"srt"}, true);
		while(iterateFiles.hasNext()){
			final File next = iterateFiles.next();
			final boolean success = downloadTorrentAndCopySubtitle(next);
			if(options.isLazy() && success){
				return true;
			}
			successfull = successfull || success;
		}
		return successfull;
	}
	
	private void extract(final File compressedFile,final File destinationFolder, final String contentType)throws ZipException, IOException {
		if(contentType.contains("rar")){
			extract.unrar(compressedFile, destinationFolder);
			return;
		}
		if(contentType.contains("zip")){
			extract.unzip(compressedFile, destinationFolder);
			return;
		}
		throw new RuntimeException("Tipo desconhecido: "+contentType);
	}

	private boolean downloadTorrentAndCopySubtitle(final File next) {
		String magnetLinkForFile;
		final String subtitleName = next.getName().replaceAll("\\.[Ss][Rr][Tt]", "");
		final boolean shouldRefuse = shouldRefuseSubtitleFile(subtitleName);
		if(shouldRefuse){
			return false;
		}
		
		magnetLinkForFile = torrentSearcher.getMagnetLinkForFileOrNull(subtitleName);
		if(magnetLinkForFile == null){
			return false;
		}
		outputListener.outVerbose("Abrindo no browser: "+magnetLinkForFile);
		magnetLinkHandler.openURL(magnetLinkForFile);
		outputListener.out("Downloading: "+subtitleName);
		try {
			FileUtils.copyFileToDirectory(next, options.getSubtitlesDestinationFolderOrNull());
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		return true;
	}
	
	private boolean shouldRefuseSubtitleFile(final String subtitleName) {
		boolean shouldRefuse = false;
		final boolean isHiDef = subtitleName.contains("720") || subtitleName.contains("1080");
		if(options.shouldRefuseHD()){
			if(isHiDef){
				shouldRefuse = true;
			}
		}
		if(options.shouldRefuseNonHD()){
			if(!isHiDef){
				shouldRefuse = true;
			}
		}
		return shouldRefuse;
	}
}
