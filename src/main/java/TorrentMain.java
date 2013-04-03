import filmeUtils.commons.VerboseSysOut;
import filmeUtils.torrent.Torrent;
import filmeUtils.torrent.torrentSites.SiteOfflineException;


public class TorrentMain {

	public static void main(String[] args) throws SiteOfflineException {
		VerboseSysOut verboseSysOut = new VerboseSysOut();
		Torrent torrent = new Torrent(verboseSysOut);
		torrent.download("windows");
	}
}
