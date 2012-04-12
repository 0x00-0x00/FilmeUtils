package filmeUtils;

import org.apache.http.impl.client.DefaultHttpClient;

public class Main {

    public static void main(final String[] args) throws Exception {

        final DefaultHttpClient httpclient = new DefaultHttpClient();
        
        System.out.println("Loging...");
        LegendasTv.login(httpclient);
        System.out.println("Searching ...");
        LegendasTv.search(httpclient);
        
        close(httpclient);        
    }

	private static void close(final DefaultHttpClient httpclient) {
		httpclient.getConnectionManager().shutdown();
	}
}
