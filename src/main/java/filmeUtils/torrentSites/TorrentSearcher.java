package filmeUtils.torrentSites;

import filmeUtils.OutputListener;

public interface TorrentSearcher {

	public abstract String getMagnetLinkForFileOrNull(final String exactFileName,OutputListener outputListener) throws SiteOfflineException;

}