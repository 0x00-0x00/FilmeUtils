package filmeUtils.torrentSites;

public class SiteOfflineException extends Exception {

	public SiteOfflineException(String siteName) {
		super(siteName+" esta fora do ar.");
	}

}
