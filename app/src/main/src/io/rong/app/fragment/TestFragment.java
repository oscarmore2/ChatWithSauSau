package io.rong.app.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import io.rong.app.R;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imkit.fragment.DispatchResultFragment;
import io.rong.imkit.fragment.SubConversationListFragment;
import io.rong.imlib.model.Conversation;

/**
 * Created by Bob on 15/5/27.
 */
public class TestFragment extends Fragment {

    ConversationListFragment mFragment;
//    Button mButton1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        View view = inflater.inflate(R.layout.de_activity, container, false);
        View view = inflater.inflate(R.layout.conversation, container, false);

//        mButton1 = (Button)view.findViewById(android.R.id.button1);

        ConversationFragment fragment = (ConversationFragment) getChildFragmentManager().findFragmentById(R.id.conversation);

        Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(io.rong.imlib.model.Conversation.ConversationType.PRIVATE.getName().toLowerCase())
                .appendQueryParameter("targetId", "10000").appendQueryParameter("title", "hello").build();
        fragment.setUri(uri);
//
//        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//        transaction.add(R.id.de_content, fragment);
//        transaction.commit();




//        SubConversationListFragment fragment = new SubConversationListFragment();
//
//        Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
//                .appendPath("subconversationlist")
//                .appendQueryParameter("type", String.valueOf(Conversation.ConversationType.GROUP))
//                .build();
//
//        fragment.setUri(uri);
//
//        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//        //xxx 为你要加载的 id
//        transaction.add(R.id.de_content, fragment);
//        transaction.commit();



//        mButton1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
//                        .appendPath("conversationlist")
//                        .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
//                        .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")
//                        .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")
//                        .build();
//
//                mFragment.setUri(uri);
//
//
//            }
//        });

//        ConversationListFragment fragment = new ConversationListFragment();
//        Uri uri = Uri.parse("rong://" +getActivity().getApplicationInfo().packageName).buildUpon()
//                .appendPath("conversationlist")
//                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
//                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//群组
//                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//讨论组
//                .appendQueryParameter(Conversation.ConversationType.CHATROOM.getName(), "false")//聊天室
//                .appendQueryParameter(Conversation.ConversationType.CUSTOMER_SERVICE.getName(), "false")//客服
//                .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//应用公众服务。
//                .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
//                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//系统
//                .build();
//        fragment.setUri(uri);
//
//        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//        transaction.add(R.id.de_content, fragment);
//        transaction.commit();

        return view;


    }

}