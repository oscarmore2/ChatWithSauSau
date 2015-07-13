package io.rong.app.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import io.rong.app.DemoContext;
import io.rong.app.R;
import io.rong.app.model.Status;
import io.rong.app.ui.WinToast;
import io.rong.app.utils.Constants;
import io.rong.imkit.RongContext;
import io.rong.imlib.model.UserInfo;
import com.sea_monster.exception.BaseException;
import com.sea_monster.network.AbstractHttpRequest;

/**
 * Created by Administrator on 2015/3/3.
 */
public class UpdateNameActivity extends BaseApiActivity {

    private EditText mNewName;//昵称


    private Handler mHandler;
    private AbstractHttpRequest<Status> httpRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.de_ac_update_name);
        initView();
    }


    protected void initView() {
        getSupportActionBar().setTitle(R.string.my_username);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.de_actionbar_back);

        mHandler = new Handler();
        mNewName = (EditText) findViewById(R.id.et_new_name);
        if (getIntent() != null) {
            mNewName.setText(getIntent().getStringExtra("USERNAME"));
            mNewName.setSelection(getIntent().getStringExtra("USERNAME").length());
        }
    }


    private void refreshUserInfo(UserInfo userInfo) {

        if (userInfo == null) {
            throw new IllegalArgumentException();
        }

        if (RongContext.getInstance() != null) {
            RongContext.getInstance().getUserInfoCache().put(userInfo.getUserId(), userInfo);

            RongContext.init(this);
        }
    }


    @Override
    public void onCallApiSuccess(AbstractHttpRequest request, Object obj) {
        if (request == httpRequest) {
            if (obj instanceof Status) {
                Status status = (Status) obj;

                if (status.getCode() == 200) {
                    WinToast.toast(this, R.string.update_profile_success);
                    Intent intent = new Intent();
                    intent.putExtra("UPDATA_RESULT", mNewName.getText().toString());
                    this.setResult(Constants.FIX_USERNAME_REQUESTCODE, intent);
                    SharedPreferences.Editor edit = DemoContext.getInstance().getSharedPreferences().edit();
                    edit.putString("DEMO_USER_NAME", mNewName.getText().toString());
                    edit.apply();
//                    refreshUserInfo(new UserInfo(DemoContext.getInstance().getSharedPreferences().getString("DEMO_USER_ID", null), mNewName.getText().toString(), null));
                    finish();
                }
            }

        }
    }

    @Override
    public void onCallApiFailure(AbstractHttpRequest request, BaseException e) {
        WinToast.toast(this, R.string.update_profile_faiture);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.de_fix_username, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.icon:
                if ("".equals(mNewName.getText().toString())) {
                    WinToast.toast(this, R.string.profile_not_null);
                    break;
                } else {
                    if (DemoContext.getInstance() != null) {
                        httpRequest = DemoContext.getInstance().getDemoApi().updateProfile(mNewName.getText().toString(), this);
                    }
                }
                break;

            case android.R.id.home:
                finish();

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
