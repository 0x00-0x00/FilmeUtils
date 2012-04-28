package filmeUtils;

import java.io.File;

public interface ArgumentsParser {

	String getUser();
	String getPassword();
	boolean showSubtitleIfMagnetWasNotFound();
	String getAcceptanceRegexOrNull();
	File getSubtitlesDestinationFolderOrNull();
	boolean isVerbose(); 

}
