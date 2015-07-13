package io.rong.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import io.rong.app.R;


/**
 * Created by Administrator on 2015/3/2.
 */
public class PrivacyActivity extends BaseActionBarActivity implements View.OnClickListener {

    /**
     * 加我时需要验证
     */
    private CheckBox mAddValidation;
    /**
     * 黑名单
     */
    private RelativeLayout mTheBlackList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.de_ac_privacy);
        initView();
    }


    protected void initView() {

        getSupportActionBar().setTitle(R.string.set_privacy);
        mAddValidation = (CheckBox) findViewById(R.id.add_validation_check);
        mTheBlackList = (RelativeLayout) findViewById(R.id.rl_the_blacklist);


        mAddValidation.setOnClickListener(this);
        mTheBlackList.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_validation_check://加我时需要验证
                mAddValidation.isChecked();
                break;
            case R.id.rl_the_blacklist://黑名单
                startActivity(new Intent(this, BlackListActivity.class));
                break;
        }

    }

}
