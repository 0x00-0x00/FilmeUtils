package filmeUtils;

import org.apache.http.impl.client.DefaultHttpClient;

public class Main {

    public static void main(final String[] args) throws Exception {

        final DefaultHttpClient httpclient = start();
        
        final LegendasTv legendasTv = new LegendasTv(httpclient);
        
        System.out.println("Loging in...");
        legendasTv.login();
        
        final String searchTerm = args[0];
        System.out.println("Searching "+searchTerm+" ...");
        
        final SearchListener searchListener = new SearchListener() {
			public void found(final String name, final String link) {
				System.out.println(name+" - "+link);
			}
		};
		
        legendasTv.search(searchTerm,searchListener);
        close(httpclient);        
    }

	private static DefaultHttpClient start() {
		final DefaultHttpClient httpclient = new DefaultHttpClient();
		return httpclient;
	}

	private static void close(final DefaultHttpClient httpclient) {
		httpclient.getConnectionManager().shutdown();
	}
}
