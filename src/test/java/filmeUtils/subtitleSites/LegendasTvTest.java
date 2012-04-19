package filmeUtils.subtitleSites;

import junit.framework.Assert;

import org.junit.Test;

import filmeUtils.OutputListener;

public class LegendasTvTest {
	
	@Test
	public void badLoginTest(){
		final SimpleHttpClientMock mock = new SimpleHttpClientMock();
		final LegendasTv subject = new LegendasTv(mock, new OutputListener() {
			public void out(final String string) {
				//do nothing
			}
		});
		mock.setResponse("badLogin.html");
		try{
			subject.login("foo", "bar");
			Assert.fail();
		}catch(final BadLoginException e){}
	}

}
