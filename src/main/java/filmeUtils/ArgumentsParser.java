package filmeUtils;

import java.io.File;

public interface ArgumentsParser {

	boolean showSubtitleIfMagnetWasNotFound();

	String getAcceptanceRegexOrNull();

	File getSubtitlesDestinationFolderOrNull();

}
