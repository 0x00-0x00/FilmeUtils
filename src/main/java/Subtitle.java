import java.io.File;
import java.io.IOException;

import filmeUtils.VerboseSysOut;
import filmeUtils.extraction.ExtractorImpl;
import filmeUtils.http.SimpleHttpClientImpl;
import filmeUtils.subtitleSites.LegendasTv;
import filmeUtils.subtitleSites.NewSubtitleLinkFoundCallback;
import filmeUtils.subtitleSites.SubtitleAndLink;
import filmeUtils.subtitleSites.SubtitleLinkSearchCallback;


public class Subtitle {

	public static void main(String[] args) {
		final VerboseSysOut output = new VerboseSysOut();
		if(args.length < 1 || args.length > 3){
			output.out("Uso:\n" +
					"procura legenda\n" +
					"ou\n" +
					"download legenda diretorioDestino\n" +
					"ou\n" +
					"novas");
			System.exit(1);
		}
			
		final SimpleHttpClientImpl httpclient = new SimpleHttpClientImpl();
		LegendasTv legendasTv = new LegendasTv(httpclient, output);
		legendasTv.login();
		if(args[0].equals("procura")){
			legendasTv.search(args[1], new SubtitleLinkSearchCallback() {
				
				@Override
				public boolean processAndReturnIfMatches(SubtitleAndLink nameAndlink) {
					output.out(nameAndlink.name);
					return false;
				}
			});
		}
		if(args[0].equals("download")){
			final File destDir = new File(args[2]);
			legendasTv.search(args[1], new SubtitleLinkSearchCallback() {	
				@Override
				public boolean processAndReturnIfMatches(SubtitleAndLink nameAndlink) {
					output.out("Download de "+nameAndlink.name);
					File tmp;
					try {
						tmp = File.createTempFile("Filmeutils", "Filmeutils");
						String contentType = httpclient.getToFile(nameAndlink.link, tmp);
						ExtractorImpl extractor = new ExtractorImpl();
						if(contentType.contains("rar")){
							extractor.unrar(tmp, destDir);
						}
						if(contentType.contains("zip")){
							extractor.unzip(tmp, destDir);
						}
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
					output.out(nameAndlink.name+" extraido para"+destDir.getAbsolutePath());
					return false;
				}
			});
		}
		if(args[0].equals("novas")){
			legendasTv.getNewer(23, new NewSubtitleLinkFoundCallback() {
				@Override
				public void processAndReturnIfMatches(SubtitleAndLink nameAndlink) {
					output.out(nameAndlink.name);
				}
			});
		}
	}
}
