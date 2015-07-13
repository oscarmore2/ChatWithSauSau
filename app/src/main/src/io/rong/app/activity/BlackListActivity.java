package io.rong.app.activity;

import android.os.Bundle;
import android.view.MenuItem;

import io.rong.app.R;

/**
 * Created by Bob on 2015/4/9.
 */
public class BlackListActivity extends BaseActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.de_fr_black);
        getSupportActionBar().setTitle(R.string.the_blacklist);

    }

}
