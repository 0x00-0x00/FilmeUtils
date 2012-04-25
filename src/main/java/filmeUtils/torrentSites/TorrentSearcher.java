package filmeUtils.torrentSites;

public interface TorrentSearcher {

	public abstract String getMagnetLinkForFileOrNull(final String exactFileName);

}