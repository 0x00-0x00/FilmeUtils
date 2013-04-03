import filmeUtils.commons.VerboseSysOut;
import filmeUtils.subtitle.Subtitle;
import filmeUtils.subtitle.subtitleSites.LegendasTv;
import filmeUtils.utils.http.SimpleHttpClient;
import filmeUtils.utils.http.SimpleHttpClientImpl;


public class SubtitleMain {

	public static void main(String[] args) {		
		final VerboseSysOut output = new VerboseSysOut();
		SimpleHttpClient httpclient = new SimpleHttpClientImpl();
		LegendasTv legendasTv = new LegendasTv(httpclient, output);
		Subtitle subtitle = new Subtitle(output,httpclient,legendasTv);
		subtitle.search("2001");
	}
}