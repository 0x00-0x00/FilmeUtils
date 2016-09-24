package filmeUtils.torrent;

import java.util.Optional;

import filmeUtils.commons.OutputListener;
import filmeUtils.torrent.torrentSites.TorrentSearcherImpl;
import filmeUtils.utils.http.URISchemeLinkHandlerImpl;

public class Torrent {

    private final OutputListener output;
    private final TorrentSearcherImpl torrentSearcherImpl;

    public Torrent(final OutputListener output) {
        this.output = output;
        this.torrentSearcherImpl = new TorrentSearcherImpl();
    }

    public void download(final String searchTerm) {
        final Optional<String> maybeMagnetLinkForTerm = this.torrentSearcherImpl.getMagnetLinkForTerm(searchTerm,
                this.output);
        if (!maybeMagnetLinkForTerm.isPresent()) {
            this.output.out("Nenhum magnet link encontrado!!!");
            return;
        }
        final String magnetLinkForTerm = maybeMagnetLinkForTerm.get();
        this.output.out("Encontrado " + magnetLinkForTerm);
        new URISchemeLinkHandlerImpl().openURL(magnetLinkForTerm);
    }

}
