package io.rong.app.activity;

import android.os.Bundle;
import android.view.MenuItem;

import io.rong.app.R;
import com.sea_monster.exception.BaseException;
import com.sea_monster.network.AbstractHttpRequest;

/**
 * Created by Bob on 2015/3/27.
 */
public class ConversationSettingActivity extends BaseActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.de_ac_setting);
        getSupportActionBar().setTitle(R.string.de_actionbar_set_conversation);

    }

}
