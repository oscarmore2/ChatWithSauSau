package io.rong.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import io.rong.app.DemoContext;
import io.rong.app.R;
import io.rong.app.model.User;
import io.rong.app.ui.LoadingDialog;
import io.rong.app.ui.WinToast;
import io.rong.app.utils.Constants;
import io.rong.imlib.model.UserInfo;
import com.sea_monster.exception.BaseException;
import com.sea_monster.network.AbstractHttpRequest;
import io.rong.imkit.widget.AsyncImageView ;
import com.sea_monster.resource.Resource;


/**
 * Created by Bob on 2015/3/26.
 *
 * 搜索好友点详情
 */
public class DePersonalDetailActivity extends BaseApiActivity implements View.OnClickListener {


    private AsyncImageView mFriendImg;
    private TextView mFriendName;
    private Button mAddFriend;
    private AbstractHttpRequest<User> mUserHttpRequest;
    private LoadingDialog mDialog;
    private UserInfo user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.de_ac_personal_detail);
        initView();
        initData();

    }


    protected void initView() {
        getSupportActionBar().setTitle(R.string.public_add_address);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.de_actionbar_back);
        mFriendImg = (AsyncImageView) findViewById(R.id.friend_adapter_img);
        mFriendName = (TextView) findViewById(R.id.de_name);
        mAddFriend = (Button) findViewById(R.id.de_add_friend);
    }

    protected void initData() {
        mAddFriend.setOnClickListener(this);
        mDialog = new LoadingDialog(this);

        if (getIntent().hasExtra("SEARCH_USERID")&&getIntent().hasExtra("SEARCH_USERNAME")&&getIntent().hasExtra("SEARCH_PORTRAIT")) {

            mFriendName.setText(getIntent().getStringExtra("SEARCH_USERNAME"));
            mFriendImg.setResource(new Resource(getIntent().getStringExtra("SEARCH_PORTRAIT")));

        }

        if (getIntent().hasExtra("USER")) {
            user = getIntent().getParcelableExtra("USER");
            mFriendName.setText(user.getName());
            mFriendImg.setResource(new Resource(user.getPortraitUri()));
            String userID = DemoContext.getInstance().getSharedPreferences().getString("DEMO_USERID","defalt");
            if(user.getUserId().equals(userID)){
                mAddFriend.setVisibility(View.GONE);
            }else if(user.getUserId().equals("kefu114")){
                mAddFriend.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void onCallApiSuccess(AbstractHttpRequest request, Object obj) {
        if (mUserHttpRequest == request) {
            if (mDialog != null)
                mDialog.dismiss();
            final User user = (User) obj;
            if (user.getCode() == 200) {
                WinToast.toast(this,R.string.friend_send_success);
                Log.e("", "--------onCallApiSuccess----发送好友请求成功---------");
                Intent intent = new Intent();
                this.setResult( Constants.PERSONAL_REQUESTCODE, intent);

            }else if(user.getCode() == 301){
                WinToast.toast(this,R.string.friend_send);
            }
        }

    }

    @Override
    public void onCallApiFailure(AbstractHttpRequest request, BaseException e) {
        if (mUserHttpRequest == request) {
            if (mDialog != null)
                mDialog.dismiss();
            Log.e("", "--------onCallApiSuccess----发送好友请求失败---------");
        }
    }

    @Override
    public void onClick(View v) {
        String targetid = getIntent().getStringExtra("SEARCH_USERID");

        if (DemoContext.getInstance() != null && !"".equals(targetid)) {
            if (DemoContext.getInstance() != null) {
//                String targetname = DemoContext.getInstance().getUserInfoById(targetid).getName().toString();
//                mUserHttpRequest = DemoContext.getInstance().getDemoApi().sendFriendInvite(targetid,"请添加我为好友，I'm "+targetname, this);
                mUserHttpRequest = DemoContext.getInstance().getDemoApi().sendFriendInvite(targetid,"请添加我为好友 ", this);

                if (mDialog != null && !mDialog.isShowing()) {
                    mDialog.show();
                }
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
