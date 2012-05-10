package filmeUtils.swing;

import filmeUtils.extraction.Extractor;
import filmeUtils.http.SimpleHttpClient;
import filmeUtils.subtitleSites.LegendasTv;

public class SearchScreenNeeds {
	
	private final SimpleHttpClient httpclient;
	private final LegendasTv legendasTv;
	private final Extractor extract;

	public SearchScreenNeeds(final SimpleHttpClient httpclient,final LegendasTv legendasTv,final Extractor extract) {
		this.httpclient = httpclient;
		this.legendasTv = legendasTv;
		this.extract = extract;
	}

	public void download(final String item) {
		throw new RuntimeException("Method not implemented");
	}

}
