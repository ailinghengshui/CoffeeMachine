package com.jingye.coffeemac.module.managermodule;

import com.jingye.coffeemac.module.repairmodule.RepairControlActivity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Hades on 2017/2/9.
 */
public class ManagerControlPresenterTest {

    @Mock
    private ManagerControlContract.ManagerControlView managerControlView;

    @Mock
    private ManagerControlContract.ManagerControlModel managerControlModel;

    private ManagerControlPresenter mManagerControlPresenter;

    @Before
    public void setUpPresenter() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mManagerControlPresenter=new ManagerControlPresenter(managerControlView,managerControlModel);

    }



    @Test
    public void testShowCurTab() throws Exception {
        mManagerControlPresenter.showCurTab(RepairControlActivity.REPAIR_CHECKIN);
        verify(managerControlView).showCurTab(RepairControlActivity.REPAIR_CHECKIN);
    }

    @Test
    public void testEqual() throws Exception {
        assertTrue(true);

    }

    @Test
    public void testList() throws Exception {
        LinkedList mockedList = mock(LinkedList.class);

// stubbing appears before the actual execution
        when(mockedList.get(0)).thenReturn("first");

// the following prints "first"
        System.out.println(mockedList.get(0));

// the following prints "null" because get(999) was not stubbed
        System.out.println(mockedList.get(999));

    }
}