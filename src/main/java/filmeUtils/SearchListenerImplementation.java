package filmeUtils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectionPoolTimeoutException;

import com.github.junrar.testutil.ExtractArchive;

import filmeUtils.torrentSites.PirateBaySe;

final class SearchListenerImplementation implements SearchListener {
	private final FilmeUtilsHttpClient httpclient;
	private final boolean showCompressedContents;
	private final PirateBaySe pirateBaySe;
	private final File subtitleDestination;

	SearchListenerImplementation(final FilmeUtilsHttpClient httpclient,final boolean showCompressedContents,final File subtitleDestination) {
		this.httpclient = httpclient;
		this.showCompressedContents = showCompressedContents;
		this.subtitleDestination = subtitleDestination;
        pirateBaySe = new PirateBaySe(httpclient);
	}

	public void found(final String name, final String link) {
		System.out.println(name+" - "+link);
		if(!showCompressedContents){
			return;
		}
		unzipAndPrint(name,link);
	}

	private void unzipAndPrint(final String name,final String link){
		try {
			final String validNameForFile = name.replaceAll("[/ \\\\?]", "_");
			final File currentSubtitleCollection = new File(subtitleDestination, validNameForFile);
			currentSubtitleCollection.mkdir();
			final File destFile = new File(currentSubtitleCollection,"compressedSubs");
			destFile.createNewFile();
			
	    	final HttpGet httpGet = new HttpGet(link);
	    	final String contentType = httpclient.executeSaveResponseToFileReturnContentType(httpGet, destFile);
			
			if(contentType.contains("rar")){
				ExtractArchive.extractArchive(destFile, currentSubtitleCollection);
			}else{
				final ZipFile zipFile = new ZipFile(destFile);
				
				final Enumeration<? extends ZipEntry> entries = zipFile.entries();
				while(entries.hasMoreElements()) {
					final ZipEntry entry = entries.nextElement();
					final File unzippingFile = new File(currentSubtitleCollection,entry.getName());
					if(entry.isDirectory()) {
						unzippingFile.mkdir();
					}else{
						unzippingFile.createNewFile();
						final FileOutputStream fileOutputStream = new FileOutputStream(unzippingFile);
						final InputStream inputStream = zipFile.getInputStream(entry);
						IOUtils.copy(inputStream, fileOutputStream);
						inputStream.close();
						fileOutputStream.close();
					}
				}
				zipFile.close();
				destFile.delete();
			}
			
			@SuppressWarnings("unchecked")
			final Iterator<File> iterateFiles = FileUtils.iterateFiles(currentSubtitleCollection, new String[]{"srt"}, true);
			while(iterateFiles.hasNext()){
				final File next = iterateFiles.next();
				String magnetLinkForFile;
				final String subtitleName = next.getName().replaceAll("\\.[Ss][Rr][Tt]", "");
				try {
					magnetLinkForFile = pirateBaySe.getMagnetLinkForFile(subtitleName);
				} catch (final Exception e) {
					magnetLinkForFile = "error finding magnet";
				}
				System.out.println("\t"+subtitleName+" - "+magnetLinkForFile);
			}
			
		} catch(final ConnectionPoolTimeoutException e){
			System.out.println("Tempo máximo de requisição atingido ("+FilmeUtilsHttpClient.TIMEOUT+" segundos)");
		}catch (final IOException e1) {
			throw new RuntimeException(e1);
		}
	}
}