package filmeUtils.web;

import java.io.IOException;
import java.net.UnknownHostException;

import filmeUtils.commons.VerboseSysOut;
import filmeUtils.subtitle.Subtitle;
import filmeUtils.subtitle.subtitleSites.legendasTV.LegendasTv;
import static spark.Spark.*;

public class WebMain {

	public static void main(String[] args) throws UnknownHostException, IOException {
		
		Subtitle subtitle = new Subtitle(new VerboseSysOut(), new LegendasTv(new VerboseSysOut()));
		
		staticFileLocation("/filmeUtils/web");
		get("/legendasTv", (req, res) -> {
			StringBuffer stringBuffer = new StringBuffer();
			subtitle.listNewSubtitles( nameAndLink -> stringBuffer.append(nameAndLink.toString() + "<br>"));
			return stringBuffer.toString();
		});
	}

}
