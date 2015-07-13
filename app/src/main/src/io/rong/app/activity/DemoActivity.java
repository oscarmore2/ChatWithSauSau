package io.rong.app.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.List;
import java.util.Locale;

import io.rong.app.DemoContext;
import io.rong.app.R;
import io.rong.app.fragment.FriendMultiChoiceFragment;
import io.rong.app.ui.LoadingDialog;
import io.rong.app.ui.WinToast;
import io.rong.imkit.RongIM;
import io.rong.imkit.common.RongConst;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imkit.fragment.MessageListFragment;
import io.rong.imkit.fragment.SubConversationListFragment;
import io.rong.imkit.fragment.UriFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Discussion;
import io.rong.imlib.model.PublicServiceInfo;

/**
 * Created by Bob on 2015/3/27.
 * 通过intent获得发送过来的数据
 * 1，程序切到后台，点击通知栏进入程序
 * 2，收到 push 消息（push消息可以理解为推送消息）
 */
public class DemoActivity extends BaseActivity implements Handler.Callback {

    private static final String TAG = DemoActivity.class.getSimpleName();
    /**
     * 对方id
     */
    private String targetId;
    /**
     * 刚刚创建完讨论组后获得讨论组的targetIds
     */
    private String targetIds;
    /**
     * 讨论组id
     */
    private String mDiscussionId;
    /**
     * 会话类型
     */
    private Conversation.ConversationType mConversationType;
    private LoadingDialog mDialog;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.de_activity);
        initView();
        initData();
    }


    protected void initView() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.de_actionbar_back);
        mHandler = new Handler(this);
        Intent intent = getIntent();

        if (intent != null && intent.hasExtra("DEMO_COVERSATIONTYPE") && intent.hasExtra("DEMO_TARGETID")
                && intent.hasExtra("DEMO_COVERSATION")) {

            if (DemoContext.getInstance() != null) {
                String conversation = intent.getStringExtra("DEMO_COVERSATION");
                targetId = intent.getStringExtra("DEMO_TARGETID");
                String conversationType = intent.getStringExtra("DEMO_COVERSATIONTYPE");
                openConversationFragment(conversation, targetId, conversationType);
            }
        }
        //push或通知过来
        if (intent != null && intent.getData() != null && intent.getData().getScheme().equals("rong") && intent.getData().getQueryParameter("push") != null) {
            //通过intent.getData().getQueryParameter("push") 为true，判断是否是push消息
            if (DemoContext.getInstance() != null && intent.getData().getQueryParameter("push").equals("true")) {
                enterActivity(intent);
            } else {
                enterFragment(intent);
            }
        } else if (intent != null) {
            //程序切到后台，收到消息后点击进入,会执行这里
            if (RongIM.getInstance() == null || RongIM.getInstance().getRongIMClient() == null) {
                if (DemoContext.getInstance() != null) {
                    String token = DemoContext.getInstance().getSharedPreferences().getString("DEMO_TOKEN", "defult");
                    reconnect(token);
                }
            } else {
                enterFragment(intent);
            }
        }

    }

    /**
     * 收到 push 以后，打开会话页面
     * @param conversation
     * @param targetId
     * @param conversationType
     */
    private void openConversationFragment(String conversation, String targetId, String conversationType) {

        String tag;
        if (conversation.equals("conversation")) {
            tag = "conversation";
            ConversationFragment conversationFragment = new ConversationFragment();

            Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversation").appendPath(conversationType.toLowerCase())
                    .appendQueryParameter("targetId", targetId).build();
            conversationFragment.setUri(uri);

            mConversationType = Conversation.ConversationType.valueOf(conversationType);

            if (conversationFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.de_content, conversationFragment, tag);
                transaction.addToBackStack(null).commitAllowingStateLoss();
            }
        }
    }

    /**
     * 收到 push 消息后，选择进入哪个 Activity
     * 如果程序缓存未被清理，进入 MainActivity
     * 程序缓存被清理，进入 LoginActivity，重新获取token
     * <p/>
     * 作用：由于在 manifest 中 intent-filter 是配置在 DemoActivity 下面，所以收到消息后点击notifacition 会跳转到 DemoActivity。
     * 以跳到 MainActivity 为例：
     * 在 DemoActivity 收到消息后，选择进入 MainActivity，这样就把 MainActivity 激活了，当你读完收到的消息点击 返回键 时，程序会退到
     * MainActivity 页面，而不是直接退回到 桌面。
     */
    private void enterActivity(Intent intent) {

        if (DemoContext.getInstance() != null) {
            String token = DemoContext.getInstance().getSharedPreferences().getString("DEMO_TOKEN", "defult");
            Intent in = new Intent();
            if (!token.equals("defult")) {
                in.setClass(DemoActivity.this, MainActivity.class);
                in.putExtra("PUSH_TOKEN", token);
                in.putExtra("PUSH_INTENT", intent.getData());
            } else {
                in.setClass(DemoActivity.this, LoginActivity.class);
                in.putExtra("PUSH_CONTEXT", "push");
            }
            startActivity(in);
            finish();
        }
    }

    /**
     * 收到push消息后做重连，重新连接融云
     *
     * @param token
     */
    private void reconnect(String token) {

        mDialog = new LoadingDialog(this);
        mDialog.setCancelable(false);
        mDialog.setText("connect_auto_reconnect");
        mDialog.show();

        try {
            RongIM.connect(token, new RongIMClient.ConnectCallback() {

                @Override
                public void onTokenIncorrect() {

                }

                @Override
                public void onSuccess(String userId) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mDialog.dismiss();
                            Intent intent = getIntent();
                            if (intent != null) {
                                enterFragment(intent);
                            }
                        }
                    });
                }

                @Override
                public void onError(RongIMClient.ErrorCode e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mDialog.dismiss();
                        }
                    });
                }
            });

        } catch (Exception e) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mDialog.dismiss();
                }
            });
            e.printStackTrace();
        }

    }

    /**
     * 消息分发，选择跳转到哪个fragment
     *
     * @param intent
     */
    private void enterFragment(Intent intent) {
        String tag = null;
        if (intent != null) {
            Fragment fragment = null;

            if (intent.getExtras() != null && intent.getExtras().containsKey(RongConst.EXTRA.CONTENT)) {
                String fragmentName = intent.getExtras().getString(RongConst.EXTRA.CONTENT);
                fragment = Fragment.instantiate(this, fragmentName);
            } else if (intent.getData() != null) {
                if (intent.getData().getPathSegments().get(0).equals("conversation")) {
                    tag = "conversation";
                    if (intent.getData().getLastPathSegment().equals("system")) {
                        //注释掉的代码为不加输入框的聊天页面（此处作为示例）
//                        String fragmentName = MessageListFragment.class.getCanonicalName();
//                        fragment = Fragment.instantiate(this, fragmentName);
                        startActivity(new Intent(DemoActivity.this, NewFriendListActivity.class));
                        finish();
                        List<Conversation> conversations = RongIM.getInstance().getRongIMClient().getConversationList(Conversation.ConversationType.SYSTEM);
                        for (int i = 0; i < conversations.size(); i++) {
                            RongIM.getInstance().getRongIMClient().clearMessagesUnreadStatus(Conversation.ConversationType.SYSTEM, conversations.get(i).getSenderUserId());
                        }
                    } else {
                        String fragmentName = ConversationFragment.class.getCanonicalName();
                        fragment = Fragment.instantiate(this, fragmentName);
                    }
                } else if (intent.getData().getLastPathSegment().equals("conversationlist")) {
                    tag = "conversationlist";
                    String fragmentName = ConversationListFragment.class.getCanonicalName();
                    fragment = Fragment.instantiate(this, fragmentName);
                } else if (intent.getData().getLastPathSegment().equals("subconversationlist")) {
                    tag = "subconversationlist";
                    String fragmentName = SubConversationListFragment.class.getCanonicalName();
                    fragment = Fragment.instantiate(this, fragmentName);
                } else if (intent.getData().getPathSegments().get(0).equals("friend")) {
                    tag = "friend";
                    String fragmentName = FriendMultiChoiceFragment.class.getCanonicalName();
                    fragment = Fragment.instantiate(this, fragmentName);
                    ActionBar actionBar = getSupportActionBar();
                    actionBar.hide();//隐藏ActionBar
                }
                targetId = intent.getData().getQueryParameter("targetId");
                targetIds = intent.getData().getQueryParameter("targetIds");
                mDiscussionId = intent.getData().getQueryParameter("discussionId");
                if (targetId != null) {
//                    intent.getData().getLastPathSegment();//获得当前会话类型
                    mConversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.getDefault()));
                } else if (targetIds != null)
                    mConversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.getDefault()));
            }

            if (fragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.de_content, fragment, tag);
                transaction.addToBackStack(null).commitAllowingStateLoss();
            }
        }
    }

    /**
     * 设置 title
     */
    protected void initData() {
        if (mConversationType != null) {
            if (mConversationType.equals(Conversation.ConversationType.PRIVATE)) {
                if (DemoContext.getInstance() != null)
                    getSupportActionBar().setTitle(DemoContext.getInstance().getUserInfoById(targetId).getName().toString());
            } else if (mConversationType.equals(Conversation.ConversationType.GROUP)) {
                if (DemoContext.getInstance() != null) {
                    getSupportActionBar().setTitle(DemoContext.getInstance().getGroupNameById(targetId));
                }
            } else if (mConversationType.equals(Conversation.ConversationType.DISCUSSION)) {
                if (targetId != null) {
                    RongIM.getInstance().getRongIMClient().getDiscussion(targetId, new RongIMClient.ResultCallback<Discussion>() {
                        @Override
                        public void onSuccess(Discussion discussion) {
                            getSupportActionBar().setTitle(discussion.getName());
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode e) {

                        }
                    });

                } else if (targetIds != null) {
                    setDiscussionName(targetIds);
                } else {
                    getSupportActionBar().setTitle("讨论组");
                }
            } else if (mConversationType.equals(Conversation.ConversationType.SYSTEM)) {
                getSupportActionBar().setTitle("系统会话类型");
            } else if (mConversationType.equals(Conversation.ConversationType.CHATROOM)) {
                getSupportActionBar().setTitle("聊天室");
            } else if (mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE)) {
                getSupportActionBar().setTitle("客服");
            } else if (mConversationType.equals(Conversation.ConversationType.APP_PUBLIC_SERVICE)) {
                if (RongIM.getInstance() != null && RongIM.getInstance().getRongIMClient() != null) {
                    RongIM.getInstance().getRongIMClient().getPublicServiceProfile(Conversation.PublicServiceType.APP_PUBLIC_SERVICE, targetId, new RongIMClient.ResultCallback<PublicServiceInfo>() {
                        @Override
                        public void onSuccess(PublicServiceInfo publicServiceInfo) {
                            getSupportActionBar().setTitle(publicServiceInfo.getName().toString());
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
                }

            } else if (mConversationType.equals(Conversation.ConversationType.PUBLIC_SERVICE)) {
                if (RongIM.getInstance() != null && RongIM.getInstance().getRongIMClient() != null) {
                    RongIM.getInstance().getRongIMClient().getPublicServiceProfile(Conversation.PublicServiceType.PUBLIC_SERVICE, targetId, new RongIMClient.ResultCallback<PublicServiceInfo>() {
                        @Override
                        public void onSuccess(PublicServiceInfo publicServiceInfo) {
                            getSupportActionBar().setTitle(publicServiceInfo.getName().toString());
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
                }
            }


        }

    }

    /**
     * set discussion name
     *
     * @param targetIds
     */
    private void setDiscussionName(String targetIds) {
        StringBuilder sb = new StringBuilder();
        getSupportActionBar().setTitle(targetIds);
        String[] ids = targetIds.split(",");
        if (DemoContext.getInstance() != null) {
            for (int i = 0; i < ids.length; i++) {
                sb.append(DemoContext.getInstance().getUserInfoById(ids[i]).getName().toString());
                sb.append(",");
            }
            sb.append(DemoContext.getInstance().getSharedPreferences().getString("DEMO_USER_NAME", "0.0"));
        }

        getSupportActionBar().setTitle(sb);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);

        String tag = null;
        Fragment fragment = null;

        if (intent.getExtras() != null && intent.getExtras().containsKey(RongConst.EXTRA.CONTENT)) {
            String fragmentName = intent.getExtras().getString(RongConst.EXTRA.CONTENT);
            fragment = Fragment.instantiate(this, fragmentName);
        } else if (intent.getData() != null) {

            if (intent.getData().getPathSegments().get(0).equals("conversation")) {
                tag = "conversation";
                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment != null)
                    return;
                String fragmentName = ConversationFragment.class.getCanonicalName();
                fragment = Fragment.instantiate(this, fragmentName);
            } else if (intent.getData().getLastPathSegment().equals("conversationlist")) {
                tag = "conversationlist";
                String fragmentName = ConversationListFragment.class.getCanonicalName();
                fragment = Fragment.instantiate(this, fragmentName);
            } else if (intent.getData().getLastPathSegment().equals("subconversationlist")) {
                tag = "subconversationlist";
                String fragmentName = SubConversationListFragment.class.getCanonicalName();
                fragment = Fragment.instantiate(this, fragmentName);
            }
        }

        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.de_content, fragment, tag);
            transaction.addToBackStack(null).commitAllowingStateLoss();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            super.onBackPressed();
            this.finish();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.de_conversation_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.icon:
                if (mConversationType == null) {
                    return false;
                }
                if (mConversationType == Conversation.ConversationType.PUBLIC_SERVICE || mConversationType == Conversation.ConversationType.APP_PUBLIC_SERVICE) {
                    RongIM.getInstance().startPublicServiceProfile(this, mConversationType, targetId);
                } else {
                    //通过targetId 和 会话类型 打开指定的设置页面
                    if (!TextUtils.isEmpty(targetId)) {
                        Uri uri = Uri.parse("demo://" + getApplicationInfo().packageName).buildUpon().appendPath("conversationSetting")
                                .appendPath(mConversationType.getName()).appendQueryParameter("targetId", targetId).build();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(uri);
                        startActivity(intent);
                        //当你刚刚创建完讨论组以后获得的是 targetIds
                    } else if (!TextUtils.isEmpty(targetIds)) {

                        UriFragment fragment = (UriFragment) getSupportFragmentManager().getFragments().get(0);
                        fragment.getUri();
                        //得到讨论组的 targetId
                        targetId = fragment.getUri().getQueryParameter("targetId");

                        if (!TextUtils.isEmpty(targetId)) {
                            Uri uri = Uri.parse("demo://" + getApplicationInfo().packageName).buildUpon().appendPath("conversationSetting")
                                    .appendPath(mConversationType.getName()).appendQueryParameter("targetId", targetId).build();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(uri);
                            startActivity(intent);
                        } else {
                            WinToast.toast(DemoActivity.this, "讨论组尚未创建成功");
                        }
                    }
                }
                break;

            case android.R.id.home:
                finish();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }
}
