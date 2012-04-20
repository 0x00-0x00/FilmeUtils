package filmeUtils.subtitleSites;

import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.Assert;

import org.junit.Test;

import filmeUtils.OutputListener;
import filmeUtils.SearchListener;

public class LegendasTvTest {
	
	private final OutputListener dummyOutputListener;

	public LegendasTvTest() {
		dummyOutputListener = new OutputListener() {
			public void out(final String string) {
				//do nothing
			}
		};
	}
	
	@Test
	public void searchTest(){
		final String response = "LegendasTvOneResult.html";
		final SimpleHttpClientMock mock = getHttpMock(response);
		final LegendasTv subject = new LegendasTv(mock, dummyOutputListener);
		final AtomicBoolean wasCalled = new AtomicBoolean(false);
		subject.search("foo", new SearchListener() {
			public void found(final String name, final String link) {
				Assert.assertEquals("Castle.S04E21.720p.WEB-DL.DD5.1.H.264-NFHD",name);
				Assert.assertEquals("http://legendas.tv/info.php?c=1&d=e613c192c4279ff32db5f3ad0640e8d0",link);
				wasCalled.set(true);
			}
		});
		Assert.assertTrue(wasCalled.get());
	}
	
	@Test
	public void badLoginTest(){
		final String response = "LegendasTvBadLogin.html";
		final SimpleHttpClientMock mock = getHttpMock(response);
		final LegendasTv subject = new LegendasTv(mock, dummyOutputListener);
		try{
			subject.login("foo", "bar");
			Assert.fail();
		}catch(final BadLoginException e){}
	}

	private SimpleHttpClientMock getHttpMock(final String response) {
		final SimpleHttpClientMock mock = new SimpleHttpClientMock();
		mock.setResponse(response);
		return mock;
	}
	
	@Test
	public void alreadyLogged_ShouldNotTryToLogAgain(){
		final String response = "LegendasTvAlreadyLogged.html";
		final SimpleHttpClientMock mock = getHttpMock(response);
		final LegendasTv subject = new LegendasTv(mock, dummyOutputListener);
		try{
			subject.login("filmeutils", "password");
		}catch(final BadLoginException e){
			Assert.fail();
		}
		boolean triedToLogin = mock.requested("http://legendas.tv/login_verificar.php");
		Assert.assertFalse(triedToLogin);
	}

}
