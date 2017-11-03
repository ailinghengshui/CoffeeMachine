package com.jingye.coffeemac.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.jingye.coffeemac.BuildConfig;
import com.jingye.coffeemac.R;
import com.jingye.coffeemac.application.AppConfig;
import com.jingye.coffeemac.beans.CheckInItem;
import com.jingye.coffeemac.fragment.ManagerControlCheckInFragment;
import com.jingye.coffeemac.util.AESUtil;
import com.jingye.coffeemac.util.StringUtil;
import com.jingye.coffeemac.util.TimeUtil;
import com.jingye.coffeemac.util.ToastUtil;
import com.jingye.coffeemac.util.U;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;


/**
 * Created by Hades on 2016/10/31.
 */

public class DialogSignUp extends BaseDialog implements View.OnClickListener {

    private RecyclerView rycleSignUp;


    private Button btnCancel;
    private Button btnOk;
    private DialogSignUpAdapter adapter;
    private List<CheckInItem> mCheckInItem;
    private int mId;
    private String mNormalSting1;
    private String mNormalSting2;
    private String mNormalSting3;


    public void setList(List<CheckInItem> checkInItems) {
        this.mCheckInItem = checkInItems;
    }


    public void setNormalString(String normalString1, String normalString2, String normalString3) {
        this.mNormalSting1 = normalString1;
        this.mNormalSting2 = normalString2;
        this.mNormalSting3 = normalString3;
    }


    @Override
    protected View onBaseCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_sign_up, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rycleSignUp = (RecyclerView) view.findViewById(R.id.rycleSignUp);

        adapter = new DialogSignUpAdapter();
        rycleSignUp.setAdapter(adapter);
        final GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.isHeader(position) ? manager.getSpanCount() : 1;
            }
        });
        rycleSignUp.setLayoutManager(manager);

        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnOk = (Button) view.findViewById(R.id.btnOk);

        btnCancel.setOnClickListener(this);
        btnOk.setOnClickListener(this);
    }

    @Override
    protected int setDialogWidth() {
        return 1460;
    }

    @Override
    public void onClick(View view) {
        if (isVisible()) {
            switch (view.getId()) {
                case R.id.btnCancel:
                    break;
                case R.id.btnOk:
                    updateSign(mCheckInItem, mId);
                    break;
                default:
                    break;
            }
            dismiss();
        }
    }

    private void updateSign(List<CheckInItem> selectList, int id) {
        ProgressDlgHelper.showProgress(getContext(), "正在上传签到内容");

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

        String url = host + "signRecords/signIn";

        RequestParams params = new RequestParams();
        params.put("machineType", BuildConfig.MACHINETYPE);
        params.put("token", token);
        params.put("login", id);
        params.put("vendingMachineId", U.getMyVendorNum());


        try {
            JSONArray defaultCheckInItems = new JSONArray();
            if (!ManagerControlCheckInFragment.NORMAL_ITEM_DEFAULT.equals(mNormalSting1)) {
                org.json.JSONObject jsonObject1 = new org.json.JSONObject();
                jsonObject1.put("id", 1020);
                jsonObject1.put("value",mNormalSting1);
                defaultCheckInItems.put(jsonObject1);

            }
            if (!ManagerControlCheckInFragment.NORMAL_ITEM_DEFAULT.equals(mNormalSting2)) {
                org.json.JSONObject jsonObject2 = new org.json.JSONObject();
                jsonObject2.put("id", 1021);
                jsonObject2.put("value",mNormalSting2);
                defaultCheckInItems.put(jsonObject2);
            }
            if (!ManagerControlCheckInFragment.NORMAL_ITEM_DEFAULT.equals(mNormalSting3)) {
                org.json.JSONObject jsonObject3 = new org.json.JSONObject();
                jsonObject3.put("id", 1022);
                jsonObject3.put("value",mNormalSting3);
                defaultCheckInItems.put(jsonObject3);
            }

            Log.d("defaultContent", defaultCheckInItems.toString());
            params.put("defaultContent", defaultCheckInItems.toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            if (getCheckInItemPosition(selectList, "作业内容") != -1) {
                params.put("workContent", getItemContent(selectList, "作业内容"));
            }
            if (getCheckInItemPosition(selectList, "作业结束检查内容") != -1) {
                params.put("checkContent", getItemContent(selectList, "作业结束检查内容"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, org.json.JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                ProgressDlgHelper.closeProgress();

                Log.d("response", response.toString());
                try {
                    if (response.getInt("statusCode") == 200) {
                        ToastUtil.showToast(getContext(), "上传签到内容成功");

                    } else {
                        ToastUtil.showToast(getContext(), "上传签到内容失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, org.json.JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                ProgressDlgHelper.closeProgress();
                ToastUtil.showToast(getContext(), "上传签到内容失败");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d("response", responseString);
                ProgressDlgHelper.closeProgress();
                ToastUtil.showToast(getContext(), "上传签到内容失败");
            }
        });

    }

    private String getItemContent(List<CheckInItem> selectList, String name) throws JSONException {

        JSONArray jsonArray = new JSONArray();
        boolean isNeed = true;
        int position = getCheckInItemPosition(selectList, name);
        if (position != -1) {
            for (int i = position + 1; i < selectList.size(); i++) {
                if (!selectList.get(i).isTitle()) {
                    if (isNeed) {
                        org.json.JSONObject jsonObject = new org.json.JSONObject();
                        jsonObject.put("id", selectList.get(i).getId());
                        jsonObject.put("status", selectList.get(i).isStatus());
                        jsonArray.put(jsonObject);
                    }
                } else {
                    isNeed = false;
                }
            }
        }

        Log.d("response---", jsonArray.toString());
        return jsonArray.toString();
    }

    /**
     * 获取String在selcectList里面的位置
     *
     * @param selectList
     * @param string     "作业内容" "作业结束检查内容"
     * @return 没找到返回-1
     */
    private int getCheckInItemPosition(List<CheckInItem> selectList, String string) {
        for (int i = 0; i < selectList.size(); i++) {
            if (string.equals(selectList.get(i).getName()) && selectList.get(i).isTitle()) {
                return i;
            }
        }
        return -1;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    class DialogSignUpAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int SIGN_UP_ITEM_TITLE = 0;
        private static final int SIGN_UP_ITEM_CONTENT = 1;
        private static final int SIGN_UP_ITEM_NORMAL = 2;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == SIGN_UP_ITEM_CONTENT) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sign_up_content, parent, false);
                return new SignUpContentViewHolder(view);
            } else if (viewType == SIGN_UP_ITEM_TITLE) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sign_up_title, parent, false);
                return new SignUpTitleViewHolder(view);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sign_up_normal, parent, false);
                return new SignUpNormalViewHolder(view);
            }

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof SignUpContentViewHolder) {
                SignUpContentViewHolder contentViewHolder = (SignUpContentViewHolder) holder;
                if (mCheckInItem.get(position).isStatus()) {
                    contentViewHolder.tvSignUpDesc.setText(getString(R.string.str_with_right_right, mCheckInItem.get(position).getName()));
                    contentViewHolder.tvSignUpDesc.setTextColor(getResources().getColor(R.color.dark_orange));
                } else {
                    contentViewHolder.tvSignUpDesc.setText(getString(R.string.str_with_right_wrong, mCheckInItem.get(position).getName()));
                    contentViewHolder.tvSignUpDesc.setTextColor(getResources().getColor(R.color.black));
                }
            } else if (holder instanceof SignUpTitleViewHolder) {
                SignUpTitleViewHolder titleViewHolder = (SignUpTitleViewHolder) holder;
                titleViewHolder.tvSignUpTitle.setText(mCheckInItem.get(position).getName());
            } else {
                SignUpNormalViewHolder normalViewHolder = (SignUpNormalViewHolder) holder;
                if (ManagerControlCheckInFragment.NORMAL_ITEM_DEFAULT.equals(mNormalSting1)) {
                    normalViewHolder.tvSignUp1.setText(getString(R.string.signup1, "0"));
                    normalViewHolder.tvSignUp1.setTextColor(getResources().getColor(R.color.black));

                } else {
                    normalViewHolder.tvSignUp1.setText(getString(R.string.signup1, mNormalSting1));
                    normalViewHolder.tvSignUp1.setTextColor(getResources().getColor(R.color.dark_orange));
                }

                if (ManagerControlCheckInFragment.NORMAL_ITEM_DEFAULT.equals(mNormalSting2)) {
                    normalViewHolder.tvSignUp2.setText(getString(R.string.signup2, "0"));
                    normalViewHolder.tvSignUp2.setTextColor(getResources().getColor(R.color.black));

                } else {
                    normalViewHolder.tvSignUp2.setText(getString(R.string.signup2, mNormalSting2));
                    normalViewHolder.tvSignUp2.setTextColor(getResources().getColor(R.color.dark_orange));
                }
                if (ManagerControlCheckInFragment.NORMAL_ITEM_DEFAULT.equals(mNormalSting3)) {
                    normalViewHolder.tvSignUp3.setText(getString(R.string.signup3, "0"));
                    normalViewHolder.tvSignUp3.setTextColor(getResources().getColor(R.color.black));

                } else {
                    normalViewHolder.tvSignUp3.setText(getString(R.string.signup3, mNormalSting3));
                    normalViewHolder.tvSignUp3.setTextColor(getResources().getColor(R.color.dark_orange));
                }

            }

        }

        public boolean isHeader(int position) {
            if (position == 0) {
                return true;
            }
            return mCheckInItem.get(position).isTitle();
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return SIGN_UP_ITEM_NORMAL;
            }
            return isHeader(position) ? SIGN_UP_ITEM_TITLE : SIGN_UP_ITEM_CONTENT;
        }


        @Override
        public int getItemCount() {
            return mCheckInItem == null ? 0 : mCheckInItem.size();
        }


        public class SignUpTitleViewHolder extends RecyclerView.ViewHolder {

            private final TextView tvSignUpTitle;

            public SignUpTitleViewHolder(View itemView) {
                super(itemView);
                tvSignUpTitle = (TextView) itemView.findViewById(R.id.tvSignUpTitle);
            }
        }

        public class SignUpContentViewHolder extends RecyclerView.ViewHolder {

            private final TextView tvSignUpDesc;

            public SignUpContentViewHolder(View itemView) {
                super(itemView);
                tvSignUpDesc = (TextView) itemView.findViewById(R.id.tvSignUpDesc);

            }
        }

        public class SignUpNormalViewHolder extends RecyclerView.ViewHolder {


            private final TextView tvSignUp1;
            private final TextView tvSignUp2;
            private final TextView tvSignUp3;

            public SignUpNormalViewHolder(View itemView) {
                super(itemView);
                tvSignUp1 = (TextView) itemView.findViewById(R.id.tvSignUp1);
                tvSignUp2 = (TextView) itemView.findViewById(R.id.tvSignUp2);
                tvSignUp3 = (TextView) itemView.findViewById(R.id.tvSignUp3);


            }
        }

    }
}
