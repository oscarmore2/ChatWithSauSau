package io.rong.app.activity;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.sea_monster.exception.BaseException;
import com.sea_monster.network.AbstractHttpRequest;

import java.util.Calendar;
import java.util.Date;

import io.rong.app.DemoContext;
import io.rong.app.R;
import io.rong.app.ui.WinToast;
import io.rong.app.utils.DateUtils;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;


/**
 * Created by Administrator on 2015/3/2.
 */
public class NewMessageRemindActivity extends BaseApiActivity implements View.OnClickListener,Handler.Callback {

    private static final int NOTIFICATION_ISCHECKED = 1;
    private static final int NOTIFICATION_NOCHECKED = 2;
    private static final int NOTIFICATION_NOCHECKED_BEGIN = 3;
    /**
     * 接收新消息通知
     */
    private CheckBox mNewMessageNotice;
    /**
     * 新消息展示
     */
    private CheckBox mNewMessageShow;
    /**
     * 声音
     */
    private CheckBox mVoiceCheck;
    /**
     * 震动
     */
    private CheckBox mVibration;
    /**
     * 关闭勿扰模式
     */
    private LinearLayout mCloseNotifacation;
    /**
     * 开始时间 RelativeLayout
     */
    private RelativeLayout mStartNotifacation;
    /**
     * 关闭时间 RelativeLayout
     */
    private RelativeLayout mEndNotifacation;
    /**
     * 开始时间
     */
    private TextView mStartTimeNofication;
    /**
     * 关闭时间
     */
    private TextView mEndTimeNofication;

    /**
     * 开始时间
     */
    private String mStartTime;
    /**
     * 结束时间
     */
    private String mEndTime;
    /**
     * 小时
     */
    int hourOfDays;
    /**
     * 分钟
     */
    int minutes;
    private String mTimeFormat = "HH:mm:ss";
    private Handler mHandler;
    boolean mIsSetting = false;
    public static final String TAG = NewMessageRemindActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.de_ac_new_message_remind);
        initView();
        initData();

    }

    protected void initView() {
        getSupportActionBar().setTitle(R.string.new_message_show);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.de_actionbar_back);
        mHandler = new Handler(this);
        mNewMessageNotice = (CheckBox) findViewById(R.id.new_message_notice_check);
        mNewMessageShow = (CheckBox) findViewById(R.id.new_message_show_check);
        mVoiceCheck = (CheckBox) findViewById(R.id.voice_check);
        mVibration = (CheckBox) findViewById(R.id.vibration_check);
        mCloseNotifacation = (LinearLayout) findViewById(R.id.close_notification);
        mStartNotifacation = (RelativeLayout) findViewById(R.id.start_notification);
        mStartTimeNofication = (TextView) findViewById(R.id.start_time_notification);
        mEndNotifacation = (RelativeLayout) findViewById(R.id.end_notification);
        mEndTimeNofication = (TextView) findViewById(R.id.end_time_notification);

        Calendar calendar = Calendar.getInstance();
        hourOfDays = calendar.get(Calendar.HOUR_OF_DAY);
        minutes = calendar.get(Calendar.MINUTE);

    }

    protected void initData() {
        mNewMessageNotice.setOnClickListener(this);
        mNewMessageShow.setOnClickListener(this);
        mVoiceCheck.setOnClickListener(this);
        mVibration.setOnClickListener(this);
        mStartNotifacation.setOnClickListener(this);
        mEndNotifacation.setOnClickListener(this);
        if (DemoContext.getInstance().getSharedPreferences() != null) {
            mIsSetting = DemoContext.getInstance().getSharedPreferences().getBoolean("IS_SETTING", false);
            if (mIsSetting) {
                Message msg = Message.obtain();
                msg.what = NOTIFICATION_ISCHECKED;
                mHandler.sendMessage(msg);
            }
        }

//        if(RongIM.getInstance()!= null &&RongIM.getInstance().getRongIMClient()!=null){
//            RongIM.getInstance().getRongIMClient().getNotificationQuietHours(new RongIMClient.GetNotificationQuietHoursCallback() {
//                @Override
//                public void onSuccess(String startTime, int spanMin) {
//                    if(startTime != null){
//
//                        Message msg = Message.obtain();
//                        msg.what = NOTIFICATION_NOCHECKED_BEGIN;
//                        msg.obj = startTime;
//                        msg.arg1 = spanMin;
//                        mHandler.sendMessage(msg);
//                    }
//
//                }
//
//                @Override
//                public void onError(RongIMClient.ErrorCode errorCode) {
//
//                }
//            });
//        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_notification://开始时间
                if (DemoContext.getInstance().getSharedPreferences() != null) {
                    String starttime = DemoContext.getInstance().getSharedPreferences().getString("START_TIME", null);
                    if (starttime != null && !"".equals(starttime)) {
                        hourOfDays = Integer.parseInt(starttime.substring(0, 2));
                        minutes = Integer.parseInt(starttime.substring(3, 5));
                    }
                }
                TimePickerDialog timePickerDialog = new TimePickerDialog(NewMessageRemindActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        mStartTime = getDaysTime(hourOfDay, minute);

                        mStartTimeNofication.setText(mStartTime);
                        SharedPreferences.Editor editor = DemoContext.getInstance().getSharedPreferences().edit();
                        editor.putString("START_TIME", mStartTime);
                        editor.apply();

                        if (DemoContext.getInstance().getSharedPreferences() != null) {
                            String endtime = DemoContext.getInstance().getSharedPreferences().getString("END_TIME", null);
                            if (endtime != null && !"".equals(endtime)) {
                                Date datastart = DateUtils.stringToDate(mStartTime, mTimeFormat);
                                Date dataend = DateUtils.stringToDate(endtime, mTimeFormat);
                                long spansTime = DateUtils.compareMin(datastart, dataend);
                                setConversationTime(mStartTime, (int) Math.abs(spansTime));
                            }
                        }
                    }
                }, hourOfDays, minutes, true);
                timePickerDialog.show();
                break;
            case R.id.end_notification://结束时间
                if (DemoContext.getInstance().getSharedPreferences() != null) {
                    String endtime = DemoContext.getInstance().getSharedPreferences().getString("END_TIME", null);
                    if (endtime != null && !"".equals(endtime)) {
                        hourOfDays = Integer.parseInt(endtime.substring(0, 2));
                        minutes = Integer.parseInt(endtime.substring(3, 5));
                    }
                }

                timePickerDialog = new TimePickerDialog(NewMessageRemindActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        mEndTime = getDaysTime(hourOfDay, minute);
                        mEndTimeNofication.setText(mEndTime);
                        SharedPreferences.Editor editor = DemoContext.getInstance().getSharedPreferences().edit();
                        editor.putString("END_TIME", mEndTime);
                        editor.apply();

                        if (DemoContext.getInstance().getSharedPreferences() != null) {
                            String starttime = DemoContext.getInstance().getSharedPreferences().getString("START_TIME", null);
                            if (starttime != null && !"".equals(starttime)) {
                                Date datastart = DateUtils.stringToDate(starttime, mTimeFormat);
                                Date dataend = DateUtils.stringToDate(mEndTime, mTimeFormat);
                                long spansTime = DateUtils.compareMin(datastart, dataend);
                                Log.e("","------结束时间----"+mEndTime);
                                Log.e("","------开始时间----"+starttime);
                                Log.e("","------时间间隔----"+spansTime);

                                setConversationTime(mStartTime, (int) Math.abs(spansTime));
                            }
                        }
                    }
                }, hourOfDays, minutes, true);
                timePickerDialog.show();

                break;
            case R.id.new_message_notice_check://接收新消息通知
                if (mNewMessageNotice.isChecked()) {
                    Message msg = Message.obtain();
                    msg.what = NOTIFICATION_ISCHECKED;
                    mHandler.sendMessage(msg);
                } else {
                    if (RongIM.getInstance() != null) {

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                RongIM.getInstance().getRongIMClient().removeNotificationQuietHours(new RongIMClient.OperationCallback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.e(TAG, "----yb----移除会话通知周期-onSuccess");
                                        Message msg = Message.obtain();
                                        msg.what = NOTIFICATION_NOCHECKED;
                                        mHandler.sendMessage(msg);
                                    }

                                    @Override
                                    public void onError(RongIMClient.ErrorCode errorCode) {
                                        Log.e(TAG, "----yb-----移除会话通知周期-oonError:" + errorCode.getValue());
                                    }
                                });
                            }
                        });

                    }
                }


                break;
            case R.id.new_message_show_check://新消息展示

                break;
            case R.id.voice_check://声音

                break;
            case R.id.vibration_check://震动

                break;
        }
    }

    @Override
    public void onCallApiSuccess(AbstractHttpRequest request, Object obj) {

    }

    @Override
    public void onCallApiFailure(AbstractHttpRequest request, BaseException e) {

    }

    /**
     * 得到"HH:mm:ss"类型时间
     *
     * @param hourOfDay 小时
     * @param minite    分钟
     * @return "HH:mm:ss"类型时间
     */
    private String getDaysTime(final int hourOfDay, final int minite) {
        String daysTime;
        String hourOfDayString = "0" + hourOfDay;
        String minuteString = "0" + minite;
        if (hourOfDay < 10 && minite >= 10) {
            daysTime = hourOfDayString + ":" + minite + ":00";
        } else if (minite < 10 && hourOfDay >= 10) {
            daysTime = hourOfDay + ":" + minuteString + ":00";
        } else if (hourOfDay < 10 && minite < 10) {
            daysTime = hourOfDayString + ":" + minuteString + ":00";
        } else {
            daysTime = hourOfDay + ":" + minite + ":00";
        }
        return daysTime;
    }
    @Override
    public boolean handleMessage(Message msg) {
        SharedPreferences.Editor editor;
        switch (msg.what){
            case NOTIFICATION_ISCHECKED://打开新消息通知
                mNewMessageNotice.setChecked(true);
                mCloseNotifacation.setVisibility(View.VISIBLE);
                if (DemoContext.getInstance().getSharedPreferences() != null) {
                    String endtime = DemoContext.getInstance().getSharedPreferences().getString("END_TIME", null);
                    String starttimes = DemoContext.getInstance().getSharedPreferences().getString("START_TIME", null);

                    if (endtime != null && starttimes != null && !"".equals(endtime) && !"".equals(starttimes)) {
                        Date datastart = DateUtils.stringToDate(starttimes, mTimeFormat);
                        Date dataend = DateUtils.stringToDate(endtime, mTimeFormat);
                        long spansTime = DateUtils.compareMin(datastart, dataend);
                        mStartTimeNofication.setText(starttimes);
                        mEndTimeNofication.setText(endtime);
                        setConversationTime(starttimes, (int) spansTime);
                    } else {
                        mStartTimeNofication.setText("23:59:59");
                        mEndTimeNofication.setText("00:00:00");
                        editor = DemoContext.getInstance().getSharedPreferences().edit();
                        editor.putString("START_TIME", "23:59:59");
                        editor.putString("END_TIME", "00:00:00");
                        editor.apply();
                    }
                }

                break;

            case NOTIFICATION_NOCHECKED://关闭新消息通知
                mCloseNotifacation.setVisibility(View.GONE);
                editor = DemoContext.getInstance().getSharedPreferences().edit();
                editor.remove("IS_SETTING");
                editor.apply();
                break;
            case NOTIFICATION_NOCHECKED_BEGIN:

                mNewMessageNotice.setChecked(true);
                mCloseNotifacation.setVisibility(View.VISIBLE);

                String startTime = (String) msg.obj;
                int span = msg.arg1;

                mStartTimeNofication.setText(startTime);
                mEndTimeNofication.setText(span+"");
                break;


        }
        return false;
    }

    /**
     * 设置勿扰时间
     *
     * @param startTime 设置勿扰开始时间 格式为：HH:mm:ss
     * @param spanMins  0 < 间隔时间 < 1440
     */
    private void setConversationTime(final String startTime, final int spanMins) {

        if (RongIM.getInstance() != null && startTime != null && !"".equals(startTime)) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (spanMins > 0 && spanMins < 1440) {
                        Log.e("", "----设置勿扰时间startTime；" + startTime + "---spanMins:" + spanMins);

                        RongIM.getInstance().getRongIMClient().setNotificationQuietHours(startTime, spanMins, new RongIMClient.OperationCallback() {

                            @Override
                            public void onSuccess() {
                                Log.e(TAG, "----yb----设置会话通知周期-onSuccess");
                                SharedPreferences.Editor editor = DemoContext.getInstance().getSharedPreferences().edit();
                                editor.putBoolean("IS_SETTING", true);
                                editor.apply();
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {
                                Log.e(TAG, "----yb----设置会话通知周期-oonError:" + errorCode.getValue());
                            }
                        });
                    } else {
                        WinToast.toast(NewMessageRemindActivity.this, "间隔时间必须>0");
                    }
                }
            });
        }
    }
}
