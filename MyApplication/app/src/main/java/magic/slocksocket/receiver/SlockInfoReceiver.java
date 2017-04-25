package magic.slocksocket.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.amap.api.maps.model.LatLng;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Iterator;

import static magic.slocksocket.utils.earthToMars.WorldGS2MarsGS;

/**
 * Created by chenhaoyu on 2017/4/16.
 */


public class SlockInfoReceiver extends BroadcastReceiver {

    UpdateUIListenner updateUIListenner;

    /**
     * 监听广播接收器的接收到的数据
     *
     * @param updateUIListenner
     */
    public void SetOnUpdateUIListenner(UpdateUIListenner updateUIListenner) {
        this.updateUIListenner = updateUIListenner;
    }


    @Override

    public void onReceive(Context context, Intent intent) {

        String data = intent.getStringExtra("data");

        Log.i("chy", data);

        updateUIListenner.updateinfo(data);

        if (data != null) {


            if (data.equals("sessionOpened")) {

                updateUIListenner.setcutdown();
            }


            try {
                JSONObject jsoninfo = new JSONObject(data);


                Iterator<?> it = jsoninfo.keys();
                String aa2 = "";
                String bb2 = null;
                String txt = "";
                while (it.hasNext()) {//遍历JSONObject
                    bb2 = (String) it.next().toString();
                    aa2 = jsoninfo.getString(bb2);
                    Log.i("chy", bb2 + " - " + aa2);
                    txt = txt + bb2 + " : " + aa2 + "\n";
                }


                updateUIListenner.updateinfo(txt);


                if (jsoninfo != null) {

                    if (jsoninfo.has("lativalue") && jsoninfo.has("longivalue")) {

                        LatLng now = new LatLng(Double.valueOf(jsoninfo.getString("lativalue")), Double.valueOf(jsoninfo.getString("longivalue")));
                        //转换坐标
                        LatLng trsnsPoint = WorldGS2MarsGS(now.latitude, now.longitude);

                        updateUIListenner.shownewmarker(now, jsoninfo.getString("id"));

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
}
