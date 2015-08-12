package io.rong.app.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sea_monster.exception.BaseException;
import com.sea_monster.network.AbstractHttpRequest;
import com.sea_monster.resource.Resource;

import java.util.ArrayList;
import java.util.List;

import io.rong.app.DemoContext;
import io.rong.app.R;
import io.rong.app.model.Friend;
import io.rong.app.model.Status;
import io.rong.app.ui.LoadingDialog;
import io.rong.app.ui.WinToast;
import io.rong.app.utils.Constants;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

/**
 * Created by Bob on 2015/4/7.
 * <p/>
 * 个人详情
 */
public class PersonalDetailActivity extends BaseApiActivity implements View.OnClickListener {

    private AsyncImageView mPersonalImg;
    private TextView mPersonalName;
    private TextView mPersonalId;
    private TextView mPersonalArea;
    private TextView mPersonalsignature;
    private Button mSendMessage;
    private Button mSendVoip;
    protected List<Friend> mFriendsList;
    private Friend mFriend;
    private String friendid;
    protected List<UserInfo> mUserInfoList;
    private AbstractHttpRequest<Status> mDeleteFriendRequest;
    UserInfo userInfo;
    private LoadingDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.de_fr_personal_intro);
        initView();
        initData();
    }


    protected void initView() {
        getSupportActionBar().setTitle(R.string.de_actionbar_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.de_actionbar_back);
        mPersonalImg = (AsyncImageView) findViewById(R.id.personal_portrait);
        mPersonalName = (TextView) findViewById(R.id.personal_name);
        mPersonalId = (TextView) findViewById(R.id.personal_id);
        mPersonalArea = (TextView) findViewById(R.id.personal_area);
        mPersonalsignature = (TextView) findViewById(R.id.personal_signature);
        mSendMessage = (Button) findViewById(R.id.send_message);
        mSendVoip = (Button) findViewById(R.id.send_voip);
        mUserInfoList = new ArrayList<UserInfo>();
        mDialog = new LoadingDialog(this);
    }

    protected void initData() {
        mSendMessage.setOnClickListener(this);
        mSendVoip.setOnClickListener(this);
//
        if (getIntent().hasExtra("PERSONAL") && DemoContext.getInstance() != null) {
            friendid = getIntent().getStringExtra("PERSONAL");
            userInfo = DemoContext.getInstance().getUserInfoById(friendid);
            mPersonalName.setText(userInfo.getName().toString());
            mPersonalId.setText("Id:" + userInfo.getUserId().toString());
        }

        if (getIntent().hasExtra("USER")) {

            userInfo = getIntent().getParcelableExtra("USER");
            mPersonalName.setText(userInfo.getName().toString());
            mPersonalImg.setResource(new Resource(userInfo.getPortraitUri()));

            if (getIntent().hasExtra("SEARCH_CONVERSATION")) {
                String conversation = getIntent().getStringExtra("SEARCH_CONVERSATION");
                if (conversation.equals("CUSTOMER_SERVICE")) {
                    mSendMessage.setVisibility(View.GONE);
                }
            }
            if (userInfo.getUserId().equals(DemoContext.getInstance().getSharedPreferences().getString("DEMO_USER_ID", "defult"))) {
                mSendMessage.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void onCallApiSuccess(AbstractHttpRequest request, Object obj) {
        if (mDeleteFriendRequest == request) {
            if (mDialog != null)
                mDialog.dismiss();
            if (obj instanceof Status) {
                final Status status = (Status) obj;
                if (status.getCode() == 200) {
                    WinToast.toast(this, "删除好友成功");
                    if (DemoContext.getInstance() != null && friendid != null) {

                        DemoContext.getInstance().updateUserInfos(friendid, "2");

                        Intent intent = new Intent();
                        this.setResult(Constants.DELETE_USERNAME_REQUESTCODE, intent);

                    }
                }
            }
        }

    }

    @Override
    public void onCallApiFailure(AbstractHttpRequest request, BaseException e) {
        if (mDialog != null)
            mDialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_message:
                if (RongIM.getInstance() != null && DemoContext.getInstance() != null) {
                    if (friendid != null)
                        RongIM.getInstance().startPrivateChat(PersonalDetailActivity.this, friendid, DemoContext.getInstance().getUserInfoById(friendid).getName().toString());
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.per_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.per_item1://加入黑名单
                if (DemoContext.getInstance() != null && friendid != null) {
                    RongIM.getInstance().getRongIMClient().addToBlacklist(friendid, new RongIMClient.AddToBlackCallback() {
                        @Override
                        public void onSuccess() {
                            WinToast.toast(PersonalDetailActivity.this, "加入黑名单成功");
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
                }

                break;
            case R.id.per_item2://删除好友
                final AlertDialog.Builder alterDialog = new AlertDialog.Builder(this);
                alterDialog.setMessage("是否删除好友？");
                alterDialog.setCancelable(true);

                alterDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (DemoContext.getInstance() != null && friendid != null) {
                            mDeleteFriendRequest = DemoContext.getInstance().getDemoApi().deletefriends(friendid, PersonalDetailActivity.this);
                            if (RongIM.getInstance() != null) {
                                RongIM.getInstance().getRongIMClient().removeConversation(Conversation.ConversationType.PRIVATE, friendid);
                            }
                        }
                        if (mDialog != null && !mDialog.isShowing()) {
                            mDialog.show();
                        }
                    }
                });
                alterDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alterDialog.show();

                break;
            case android.R.id.home:
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

}
