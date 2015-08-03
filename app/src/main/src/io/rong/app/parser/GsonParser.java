package io.rong.app.parser;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import org.apache.http.Header;
import org.json.JSONException;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;

import io.rong.app.DemoContext;
import com.sea_monster.exception.InternalException;
import com.sea_monster.exception.ParseException;

/**
 * Created by DragonJ on 14-7-15.
 */
public class GsonParser<T extends Serializable> extends JsonObjectParser<T> {


    Type type;
    Gson gson;

    public GsonParser(Class<T> type) {
        gson = new Gson();
        this.type = type;
    }

    @Override
    public T jsonParse(JsonReader reader) throws JSONException, IOException, ParseException, InternalException,JsonSyntaxException {
        return gson.fromJson(reader, this.type);
    }

    @Override
    public void onHeaderParsed(Header[] headers) {

        if (headers == null) {
            return;
        } else {
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].getName().equals("Set-Cookie")) {
                    String[] cookievalues = headers[i].getValue().split(";");
                    SharedPreferences.Editor edit = DemoContext.getInstance().getSharedPreferences().edit();
                    edit.putString("DEMO_COOKIE", cookievalues[0]);
                    edit.commit();
                }
            }
        }
    }


}

