package filmeUtils.utils.extraction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

import com.github.junrar.extract.ExtractArchive;

public class ExtractorImpl implements Extractor {


	public void unzip(final File zip, final File destinationFolder) throws ZipException, IOException{
		final ZipFile zipFile = new ZipFile(zip);
		
		final Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while(entries.hasMoreElements()) {
			final ZipEntry entry = entries.nextElement();
			final File unzippingFile = new File(destinationFolder,entry.getName());
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
		zip.delete();
	}


	public void unrar(final File rar, final File destinationFolder) {
		ExtractArchive extractArchive = new ExtractArchive();
		extractArchive.extractArchive(rar, destinationFolder);
	}
	
}
