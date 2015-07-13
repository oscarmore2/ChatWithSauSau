package io.rong.app.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import io.rong.app.R;
import io.rong.imkit.fragment.SubConversationListFragment;
import io.rong.imkit.widget.adapter.SubConversationListAdapter;

/**
 * Created by Bob_ge on 15/6/18.
 */
public class RongActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("","----RongActivity-----onCreate--");
//        setContentView(R.layout.conversationlist);


        setContentView(R.layout.de_activity);
        SubConversationListFragment fragment = new SubConversationListFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.de_content, fragment);
        transaction.commit();

    }
}
