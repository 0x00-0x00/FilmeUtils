package filmeUtils;


import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.zip.ZipException;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectionPoolTimeoutException;

import filmeUtils.extraction.ExtractorImpl;
import filmeUtils.http.SimpleHttpClient;
import filmeUtils.http.SimpleHttpClientImpl;
import filmeUtils.torrentSites.TorrentSearcher;

final class SearchListenerImplementation implements SearchListener {
	private final SimpleHttpClient httpclient;
	private final boolean extractContents;
	private final TorrentSearcher torrentSearcher;
	private final File subtitleDestination;
	private final boolean showDirectLink;
	private final boolean showSubtitleIfMagnetWasNotFound;

	SearchListenerImplementation(final SimpleHttpClient httpclient,final boolean showDirectLink,final boolean showSubtitleIfMagnetWasNotFound, final File subtitleDestination) {
		this.httpclient = httpclient;
		this.showSubtitleIfMagnetWasNotFound = showSubtitleIfMagnetWasNotFound;
		this.extractContents = subtitleDestination!= null;
		this.showDirectLink = showDirectLink;
		this.subtitleDestination = subtitleDestination;
        torrentSearcher = new TorrentSearcher(httpclient);
	}

	public void found(final String name, final String link) {
		String direct_link = "";
		if(showDirectLink){
			direct_link = " - "+link;
		}
		System.out.println(name+direct_link);
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
			System.out.println("Extraindo legendas para "+currentSubtitleFolder.getAbsolutePath());
			
			downloadLinkToFolder(link, currentSubtitleFolder);
	    	
			outputSubtitleFiles(currentSubtitleFolder);
			
		} catch(final ConnectionPoolTimeoutException e){
			System.out.println("Tempo máximo de requisição atingido ("+SimpleHttpClientImpl.TIMEOUT+" segundos)");
		}catch (final IOException e) {
			e.printStackTrace();//not the end of the world as we know it, and I fell fine
		}
	}

	private void downloadLinkToFolder(final String link, final File folder) throws IOException, ClientProtocolException, ZipException {
		String contentType;
		final File destFile = createFileToBeWritten(folder);
		
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

	private void extract(final File compressedFile,
			final File destinationFolder, final String contentType)
			throws ZipException, IOException {
		final ExtractorImpl extract = new ExtractorImpl();
		if(contentType.contains("rar")){
			extract.unrar(compressedFile, destinationFolder);
		}
		if(contentType.contains("zip")){
			extract.unzip(compressedFile, destinationFolder);
		}
		if(contentType.contains("text/html")){
			final String fileText = FileUtils.readFileToString(compressedFile);
			System.out.println(fileText);
		}
	}

	private void showFileName(final File next) {
		String magnetLinkForFile;
		final String subtitleName = next.getName().replaceAll("\\.[Ss][Rr][Tt]", "");
		final String subtitleNameFormmated = "\t* "+subtitleName;
		magnetLinkForFile = torrentSearcher.getMagnetLinkForFileOrNull(subtitleName);
		if(magnetLinkForFile == null){
			if(showSubtitleIfMagnetWasNotFound){
				System.out.println(subtitleNameFormmated + " - magnet não foi encontrado");
			}
			return;
		}
		System.out.println(subtitleNameFormmated + " - " + magnetLinkForFile);
	}
}