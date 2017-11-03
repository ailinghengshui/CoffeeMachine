package com.jingye.coffeemac.common.fragment;

import com.jingye.coffeemac.common.action.RemoteProxy;
import com.jingye.coffeemac.common.action.TViewWatcher;
import com.jingye.coffeemac.inter.IServiceBindListener;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.service.VendorService;
import com.jingye.coffeemac.util.log.LogUtil;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;

public abstract class TFragment extends Fragment {
	private static final Handler handler = new Handler();
    /**
     * service bind listener
     */
    protected IServiceBindListener mServiceBindListener = new IServiceBindListener() {
    	@Override
		public void onBindSuccess() {
			// handle bound
			handleBound();
		}

		@Override
		public void onBindFailed(String errorMessage) {
			// TODO
		}
    };
	private int fragmentId;
	private boolean destroyed;
    private RemoteProxy proxy = new RemoteProxy() {
		@Override
		public void onReceive(Remote remote) {
			TFragment.this.onReceive(remote);
		}
	};

	protected final boolean isDestroyed() {
		return destroyed;
	}
 
	public int getFragmentId() {
		return fragmentId;
	}
	
	public void setFragmentId(int fragmentId) {
		this.fragmentId = fragmentId;

	}
	
	public abstract void onReceive(Remote remote);

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		LogUtil.vendor("fragment: " + getClass().getSimpleName() + " onActivityCreated()");

		destroyed = false;

		proxy.bind(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		LogUtil.vendor("fragment: " + getClass().getSimpleName() + " onResume()");
	}
	
	public void onDestroy() {
		super.onDestroy();

		LogUtil.vendor("fragment: " + getClass().getSimpleName() + " onDestroy()");

		destroyed = true;

		proxy.bind(false);
	}

	public Remote execute(Remote remote) {
		TViewWatcher.newInstance().execute(remote);
		return remote;
	}
	
	public Remote executeBackground(Remote remote) {
		TViewWatcher.newInstance().executeBackground(remote);
		return remote;
	}
	
	protected final Handler getHandler() {
		return handler;
	}
	
	protected final void postRunnable(final Runnable runnable) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				// validate
				// TODO use getActivity ?
				if (!isAdded()) {
					return;
				}

				// run
				runnable.run();
			}
		});
	}

	protected final void postDelayed(final Runnable runnable, long delay) {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// validate
				// TODO use getActivity ?
				if (!isAdded()) {
					return;
				}

				// run
				runnable.run();
			}
		}, delay);
	}

	/**
     * is bound
     *
     * @return bind state
     */
    protected boolean isBound() {
    	return VendorService.isBind;
    }

    /**
     * request bind
     */
    protected void requestBind() {
    	if(isBound()) {
			// handle bound
			handleBound();
		} else {
			// listen bind
			TViewWatcher.newInstance().addServiceBinderListener(mServiceBindListener);
		}
    }

    /**
     * handle bound
     */
    protected void handleBound() {

    }

}
