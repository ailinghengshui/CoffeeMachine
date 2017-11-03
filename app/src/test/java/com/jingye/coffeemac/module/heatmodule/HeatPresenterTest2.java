package com.jingye.coffeemac.module.heatmodule;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

/**
 * Created by Hades on 2017/2/8.
 */
public class HeatPresenterTest2 {

    @Mock
    private HeatContract.IHeatView iHeatView;
    private HeatPresenter heatPresenter;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        heatPresenter=new HeatPresenter(iHeatView);
    }

    @Test
    public void testCompTemp() throws Exception {
        heatPresenter.compareTemp(11);
        verify(iHeatView).intentToWelcome();
    }

}