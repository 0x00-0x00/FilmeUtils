package filmeUtils.utils;

public class RegexUtils {

	public static boolean matchesCaseInsensitive(String string, String regex) {
		return string.toLowerCase().matches(regex.toLowerCase());
	}

}
