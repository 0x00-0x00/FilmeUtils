package filmeUtils.fileSystem;

import java.io.File;
import java.io.IOException;

public class FileSystemImpl implements FileSystem {

	public void mkdir(final File newFolder) {
		newFolder.mkdirs();
	}

	public void createNewFile(final File file) throws IOException {
		file.createNewFile();
	}

}
