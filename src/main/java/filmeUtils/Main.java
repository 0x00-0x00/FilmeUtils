package filmeUtils;

import org.apache.http.impl.client.DefaultHttpClient;

public class Main {

    public static void main(final String[] args) throws Exception {

        final DefaultHttpClient httpclient = new DefaultHttpClient();
        
        System.out.println("Loging in...");
        LegendasTv.login(httpclient);
        
        final String searchTerm = args[0];
        System.out.println("Searching "+searchTerm+" ...");
		LegendasTv.search(searchTerm,httpclient);
        
        close(httpclient);        
    }

	private static void close(final DefaultHttpClient httpclient) {
		httpclient.getConnectionManager().shutdown();
	}
}
