package io.rong.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Locale;

import io.rong.app.R;
import io.rong.imlib.model.Conversation;

/**
 * Created by Bob on 2015/4/11.
 * 聚合会话页面设置actionbar
 */
public class RongSubActivity extends RongActivity {

    private String targetId;
    private String targetIds;
    private String type;
    private Conversation.ConversationType mConversationType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        targetId = intent.getData().getQueryParameter("targetId");
        targetIds = intent.getData().getQueryParameter("targetIds");
        type = intent.getData().getQueryParameter("type");

        if (targetId != null) {
            mConversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.getDefault()));
        } else if (targetIds != null)
            mConversationType = Conversation.ConversationType.valueOf(intent.getData().getQueryParameter("type").toUpperCase(Locale.getDefault()));


        if (type.equals("group")) {
            getSupportActionBar().setTitle(R.string.de_actionbar_sub_group);
        } else if (type.equals("private")) {
            getSupportActionBar().setTitle(R.string.de_actionbar_sub_private);
        } else if (type.equals("discussion")) {
            getSupportActionBar().setTitle(R.string.de_actionbar_sub_discussion);
        } else if (type.equals("system")) {
            getSupportActionBar().setTitle(R.string.de_actionbar_sub_system);
        } else {
            getSupportActionBar().setTitle(R.string.de_actionbar_sub_defult);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
