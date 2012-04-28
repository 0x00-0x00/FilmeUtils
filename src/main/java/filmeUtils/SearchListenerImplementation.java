package filmeUtils;


import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.zip.ZipException;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectionPoolTimeoutException;

import filmeUtils.extraction.Extractor;
import filmeUtils.http.MagnetLinkHandler;
import filmeUtils.http.SimpleHttpClient;
import filmeUtils.http.SimpleHttpClientImpl;
import filmeUtils.subtitleSites.LegendasTv;
import filmeUtils.torrentSites.TorrentSearcher;

final class SearchListenerImplementation implements SearchListener {
	private final SimpleHttpClient httpclient;
	private final boolean extractContents;
	private final TorrentSearcher torrentSearcher;
	private final File subtitleDestination;
	private final boolean showSubtitleIfMagnetWasNotFound;
	private final String nameAcceptanceRegex;
	private final OutputListener outputListener;
	private final LegendasTv legendasTv;
	private final MagnetLinkHandler magnetLinkHandler;
	private final Extractor extract;

	SearchListenerImplementation(final SimpleHttpClient httpclient,final Extractor extract,final TorrentSearcher torrentSearcher,final MagnetLinkHandler magnetLinkHandler,final LegendasTv legendasTv, final ArgumentsParser cli, final OutputListener outputListener) {
		this.httpclient = httpclient;
		this.extract = extract;
		this.torrentSearcher = torrentSearcher;
		this.magnetLinkHandler = magnetLinkHandler;
		this.legendasTv = legendasTv;
		this.showSubtitleIfMagnetWasNotFound = cli.showSubtitleIfMagnetWasNotFound();
		this.nameAcceptanceRegex = cli.getAcceptanceRegexOrNull();
		this.outputListener = outputListener;
		this.subtitleDestination = cli.getSubtitlesDestinationFolderOrNull();
		this.extractContents = subtitleDestination!= null;
	}

	public void found(final String name, final String link) {
		if(nameAcceptanceRegex != null && !nameAcceptanceRegex.isEmpty()){
			if(!name.matches(nameAcceptanceRegex)){
				return;
			}
		}
		outputListener.out(name);
		if(!extractContents){
			return;
		}
		unzipAndPrint(name,link);
	}

	private void unzipAndPrint(final String name,final String link){
		try {
			final String validNameForFile = name.replaceAll("[/ \\\\?]", "_");
			final File currentSubtitleFolder = new File(subtitleDestination, validNameForFile);
			currentSubtitleFolder.mkdir();
			outputListener.out("Extraindo legendas para "+currentSubtitleFolder.getAbsolutePath());
			
			downloadLinkToFolder(link, currentSubtitleFolder);
	    	
			outputSubtitleFiles(currentSubtitleFolder);
			
		} catch(final ConnectionPoolTimeoutException e){
			outputListener.out("Tempo máximo de requisição atingido ("+SimpleHttpClientImpl.TIMEOUT+" segundos)");
		}catch (final IOException e) {
			e.printStackTrace();//not the end of the world as we know it, and I fell fine
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

	private File createFileToBeWritten(final File currentSubtitleCollection)
			throws IOException {
		final File destFile = new File(currentSubtitleCollection,"compressedSubs");
		destFile.createNewFile();
		return destFile;
	}

	private void outputSubtitleFiles(final File currentSubtitleFolder) {
		@SuppressWarnings("unchecked")
		final Iterator<File> iterateFiles = FileUtils.iterateFiles(currentSubtitleFolder, new String[]{"srt"}, true);
		while(iterateFiles.hasNext()){
			final File next = iterateFiles.next();
			showFileName(next);
		}
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

	private void showFileName(final File next) {
		String magnetLinkForFile;
		final String subtitleName = next.getName().replaceAll("\\.[Ss][Rr][Tt]", "");
		final String subtitleNameFormmated = "\t* "+subtitleName;
		magnetLinkForFile = torrentSearcher.getMagnetLinkForFileOrNull(subtitleName);
		if(magnetLinkForFile == null){
			if(showSubtitleIfMagnetWasNotFound){
				outputListener.out(subtitleNameFormmated + " - magnet não foi encontrado");
			}
			return;
		}
		
		outputListener.outVerbose("Abrindo no browser: "+magnetLinkForFile);
		
		magnetLinkHandler.openURL(magnetLinkForFile);
		outputListener.out(subtitleNameFormmated + " - " + magnetLinkForFile);
	}
}