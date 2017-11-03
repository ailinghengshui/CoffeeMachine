package com.jingye.coffeemac.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jingye.coffeemac.BuildConfig;
import com.jingye.coffeemac.R;
import com.jingye.coffeemac.application.AppConfig;
import com.jingye.coffeemac.beans.CheckInItem;
import com.jingye.coffeemac.common.fragment.TFragment;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.ui.DialogSignUp;
import com.jingye.coffeemac.ui.ProgressDlgHelper;
import com.jingye.coffeemac.ui.SwitchButton;
import com.jingye.coffeemac.util.AESUtil;
import com.jingye.coffeemac.util.StringUtil;
import com.jingye.coffeemac.util.TimeUtil;
import com.jingye.coffeemac.util.ToastUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**This class implements check-in
 * Created by Hades on 2016/10/26.
 */
public class ManagerControlCheckInFragment extends TFragment implements View.OnClickListener {

    public final static String NORMAL_ITEM_DEFAULT="未设置";


    private RecyclerView recyclerView;
    private CheckInAdapter adapter;
    private Button btnSignUp;
    private int mId;
    private int mScrollThreshold=10;

    public ManagerControlCheckInFragment() {
        this.setFragmentId(R.id.checkin_fragment);
    }

    public void  setAdminId(int id){
        this.mId=id;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manager_control_checkin, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);

        getCheckInItem();
    }


    private void getCheckInItem() {

        ProgressDlgHelper.showProgress(getContext(), "正在加载签到内容");

        JSONObject tokenJson = new JSONObject();
        tokenJson.put("sign", "jinyekeji2016");
        tokenJson.put("time", TimeUtil.getNow_millisecond());
        String source = tokenJson.toString();
        String token = null;
        try {
            token = AESUtil.encrypt("442bef40be3e8188", source);
        } catch (Exception e) {
            e.printStackTrace();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        String host = "";
        if (AppConfig.BUILD_SERVER == AppConfig.Build.ONLINE) {
            host = StringUtil.HOST_ONLINE;
        }else if(AppConfig.BUILD_SERVER==AppConfig.Build.TEST){
            host = StringUtil.HOST_TEST;
        }else {
            host = StringUtil.HOST_LOCAL;
        }

        String url = host + "signRecords/signContent";

        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("machineType", BuildConfig.MACHINETYPE);
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, org.json.JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                ProgressDlgHelper.closeProgress();
                Log.d("response", response.toString());
                try {
                    if (response.getInt("statusCode") == 200) {

                        setAdapter(handleSignContent(response.getJSONObject("results")));


                    } else {
                        ToastUtil.showToast(getContext(), "获取签到内容失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, org.json.JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                ProgressDlgHelper.closeProgress();
                ToastUtil.showToast(getContext(), "获取签到内容失败");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d("response", responseString);
                ProgressDlgHelper.closeProgress();
                ToastUtil.showToast(getContext(), "获取签到内容失败");
            }
        });
    }

    private void setAdapter(List<CheckInItem> checkInItems) {
        adapter = new CheckInAdapter();
        adapter.setList(checkInItems);
        adapter.setCheckChangeListener(new ICheckChangeListener() {
            @Override
            public void onCheckChangeListener(List<CheckInItem> checkInItems) {
                for (CheckInItem checkInItem : checkInItems) {
                    if (checkInItem.isStatus()) {
                        btnSignUp.setEnabled(true);
                        return;
                    } else {
                        btnSignUp.setEnabled(false);
                    }
                }
            }
        });
        recyclerView.setAdapter(adapter);
        final GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.isHeader(position) ? manager.getSpanCount() : 1;
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                boolean isSignificantDelta = Math.abs(dy) > mScrollThreshold;
                if (isSignificantDelta) {
                    if (dy > 0) {
                        onScrollUp();
                    } else {
                        onScrollDown();
                    }
                }
            }

        });
        recyclerView.setLayoutManager(manager);
    }

    private void onScrollDown() {
        hideKeyboard();
    }

    private void onScrollUp() {
        hideKeyboard();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(recyclerView.getWindowToken(), 0);
    }


    private List<CheckInItem> handleSignContent(org.json.JSONObject results) throws JSONException {
        List<CheckInItem> checkInItems = new ArrayList<CheckInItem>();

        //add normal signin content
        checkInItems.add(new CheckInItem(true, "常规内容"));


        if (results.has("workContent")) {
            List<CheckInItem> workContent = JSONArray.parseArray(results.getString("workContent"), CheckInItem.class);
            if (workContent.size() > 0) {
                checkInItems.add(new CheckInItem(true, "作业内容"));
                checkInItems.addAll(workContent);
            }
        }
        if (results.has("checkContent")) {
            List<CheckInItem> checkContent = JSONArray.parseArray(results.getString("checkContent"), CheckInItem.class);
            if (checkContent.size() > 0) {
                checkInItems.add(new CheckInItem(true, "作业结束检查内容"));
                checkInItems.addAll(checkContent);
            }
        }
        return checkInItems;
    }

    private void initView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        btnSignUp = (Button) view.findViewById(R.id.btnSignUp);
        btnSignUp.setEnabled(false);

        btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignUp:


                DialogSignUp dialogSignUp=new DialogSignUp();
                dialogSignUp.setList(adapter.getList());
                dialogSignUp.setId(mId);
                dialogSignUp.setNormalString(adapter.getNormalSting(1),adapter.getNormalSting(2),adapter.getNormalSting(3));
                dialogSignUp.show(getActivity().getSupportFragmentManager(),"signupDialog");
//                updateSign(adapter.getList());
                break;

        }
    }

    private void updateSign(List<CheckInItem> selectList) {
        for (CheckInItem checkInItem : selectList) {
            if (checkInItem.isStatus()) {
                Log.d("checkInItem", checkInItem.getName());
            }
        }
    }

    @Override
    public void onReceive(Remote remote) {

    }

    public interface ICheckChangeListener {
        void onCheckChangeListener(List<CheckInItem> checkInItems);
    }

    class CheckInAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


        private static final int CHECK_ITEM_TITLE = 0;
        private static final int CHECK_ITEM_CONTENT = 1;
        private static final int CHECK_ITEM_DEFAULT = 2;
        private  EditText etCheckInDefaultCount1;
        private EditText etCheckInDefaultCount2;
        private  EditText etCheckInDefaultCount3;
        private List<CheckInItem> mList = new ArrayList<CheckInItem>();
        private ICheckChangeListener mCheckChangeListener;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == CHECK_ITEM_CONTENT) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_check_in_content, parent, false);
                return new CheckInContentViewHolder(view);
            }else if(viewType== CHECK_ITEM_DEFAULT){
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_check_in_default, parent, false);
                return new CheckInDefaultViewHolder(view);
            }else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_check_in_title, parent, false);
                return new CheckInTitleViewHolder(view);
            }

        }

        public String getNormalSting(int i){
            switch (i){
                case 1:
                    return TextUtils.isEmpty(etCheckInDefaultCount1.getText().toString())?NORMAL_ITEM_DEFAULT:etCheckInDefaultCount1.getText().toString();
                case 2:
                    return TextUtils.isEmpty(etCheckInDefaultCount2.getText().toString())?NORMAL_ITEM_DEFAULT:etCheckInDefaultCount2.getText().toString();
                case 3:
                    return TextUtils.isEmpty(etCheckInDefaultCount3.getText().toString())?NORMAL_ITEM_DEFAULT:etCheckInDefaultCount3.getText().toString();
                default:
                    return NORMAL_ITEM_DEFAULT;
            }
        }

        public void setCheckChangeListener(ICheckChangeListener listener) {
            this.mCheckChangeListener = listener;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof CheckInContentViewHolder) {
                CheckInContentViewHolder contentViewHolder = (CheckInContentViewHolder) holder;
                contentViewHolder.tvCheckInDesc.setText(mList.get(position).getName());
                contentViewHolder.sbCheckInContent.setChecked(mList.get(position).isStatus());
            } if(holder instanceof CheckInTitleViewHolder){
                CheckInTitleViewHolder titleViewHolder = (CheckInTitleViewHolder) holder;
                titleViewHolder.tvCheckInTitle.setText(mList.get(position).getName());
            }

        }

        public boolean isHeader(int position) {
            if(position==0){
                return true;
            }
            return mList.get(position).isTitle();
        }

        @Override
        public int getItemViewType(int position) {
            if(position==0){
                return CHECK_ITEM_DEFAULT;
            }else{
                return isHeader(position) ? CHECK_ITEM_TITLE : CHECK_ITEM_CONTENT;
            }
        }

        public List<CheckInItem> getList() {

            return mList;
        }

        public void setList(List<CheckInItem> list) {
            this.mList = list;
        }

        public List<CheckInItem> getSelectList() {
            List<CheckInItem> selectList = new ArrayList<CheckInItem>();
            for (CheckInItem checkInItem : mList) {
                if (checkInItem.isStatus()) {
                    selectList.add(checkInItem);
                }
            }
            return selectList;
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }

        public class CheckInTitleViewHolder extends RecyclerView.ViewHolder {

            private final TextView tvCheckInTitle;

            public CheckInTitleViewHolder(View itemView) {
                super(itemView);
                tvCheckInTitle = (TextView) itemView.findViewById(R.id.tvCheckInTitle);
            }
        }

        public class CheckInDefaultViewHolder extends RecyclerView.ViewHolder {

            public CheckInDefaultViewHolder(View itemView) {
                super(itemView);
                etCheckInDefaultCount1 = (EditText) itemView.findViewById(R.id.etCheckInDefaultCount1);
                etCheckInDefaultCount2 = (EditText) itemView.findViewById(R.id.etCheckInDefaultCount2);
                etCheckInDefaultCount3 = (EditText) itemView.findViewById(R.id.etCheckInDefaultCount3);
            }
        }

        public class CheckInContentViewHolder extends RecyclerView.ViewHolder {

            private final TextView tvCheckInDesc;
            private final SwitchButton sbCheckInContent;

            public CheckInContentViewHolder(View itemView) {
                super(itemView);
                tvCheckInDesc = (TextView) itemView.findViewById(R.id.tvCheckInDesc);
                sbCheckInContent = (SwitchButton) itemView.findViewById(R.id.sbCheckInContent);


                sbCheckInContent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                        if (b) {
                            mList.get(getLayoutPosition()).setStatus(true);
                            if (mCheckChangeListener != null) {
                                mCheckChangeListener.onCheckChangeListener(mList);
                            }
                        } else {
                            mList.get(getLayoutPosition()).setStatus(false);
                            if (mCheckChangeListener != null) {
                                mCheckChangeListener.onCheckChangeListener(mList);
                            }
                        }

                    }
                });
            }
        }
    }
}
