package io.rong.app.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import io.rong.app.R;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;

/**
 * Created by Bob on 15/6/17.
 */
public class TestActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.conversation);

//        ConversationFragment fragment = new ConversationFragment();
//
//        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
//                .appendPath("conversation").appendPath(io.rong.imlib.model.Conversation.ConversationType.PRIVATE.getName().toLowerCase())
//                .appendQueryParameter("targetId", "10000").appendQueryParameter("title", "hello").build();
//        fragment.setUri(uri);
//
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.add(R.id.de_content, fragment);
//        transaction.commit();


//        startActivity(new Intent(this,RongActivity.class));
//        if(RongIM.getInstance()!=null){
//            /**
//             * 启动会话界面。
//             *
//             * @param context          应用上下文。
//             * @param conversationType 开启会话类型。
//             * @param targetId         目标 Id；根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
//             * @param title            聊天的标题，如果传入空值，则默认显示会话的名称。
//             */
//            RongIM.getInstance().startConversation(this, Conversation.ConversationType.PRIVATE,"targetId","title");
//
//            /**
//             * 启动单聊界面。
//             *
//             * @param context      应用上下文。
//             * @param targetUserId 要与之聊天的用户 Id。
//             * @param title        聊天的标题，如果传入空值，则默认显示与之聊天的用户名称。
//             */
//            RongIM.getInstance().startPrivateChat(this,"targetId","title");
//        }

        ConversationFragment fragment =  (ConversationFragment)getSupportFragmentManager().findFragmentById(R.id.conversation);

                Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(io.rong.imlib.model.Conversation.ConversationType.PRIVATE.getName().toLowerCase())
                .appendQueryParameter("targetId", "10000").appendQueryParameter("title", "hello").build();

        fragment.setUri(uri);
    }
}
