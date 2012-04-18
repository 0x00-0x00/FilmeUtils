package filmeUtils.torrentSites;

public interface TorrentSite {

	public abstract String getMagnetLinkFirstResultOrNull(
			final String exactFileName);

}