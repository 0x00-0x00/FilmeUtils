package filmeUtils.fileSystem;

import java.io.File;
import java.io.IOException;

public interface FileSystem {

	void mkdir(File currentSubtitleFolder);
	void createNewFile(File file) throws IOException;

}
