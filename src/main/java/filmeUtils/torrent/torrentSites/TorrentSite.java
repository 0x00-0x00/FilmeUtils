package filmeUtils.torrent.torrentSites;

public interface TorrentSite {
	
	public String getSiteName();
	public String getMagnetLinkFirstResultOrNull(final String exactFileName) throws SiteOfflineException;

}