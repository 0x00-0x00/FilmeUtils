package filmeUtils.torrent.torrentSites;

@SuppressWarnings("serial")
public class SiteOfflineException extends Exception {

    public SiteOfflineException(final String siteName) {
        super(siteName + " esta fora do ar.");
    }

}
