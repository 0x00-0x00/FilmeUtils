package filmeUtils;

import org.apache.http.impl.client.DefaultHttpClient;

public class Main {

    public static void main(final String[] args){
    	final MainCLI cli = new MainCLI();
    	cli.parse(args);
    	if(cli.isDone()){
    		return;
    	}
    	
        final DefaultHttpClient httpclient = start();
        
        final LegendasTv legendasTv = new LegendasTv(httpclient);
        System.out.println("Autenticando...");
        legendasTv.login();
        
        final SearchListener searchListener = new SearchListener() {public void found(final String name, final String link) {
        	System.out.println(name+" - "+link);
        }};
        
        if(cli.search()){
        	final String searchTerm = cli.searchTerm();
			System.out.println("Procurando '"+searchTerm+"' ...");
        	
        	legendasTv.search(searchTerm,searchListener);
        }
        
        if(cli.showNewAdditions()){
        	System.out.println("Novas legendas:");
        	final int newAdditionsPageCountToShow = cli.newAdditionsPageCountToShow();
        	legendasTv.getNewer(newAdditionsPageCountToShow,searchListener);
        	
        }
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
