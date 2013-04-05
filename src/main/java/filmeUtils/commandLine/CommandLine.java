package filmeUtils.commandLine;

import java.io.File;
import java.util.List;

import filmeUtils.utils.RegexForSubPackageAndSubFile;

public interface CommandLine {
	
	public void h();
	public void lt(final String subtitleSearchTerm, final String regexToApplyOnSubtitlesFiles, final File destinantion);
	public void l(final String subtitleSearchTerm, final String regexToApplyOnSubtitlesFiles, final File destinantion);
	public void t(final String torrentSearchTerm);
	public void n();
	public void n(final RegexForSubPackageAndSubFile regexToApplyOnSubtitlesPackage, final File destinantion);
	public void f(final List<RegexForSubPackageAndSubFile> regexes, final File destinantion);
	public void p(final String subtitleSearchTerm);
	public void auto();
	
}
