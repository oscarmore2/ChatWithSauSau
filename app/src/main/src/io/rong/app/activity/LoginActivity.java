package io.rong.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.*;
import android.os.Process;
import android.support.v7.app.ActionBar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sea_monster.exception.BaseException;
import com.sea_monster.network.AbstractHttpRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.rong.app.DemoContext;
import io.rong.app.R;
import io.rong.app.RongCloudEvent;
import io.rong.app.database.DBManager;
import io.rong.app.database.UserInfos;
import io.rong.app.database.UserInfosDao;
import io.rong.app.model.ApiResult;
import io.rong.app.model.Friends;
import io.rong.app.model.Groups;
import io.rong.app.model.User;
import io.rong.app.ui.EditTextHolder;
import io.rong.app.ui.LoadingDialog;
import io.rong.app.ui.WinToast;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;

/**
 * Created by Bob on 2015/1/30.
 */
public class LoginActivity extends BaseApiActivity implements View.OnClickListener, Handler.Callback, EditTextHolder.OnEditTextFocusChangeListener {
    private static final String TAG = "LoginActivity";
    /**
     * 用户账户
     */
    private EditText mUserNameEt;
    /**
     * 密码
     */
    private EditText mPassWordEt;
    /**
     * 登录button
     */
    private Button mSignInBt;
    /**
     * 设备id
     */
    private String mDeviceId;
    /**
     * 忘记密码
     */
    private TextView mFogotPassWord;
    /**
     * 注册
     */
    private TextView mRegister;
    /**
     * 输入用户名删除按钮
     */
    private FrameLayout mFrUserNameDelete;
    /**
     * 输入密码删除按钮
     */
    private FrameLayout mFrPasswordDelete;
    /**
     * logo
     */
    private ImageView mLoginImg;
    /**
     * 软键盘的控制
     */
    private InputMethodManager mSoftManager;
    /**
     * 是否展示title
     */
    private RelativeLayout mIsShowTitle;
    /**
     * 左侧title
     */
    private TextView mLeftTitle;
    /**
     * 右侧title
     */
    private TextView mRightTitle;


    private static final int REQUEST_CODE_REGISTER = 200;
    public static final String INTENT_IMAIL = "intent_email";
    public static final String INTENT_PASSWORD = "intent_password";
    private static final int HANDLER_LOGIN_SUCCESS = 1;
    private static final int HANDLER_LOGIN_FAILURE = 2;
    private static final int HANDLER_LOGIN_HAS_FOCUS = 3;
    private static final int HANDLER_LOGIN_HAS_NO_FOCUS = 4;


    private LoadingDialog mDialog;
    private AbstractHttpRequest<User> loginHttpRequest;
    private AbstractHttpRequest<User> getTokenHttpRequest;
    private AbstractHttpRequest<Friends> getUserInfoHttpRequest;
    private AbstractHttpRequest<Groups> mGetMyGroupsRequest;

    private Handler mHandler;
    private List<User> mUserList;
    private List<ApiResult> mResultList;
    private ImageView mImgBackgroud;
    EditTextHolder mEditUserNameEt;
    EditTextHolder mEditPassWordEt;

    List<UserInfos> friendsList = new ArrayList<UserInfos>();
    UserInfosDao mUserInfosDao;
    String userName;
    private boolean isFirst = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.de_ac_login);
        initView();
        initData();
    }

    protected void initView() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        mSoftManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mUserInfosDao = DBManager.getInstance(LoginActivity.this).getDaoSession().getUserInfosDao();
        mLoginImg = (ImageView) findViewById(R.id.de_login_logo);
        mUserNameEt = (EditText) findViewById(R.id.app_username_et);
        mPassWordEt = (EditText) findViewById(R.id.app_password_et);
        mSignInBt = (Button) findViewById(R.id.app_sign_in_bt);
        mRegister = (TextView) findViewById(R.id.de_login_register);
        mFogotPassWord = (TextView) findViewById(R.id.de_login_forgot);
        mImgBackgroud = (ImageView) findViewById(R.id.de_img_backgroud);
        mFrUserNameDelete = (FrameLayout) findViewById(R.id.fr_username_delete);
        mFrPasswordDelete = (FrameLayout) findViewById(R.id.fr_pass_delete);
        mIsShowTitle = (RelativeLayout) findViewById(R.id.de_merge_rel);
        mLeftTitle = (TextView) findViewById(R.id.de_left);
        mRightTitle = (TextView) findViewById(R.id.de_right);
        mUserList = new ArrayList<User>();
        mResultList = new ArrayList<ApiResult>();

        mSignInBt.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        mLeftTitle.setOnClickListener(this);
        mRightTitle.setOnClickListener(this);
        mHandler = new Handler(this);
        mDialog = new LoadingDialog(this);

        mEditUserNameEt = new EditTextHolder(mUserNameEt, mFrUserNameDelete, null);
        mEditPassWordEt = new EditTextHolder(mPassWordEt, mFrPasswordDelete, null);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.translate_anim);
                mImgBackgroud.startAnimation(animation);
            }
        });


    }

    protected void initData() {

        if (DemoContext.getInstance() != null) {
            String email = DemoContext.getInstance().getSharedPreferences().getString(INTENT_IMAIL, "");
            String password = DemoContext.getInstance().getSharedPreferences().getString(INTENT_PASSWORD, "");
            mUserNameEt.setText(email);
            mPassWordEt.setText(password);
        }

        TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mDeviceId = mTelephonyManager.getDeviceId();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mUserNameEt.setOnClickListener(LoginActivity.this);
                mPassWordEt.setOnClickListener(LoginActivity.this);
                mEditPassWordEt.setmOnEditTextFocusChangeListener(LoginActivity.this);
                mEditUserNameEt.setmOnEditTextFocusChangeListener(LoginActivity.this);
            }
        }, 200);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.app_sign_in_bt://登录

                userName = mUserNameEt.getEditableText().toString();
                String passWord = mPassWordEt.getEditableText().toString();
                String name = null;
                if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(passWord)) {
                    WinToast.toast(this, R.string.login_erro_is_null);
                    return;
                }

                if (mDialog != null && !mDialog.isShowing()) {
                    mDialog.show();
                }
                //发起登录 http请求 (注：非融云SDK接口，是demo接口)
                if (DemoContext.getInstance() != null) {
                    //如果切换了一个用户，token和 cookie 都需要重新获取
                    if (DemoContext.getInstance() != null) {
                        name = DemoContext.getInstance().getSharedPreferences().getString("DEMO_USERNAME", "DEFAULT");
                    }

                    if (!userName.equals(name)) {

                        loginHttpRequest = DemoContext.getInstance().getDemoApi().login(userName, passWord, this);
                        isFirst = true;
                    } else {
                        isFirst = false;
                        String cookie = DemoContext.getInstance().getSharedPreferences().getString("DEMO_COOKIE", "DEFAULT");
                        String token = DemoContext.getInstance().getSharedPreferences().getString("DEMO_TOKEN", "DEFAULT");
                        if (!cookie.equals("DEFAULT") && !token.equals("DEFAULT")) {
                            httpGetTokenSuccess(token);
                        } else {
                            loginHttpRequest = DemoContext.getInstance().getDemoApi().login(userName, passWord, this);
                        }

                    }
                }

                break;
            case R.id.de_left://注册
            case R.id.de_login_register://注册
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivityForResult(intent, REQUEST_CODE_REGISTER);
                break;
            case R.id.de_login_forgot://忘记密码
                WinToast.toast(this, "忘记密码");
                break;
            case R.id.de_right://忘记密码
                Intent intent1 = new Intent(this, RegisterActivity.class);
                startActivityForResult(intent1, REQUEST_CODE_REGISTER);
                break;

            case R.id.app_username_et:
            case R.id.app_password_et:
                Message mess = Message.obtain();
                mess.what = HANDLER_LOGIN_HAS_FOCUS;
                mHandler.sendMessage(mess);
                break;

        }
    }

    @Override
    public boolean handleMessage(Message msg) {

        if (msg.what == HANDLER_LOGIN_FAILURE) {

            if (mDialog != null)
                mDialog.dismiss();
            WinToast.toast(LoginActivity.this, R.string.login_failure);
            startActivity(new Intent(this, MainActivity.class));

            finish();

        } else if (msg.what == HANDLER_LOGIN_SUCCESS) {
            if (mDialog != null)
                mDialog.dismiss();
            WinToast.toast(LoginActivity.this, R.string.login_success);

            startActivity(new Intent(this, MainActivity.class));

            finish();

        } else if (msg.what == HANDLER_LOGIN_HAS_FOCUS) {
            mLoginImg.setVisibility(View.GONE);
            mRegister.setVisibility(View.GONE);
            mFogotPassWord.setVisibility(View.GONE);
            mIsShowTitle.setVisibility(View.VISIBLE);
            mLeftTitle.setText(R.string.app_sign_up);
            mRightTitle.setText(R.string.app_fogot_password);
        } else if (msg.what == HANDLER_LOGIN_HAS_NO_FOCUS) {
            mLoginImg.setVisibility(View.VISIBLE);
            mRegister.setVisibility(View.VISIBLE);
            mFogotPassWord.setVisibility(View.VISIBLE);
            mIsShowTitle.setVisibility(View.GONE);
        }

        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_REGISTER && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                mUserNameEt.setText(data.getStringExtra(INTENT_IMAIL));
                mPassWordEt.setText(data.getStringExtra(INTENT_PASSWORD));
            }
        }

    }

    private void httpLoginSuccess(User user) {


        if (user.getCode() == 200) {

            getTokenHttpRequest = DemoContext.getInstance().getDemoApi().getToken(this);
        }

    }


    private void httpGetTokenSuccess(String token) {

        try {
            /**
             * IMKit SDK调用第二步
             *
             * 建立与服务器的连接
             *
             * 详见API
             * http://docs.rongcloud.cn/api/android/imkit/index.html
             */
            Log.e("LoginActivity", "---------onSuccess gettoken----------:" + token);
            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                        @Override
                        public void onTokenIncorrect() {
                            Log.e("LoginActivity", "---------onTokenIncorrect userId----------:");
                        }

                        @Override
                        public void onSuccess(String userId) {
                            Log.e("LoginActivity", "---------onSuccess userId----------:" + userId);

                            if (isFirst) {

                                getUserInfoHttpRequest = DemoContext.getInstance().getDemoApi().getFriends(LoginActivity.this);
                                DemoContext.getInstance().deleteUserInfos();

                            } else {
                                final List<UserInfos> list = mUserInfosDao.loadAll();
                                if (list != null && list.size() > 0) {
                                    mHandler.obtainMessage(HANDLER_LOGIN_SUCCESS).sendToTarget();
                                } else {
                                    //请求网络
                                    getUserInfoHttpRequest = DemoContext.getInstance().getDemoApi().getFriends(LoginActivity.this);
                                }
                            }
                            SharedPreferences.Editor edit = DemoContext.getInstance().getSharedPreferences().edit();
                            edit.putString("DEMO_USERID", userId);
                            edit.putString("DEMO_USERNAME", userName);
                            edit.apply();

                            RongCloudEvent.getInstance().setOtherListener();

                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode e) {
                            mHandler.obtainMessage(HANDLER_LOGIN_FAILURE).sendToTarget();
                            Log.e("LoginActivity", "---------onError ----------:" + e);
                        }
                    }
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (DemoContext.getInstance() != null) {
            mGetMyGroupsRequest = DemoContext.getInstance().getDemoApi().getMyGroups(LoginActivity.this);
        }

        if (DemoContext.getInstance() != null) {
            SharedPreferences.Editor editor = DemoContext.getInstance().getSharedPreferences().edit();
            editor.putString(INTENT_PASSWORD, mPassWordEt.getText().toString());
            editor.putString(INTENT_IMAIL, mUserNameEt.getText().toString());
            editor.apply();
        }
    }


    @Override
    public void onCallApiSuccess(AbstractHttpRequest request, Object obj) {

        if (mGetMyGroupsRequest != null && mGetMyGroupsRequest.equals(request)) {
            getMyGroupApiSuccess(obj);
        } else if (loginHttpRequest != null && loginHttpRequest.equals(request)) {
            loginApiSuccess(obj);
        } else if (getTokenHttpRequest != null && getTokenHttpRequest.equals(request)) {
            getTokenApiSuccess(obj);
        } else if (getUserInfoHttpRequest != null && getUserInfoHttpRequest.equals(request)) {
            getFriendsApiSuccess(obj);
        }
    }


    @Override
    public void onCallApiFailure(AbstractHttpRequest request, BaseException e) {

        if (loginHttpRequest != null && loginHttpRequest.equals(request)) {
            if (mDialog != null)
                mDialog.dismiss();
        } else if (getTokenHttpRequest != null && getTokenHttpRequest.equals(request)) {
            if (mDialog != null)
                mDialog.dismiss();
        }
    }

    /**
     * 获得好友列表
     *
     * @param obj
     */
    private void getFriendsApiSuccess(Object obj) {

        //获取好友列表接口  返回好友数据  (注：非融云SDK接口，是demo接口)

        if (obj instanceof Friends) {
            final Friends friends = (Friends) obj;
            if (friends.getCode() == 200) {
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        ArrayList<UserInfo> friendLists = new ArrayList<UserInfo>();

                        for (int i = 0; i < friends.getResult().size(); i++) {
                            UserInfos userInfos = new UserInfos();

                            userInfos.setUserid(friends.getResult().get(i).getId());
                            userInfos.setUsername(friends.getResult().get(i).getUsername());
                            userInfos.setStatus("5");
                            if (friends.getResult().get(i).getPortrait() != null)
                                userInfos.setPortrait(friends.getResult().get(i).getPortrait());
                            friendsList.add(userInfos);
                        }

                        UserInfos addFriend = new UserInfos();
                        addFriend.setUsername("新好友消息");
                        addFriend.setUserid("10000");
                        addFriend.setPortrait("test");
                        addFriend.setStatus("0");
                        UserInfos customer = new UserInfos();
                        customer.setUsername("客服");
                        customer.setUserid("kefu114");
                        customer.setPortrait("http://jdd.kefu.rongcloud.cn/image/service_80x80.png");
                        customer.setStatus("0");
                        friendsList.add(customer);
                        friendsList.add(addFriend);

                        if (friendsList != null) {
                            for (UserInfos friend : friendsList) {
                                UserInfos f = new UserInfos();
                                f.setUserid(friend.getUserid());
                                f.setUsername(friend.getUsername());
                                f.setPortrait(friend.getPortrait());
                                f.setStatus(friend.getStatus());
                                mUserInfosDao.insertOrReplace(f);
                            }
                        }
                        mHandler.obtainMessage(HANDLER_LOGIN_SUCCESS).sendToTarget();
                    }

                });
            }
        }
    }

    private void getMyGroupApiSuccess(Object obj) {
        if (obj instanceof Groups) {
            final Groups groups = (Groups) obj;

            if (groups.getCode() == 200) {
                List<Group> grouplist = new ArrayList<Group>();
                if (groups.getResult() != null) {
                    for (int i = 0; i < groups.getResult().size(); i++) {

                        String id = groups.getResult().get(i).getId();
                        String name = groups.getResult().get(i).getName();
                        if (groups.getResult().get(i).getPortrait() != null) {
                            Uri uri = Uri.parse(groups.getResult().get(i).getPortrait());
                            grouplist.add(new Group(id, name, uri));
                        } else {
                            grouplist.add(new Group(id, name, null));
                        }
                    }
                    HashMap<String, Group> groupM = new HashMap<String, Group>();
                    for (int i = 0; i < grouplist.size(); i++) {
                        groupM.put(groups.getResult().get(i).getId(), grouplist.get(i));
                        Log.e("login", "------get Group id---------" + groups.getResult().get(i).getId());
                    }

                    if (DemoContext.getInstance() != null)
                        DemoContext.getInstance().setGroupMap(groupM);

                    if (grouplist.size() > 0)
                        RongIM.getInstance().getRongIMClient().syncGroup(grouplist, new RongIMClient.OperationCallback() {
                            @Override
                            public void onSuccess() {
                                Log.e(TAG, "---syncGroup-onSuccess---");
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {
                                Log.e(TAG, "---syncGroup-onError---");
                            }
                        });
                }
            } else {
//                    WinToast.toast(this, groups.getCode());
            }
        }
    }

    private void getTokenApiSuccess(Object obj) {

        if (obj instanceof User) {
            final User user = (User) obj;
            if (user.getCode() == 200) {

                httpGetTokenSuccess(user.getResult().getToken());

                SharedPreferences.Editor edit = DemoContext.getInstance().getSharedPreferences().edit();
                edit.putString("DEMO_TOKEN", user.getResult().getToken());
                edit.putBoolean("DEMO_ISFIRST", false);
                edit.apply();
                Log.e(TAG, "------getTokenHttpRequest -success--" + user.getResult().getToken());
            } else if (user.getCode() == 110) {
                WinToast.toast(LoginActivity.this, "请先登陆");
            } else if (user.getCode() == 111) {
                WinToast.toast(LoginActivity.this, "cookie 为空");
            }
        }
    }

    private void loginApiSuccess(Object obj) {

        if (obj instanceof User) {

            final User user = (User) obj;

            if (user.getCode() == 200) {
                if (DemoContext.getInstance() != null && user.getResult() != null) {
                    SharedPreferences.Editor edit = DemoContext.getInstance().getSharedPreferences().edit();
                    edit.putString("DEMO_USER_ID", user.getResult().getId());
                    edit.putString("DEMO_USER_NAME", user.getResult().getUsername());
                    edit.putString("DEMO_USER_PORTRAIT", user.getResult().getPortrait());
                    edit.apply();
                    Log.e(TAG, "-------login success------");

                    httpLoginSuccess(user);
                }
            } else if (user.getCode() == 103) {

                if (mDialog != null)
                    mDialog.dismiss();

                WinToast.toast(LoginActivity.this, "密码错误");
            } else if (user.getCode() == 104) {

                if (mDialog != null)
                    mDialog.dismiss();

                WinToast.toast(LoginActivity.this, "账号错误");
            }
        }
    }


    @Override
    public void onEditTextFocusChange(View v, boolean hasFocus) {
        Message mess = Message.obtain();
        switch (v.getId()) {
            case R.id.app_username_et:
            case R.id.app_password_et:
                if (hasFocus) {
                    mess.what = HANDLER_LOGIN_HAS_FOCUS;
                }
                mHandler.sendMessage(mess);
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                mSoftManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                Message mess = Message.obtain();
                mess.what = HANDLER_LOGIN_HAS_NO_FOCUS;
                mHandler.sendMessage(mess);
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        event.getKeyCode();
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_ESCAPE:
                Message mess = Message.obtain();
                mess.what = HANDLER_LOGIN_HAS_NO_FOCUS;
                mHandler.sendMessage(mess);
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    protected void onPause() {
        super.onPause();
        if (mSoftManager == null) {
            mSoftManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        if (getCurrentFocus() != null) {
            mSoftManager.hideSoftInputFromWindow(getCurrentFocus()
                    .getWindowToken(), 0);// 隐藏软键盘
        }
    }

}
