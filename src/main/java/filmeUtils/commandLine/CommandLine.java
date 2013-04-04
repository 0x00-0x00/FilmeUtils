package filmeUtils.commandLine;

import java.io.File;

public interface CommandLine {
	
	public void h();
	public void lt(final String subtitleSearchTerm, final String regexToApplyOnSubtitlesFiles, final File destinantion);
	public void l(final String subtitleSearchTerm, final File destinantion);
	public void l(final String subtitleSearchTerm, final String regexToApplyOnSubtitlesFiles, final File destinantion);
	public void t(final String torrentSearchTerm);
	public void n();
	public void n(final String regexToApplyOnSubtitlesPackage,final String regexToApplyOnSubtitlesFiles, final File destinantion);
	public void f(final File regexFile, final File destinantion);
	public void p(final String subtitleSearchTerm);
	
}
