package filmeUtils.utils.extraction;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;

public interface Extractor {

    public abstract void unzip(final File zip, final File destinationFolder) throws ZipException, IOException;

    public abstract void unrar(final File rar, final File destinationFolder);

}