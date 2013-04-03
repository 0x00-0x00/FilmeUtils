package filmeUtils.subtitle;

public class SubtitleRegexUtils {

	public static String getSubtitlesZipRegex(String subtitleRegex) {
		String[] splitted = subtitleRegex.split(":");
		return splitted[0];
	}

	public static String getSubtitleRegex(String pattern) {
		String[] splitted = pattern.split(":");
		String subtitleRegex = ".*";
		if(splitted.length > 1)
			subtitleRegex = splitted[1];
		return subtitleRegex;
	}

}
