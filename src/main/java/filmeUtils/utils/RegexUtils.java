package filmeUtils.utils;

import java.util.List;

public class RegexUtils {

	public static boolean matchesCaseInsensitive(String string, String regex) {
		return string.toLowerCase().matches(regex.toLowerCase());
	}

	public static RegexForSubPackageAndSubFile getRegexForSubPackageAndSubFile(String regex) {
		String packageRegex = ".*";
		String fileRegex = ".*";
		String[] splitted = regex.split(":");
		if(splitted.length > 1){
			fileRegex = splitted[1];
		}
		packageRegex = splitted[0];
		return new RegexForSubPackageAndSubFile(packageRegex,fileRegex);
	}

	public static RegexForSubPackageAndSubFile getRegexMatchingPackageOrNull(final String packageName,final List<RegexForSubPackageAndSubFile> regexes) {
		for (RegexForSubPackageAndSubFile regexForSubPackageAndSubFile : regexes) {
			if(matchesCaseInsensitive(packageName, regexForSubPackageAndSubFile.packageRegex)){
				return regexForSubPackageAndSubFile;
			}
		}
		return null;
	}

}
