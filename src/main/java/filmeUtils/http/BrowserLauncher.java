package filmeUtils.http;

public interface BrowserLauncher {

	/**
	 * Opens the specified web page in the user's default browser
	 * 
	 * @param url
	 *            A web address (URL) of a web page (ex:
	 *            "http://www.google.com/")
	 */
	public abstract void openURL(final String url);

}