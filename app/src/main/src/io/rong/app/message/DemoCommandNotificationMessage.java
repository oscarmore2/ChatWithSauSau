package io.rong.app.message;

import android.os.Parcel;
import android.text.TextUtils;

import com.sea_monster.common.ParcelUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;
import io.rong.message.utils.RLog;


@MessageTag(value = "RC:CmdNtf", flag = MessageTag.NONE)
public class DemoCommandNotificationMessage extends MessageContent {

    private String name; // 命令名。
    private String data; // 命令数据，可以为任意格式，如 JSON。

    /**
     * 获取命令名。
     *
     * @return 命令名。
     */
    public String getName() {
        return name;
    }

    /**
     * 设置命令名。
     *
     * @param name 命令名。
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取命令数据，可以为任意格式，如 JSON。
     *
     * @return 命令数据，可以为任意格式，如 JSON。
     */
    public String getData() {
        return data;
    }

    /**
     * 设置命令数据，可以为任意格式，如 JSON。
     *
     * @param data  命令数据，可以为任意格式，如 JSON。
     */
    public void setData(String data) {
        this.data = data;
    }



    public DemoCommandNotificationMessage(Parcel in) {
        name = ParcelUtils.readFromParcel(in);
        data = ParcelUtils.readFromParcel(in);
    }

    /**
     * 创建消息实例。
     *
     * @param name  获取命令名。
     * @param data  设置命令数据，可以为任意格式，如 JSON。
     * @return  消息实例。
     */
    public static DemoCommandNotificationMessage obtain(String name, String data) {
        DemoCommandNotificationMessage obj = new DemoCommandNotificationMessage();
        obj.name = name;
        obj.data = data;
        return obj;
    }

    protected DemoCommandNotificationMessage() {

    }

    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("name", name);

            if (!TextUtils.isEmpty(data))
                jsonObj.put("data", data);

        } catch (JSONException e) {
            RLog.e(this, "JSONException", e.getMessage());
        }

        try {
            return jsonObj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public DemoCommandNotificationMessage(byte[] data) {
        String jsonStr = null;
        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e1) {

        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);

            setName(jsonObj.optString("name"));
            setData(jsonObj.optString("data"));

        } catch (JSONException e) {
            RLog.e(this, "JSONException", e.getMessage());
        }
    }

    /**
     * 将类的数据写入外部提供的 Parcel 中。
     *
     * @param dest  对象被写入的 Parcel。
     * @param flags 对象如何被写入的附加标志，可能是 0 或 PARCELABLE_WRITE_RETURN_VALUE。
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest, name);
        ParcelUtils.writeToParcel(dest, data);
    }

    /**
     * 描述了包含在 Parcelable 对象排列信息中的特殊对象的类型。
     *
     * @return 一个标志位，表明Parcelable对象特殊对象类型集合的排列。
     */
    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DemoCommandNotificationMessage> CREATOR = new Creator<DemoCommandNotificationMessage>() {

        @Override
        public DemoCommandNotificationMessage createFromParcel(Parcel source) {
            return new DemoCommandNotificationMessage(source);
        }

        @Override
        public DemoCommandNotificationMessage[] newArray(int size) {
            return new DemoCommandNotificationMessage[size];
        }
    };





}