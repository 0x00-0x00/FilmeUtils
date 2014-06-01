package filmeUtils.subtitleSites;

import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.Assert;

import org.junit.Test;

import filmeUtils.commons.OutputListener;
import filmeUtils.subtitle.subtitleSites.legendasTV.LegendasTv;
import filmeUtils.subtitle.subtitleSites.SubtitlePackageAndLink;

public class LegendasTvTest {

	private final OutputListener dummyOutputListener;

	public LegendasTvTest() {
		dummyOutputListener = new DummyOutputListener();
	}
	
	@Test
	public void getNewAddsForReal(){
		final LegendasTv subject = new LegendasTv( dummyOutputListener);
		final AtomicBoolean atLeastOne = new AtomicBoolean(false);
		subject.getNewer( 1, (SubtitlePackageAndLink subAndLink) -> atLeastOne.set(true));
		Assert.assertTrue(atLeastOne.get());
	}

    @Test
    public void getSearchForReal(){
        final LegendasTv subject = new LegendasTv( dummyOutputListener);
        final AtomicBoolean atLeastOne = new AtomicBoolean(false);
        subject.search("house m.d.", (SubtitlePackageAndLink subAndLink) -> atLeastOne.set(true));
        Assert.assertTrue(atLeastOne.get());
    }
}