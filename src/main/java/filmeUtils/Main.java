package filmeUtils;

import httpClientUtils.HttpClientUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class Main {

    public static void main(final String[] args){
    	final MainCLI cli = new MainCLI();
    	cli.parse(args);
    	if(cli.isDone()){
    		return;
    	}
    	
        final DefaultHttpClient httpclient = start();
        
        final LegendasTv legendasTv = new LegendasTv(httpclient);
        System.out.println("Autenticando...");
        legendasTv.login();
        
        final boolean showCompressedContents = cli.showCompressedContents();
        final SearchListener searchListener = new SearchListener() {public void found(final String name, final String link) {
        	System.out.println(name+" - "+link);
        	if(!showCompressedContents){
        		return;
        	}
        	unzipAndPrint(httpclient, link);
        }

		private void unzipAndPrint(final DefaultHttpClient httpclient,
				final String link) {
			try {
	        	
	        	final File destFolder = File.createTempFile("AAA", "BBB");
	        	destFolder.delete();
	        	destFolder.mkdir();
	        	
	        	final File destFile = new File(destFolder,"compressedSubs");
				destFile.createNewFile();
				
	        	final HttpGet httpGet = new HttpGet(link);
				HttpClientUtils.executeAndSaveResponseToFile(httpGet, destFile, httpclient);
        	
				final ZipFile zipFile = new ZipFile(destFile);
				
				final Enumeration<? extends ZipEntry> entries = zipFile.entries();
				while(entries.hasMoreElements()) {
					final ZipEntry entry = entries.nextElement();
					final File unzippingFile = new File(destFolder,entry.getName());
					if(entry.isDirectory()) {
						unzippingFile.mkdir();
					}else{
						unzippingFile.createNewFile();
						final FileOutputStream fileOutputStream = new FileOutputStream(unzippingFile);
						final InputStream inputStream = zipFile.getInputStream(entry);
						IOUtils.copy(inputStream, fileOutputStream);
					}
				}
				zipFile.close();
				
				final Iterator<File> iterateFiles = FileUtils.iterateFiles(destFolder, new String[]{"srt"}, true);
				while(iterateFiles.hasNext()){
					final File next = iterateFiles.next();
					System.out.println("\t"+next.getName());
				}
				
	        	FileUtils.cleanDirectory(destFolder);
        	} catch (final ZipException e) {
        		System.out.println("\tOnly zip supported for now.");
        	} catch (final IOException e1) {
				throw new RuntimeException(e1);
			}
		}};
        
        if(cli.search()){
        	final String searchTerm = cli.searchTerm();
			System.out.println("Procurando '"+searchTerm+"' ...");
        	
        	legendasTv.search(searchTerm,searchListener);
        }
        
        if(cli.showNewAdditions()){
        	System.out.println("Novas legendas:");
        	final int newAdditionsPageCountToShow = cli.newAdditionsPageCountToShow();
        	legendasTv.getNewer(newAdditionsPageCountToShow,searchListener);
        	
        }
        close(httpclient);        
    }
    
	private static DefaultHttpClient start() {
		final DefaultHttpClient httpclient = new DefaultHttpClient();
		return httpclient;
	}

	private static void close(final DefaultHttpClient httpclient) {
		httpclient.getConnectionManager().shutdown();
	}
}
