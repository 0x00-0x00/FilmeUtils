package filmeUtils.subtitleSites;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import filmeUtils.commons.OutputListener;
import filmeUtils.subtitle.subtitleSites.SubtitlePackageAndLink;
import filmeUtils.subtitle.subtitleSites.legendasTV.LegendasTv;
import junit.framework.Assert;

public class LegendasTvTest {

    private final OutputListener dummyOutputListener;

    public LegendasTvTest() {
        dummyOutputListener = new DummyOutputListener();
    }

    @Test
    public void getNewAddsForReal() {
        final LegendasTv subject = new LegendasTv(dummyOutputListener);
        final AtomicBoolean atLeastOne = new AtomicBoolean(false);
        subject.getNewer(1, (final SubtitlePackageAndLink subAndLink) -> atLeastOne.set(true));
        Assert.assertTrue(atLeastOne.get());
    }

    @Test
    public void getSearchForReal() {
        final LegendasTv subject = new LegendasTv(dummyOutputListener);
        final AtomicBoolean atLeastOne = new AtomicBoolean(false);
        subject.search("house m.d.", (final SubtitlePackageAndLink subAndLink) -> atLeastOne.set(true));
        Assert.assertTrue(atLeastOne.get());
    }
}