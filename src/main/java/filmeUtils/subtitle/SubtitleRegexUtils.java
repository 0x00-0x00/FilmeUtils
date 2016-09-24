package filmeUtils.subtitle;

public class SubtitleRegexUtils {

    public static String getSubtitlesZipRegex(final String subtitleRegex) {
        final String[] splitted = subtitleRegex.split(":");
        return splitted[0];
    }

    public static String getSubtitleRegex(final String pattern) {
        final String[] splitted = pattern.split(":");
        String subtitleRegex = ".*";
        if (splitted.length > 1) {
            subtitleRegex = splitted[1];
        }
        return subtitleRegex;
    }

}
