package io.rong.app.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.rong.app.DemoContext;
import io.rong.app.R;
import io.rong.app.utils.Constants;
import com.sea_monster.resource.Resource;
import io.rong.imkit.widget.AsyncImageView;

/**
 * Created by Administrator on 2015/3/2.
 */
public class MyAccountActivity extends BaseActionBarActivity implements View.OnClickListener {

    private static final int RESULTCODE = 10;
    ;
    /**
     * 头像
     */
    private RelativeLayout mMyPortrait;
    /**
     * 昵称
     */
    private RelativeLayout mMyUsername;

    private TextView mTVUsername;
    private AsyncImageView mImgMyPortrait;
    private String mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.de_ac_myaccount);
        initView();

    }


    protected void initView() {
        getSupportActionBar().setTitle(R.string.de_actionbar_myacc);

        mImgMyPortrait = (AsyncImageView) findViewById(R.id.img_my_portrait);
        mMyPortrait = (RelativeLayout) findViewById(R.id.rl_my_portrait);
        mMyUsername = (RelativeLayout) findViewById(R.id.rl_my_username);
        mTVUsername = (TextView) findViewById(R.id.tv_my_username);
        if (DemoContext.getInstance().getSharedPreferences() != null) {
//            String userId = DemoContext.getInstance().getSharedPreferences().getString("DEMO_USER_ID", null);
            mUserName = DemoContext.getInstance().getSharedPreferences().getString("DEMO_USER_NAME", null);
            String userPortrait = DemoContext.getInstance().getSharedPreferences().getString("DEMO_USER_PORTRAIT", null);
            mImgMyPortrait.setResource(new Resource(Uri.parse(userPortrait)));
            mTVUsername.setText(mUserName.toString());
        }

        mMyPortrait.setOnClickListener(this);
        mMyUsername.setOnClickListener(this);
//        mResourceHandler = new ResourceHandler.Builder().enableBitmapCache().build(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_my_portrait://头像

                break;

            case R.id.rl_my_username://昵称
                Intent intent = new Intent(this, UpdateNameActivity.class);
                intent.putExtra("USERNAME", mUserName);
                startActivityForResult(intent, RESULTCODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode) {
            case Constants.FIX_USERNAME_REQUESTCODE:
                if (data != null) {
                    mTVUsername.setText(data.getStringExtra("UPDATA_RESULT"));
                    mUserName = data.getStringExtra("UPDATA_RESULT");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
