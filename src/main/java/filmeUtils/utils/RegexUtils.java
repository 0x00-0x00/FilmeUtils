package filmeUtils.utils;

public class RegexUtils {

	public static boolean matchesCaseInsensitive(String string, String regex) {
		return string.toLowerCase().matches(regex.toLowerCase());
	}

	public static RegexForSubPackageAndSubFile getSplittedRegex(String regex) {
		String packageRegex = ".*";
		String fileRegex = ".*";
		String[] splitted = regex.split(":");
		if(splitted.length > 1){
			fileRegex = splitted[1];
		}
		packageRegex = splitted[0];
		return new RegexForSubPackageAndSubFile(packageRegex,fileRegex);
	}

}
