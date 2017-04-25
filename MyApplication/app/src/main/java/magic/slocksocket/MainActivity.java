package magic.slocksocket;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.magic.zxing.CaptureActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.UUID;


import magic.slocksocket.mina.BroadcastHelper;
import magic.slocksocket.mina.ConstantValues;
import magic.slocksocket.mina.updateService;
import magic.slocksocket.permissionshelper.PermissionsHelper;
import magic.slocksocket.permissionshelper.permission.DangerousPermissions;

import static magic.slocksocket.earthToMars.WorldGS2MarsGS;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button btn_openconfig, btn_conn, btn_cut, btn_scan, btn_send, btn_scan_new;
    private EditText et_lockid;
    private TextView info;
    private Intent serviceintent;


    private AMap aMap;
    private MapView mapView;


    private static final String FILENAME_app = "appconfigure";
    private SharedPreferences share_app;
    private SharedPreferences.Editor editor_app;

    static final String[] PERMISSIONS = new String[]{
            DangerousPermissions.CALENDAR,
            DangerousPermissions.CAMERA,
            DangerousPermissions.CONTACTS,
            DangerousPermissions.LOCATION,
            DangerousPermissions.MICROPHONE,
            DangerousPermissions.PHONE,
            DangerousPermissions.STORAGE,
            DangerousPermissions.SENSORS,
            DangerousPermissions.SMS
    };

    private PermissionsHelper permissionsHelper;


    // 接受消息广播 TAG
    private BroadcastReceiver contentRecevier = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {


            String data = intent.getStringExtra("data");

            Log.i("chy", data);
            info.setText(data);

            if (data != null) {


                if (data.equals("sessionOpened")) {
                    btn_conn.setVisibility(View.GONE);
                    btn_cut.setVisibility(View.VISIBLE);
                }


                try {
                    JSONObject jsoninfo = new JSONObject(data);
//                    Log.i("chy",info.toString());

//                    info.setText(jsoninfo.toString());

//
//                    "batvalue":"3990",
//                            "chargestatus":"FALSE",
//                            "device":"lock",
//                            "gpsdatatime":"17-04-11 14:18:57",
//                            "gpsstatus":"1",
//                            "id":"861358032238925",
//                            "latitude":"North",
//                            "lativalue":"121.56978",
//                            "lockstatus":"unlocked",
//                            "longitude":"East",
//                            "longivalue":"31.310332"

//                    String batvalue = jsoninfo.getString("batvalue");
//                    String chargestatus = jsoninfo.getString("chargestatus");
//                    String device = jsoninfo.getString("device");
//                    String gpsdatatime = jsoninfo.getString("gpsdatatime");
//                    String gpsstatus = jsoninfo.getString("gpsstatus");
//                    String id = jsoninfo.getString("id");
//                    String latitude = jsoninfo.getString("latitude");
//                    String lativalue = jsoninfo.getString("lativalue");
//                    String lockstatus = jsoninfo.getString("lockstatus");
//                    String longitude = jsoninfo.getString("longitude");
//                    String longivalue = jsoninfo.getString("longivalue");
//
//
//                    info.setText("batvalue:" + batvalue +
//                            "\nchargestatus:" + chargestatus +
//                            "\ndevice:" + device +
//                            "\ngpsdatatime:" + gpsdatatime +
//                            "\ngpsstatus:" + gpsstatus +
//                            "\nid:" + id +
//                            "\nlockstatus:" + lockstatus +
//                            "\nlatitude:" + latitude +
//                            "\nlativalue:" + lativalue +
//                            "\nlongitude:" + longitude +
//                            "\nlongivalue:" + longivalue
//                    );


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

                    info.setText(txt);


                    if (jsoninfo != null) {
//                        if (jsoninfo.has("ins") && !jsoninfo.has("lativalue") && !jsoninfo.has("longivalue")) {
//                            //暂时用 ins 存不存在判断是开锁 还是 get返回
//
//                            //上锁解析
//                            Log.i("chy", "上锁解析\n");
//                            String status = jsoninfo.getString("ins");
//
//                            if (status.equals("unlocked")) {
//                                //解锁成功
//                                Toast.makeText(MainActivity.this, "开锁成功", Toast.LENGTH_SHORT).show();
//                            } else if (status.equals("locked")) {
//                                //车已上锁
//                                Toast.makeText(MainActivity.this, "车已上锁", Toast.LENGTH_SHORT).show();
//                            } else {
//                                //错误
//                                Toast.makeText(MainActivity.this, "开锁失败", Toast.LENGTH_SHORT).show();
//                            }
//                        }

                        if (jsoninfo.has("lativalue") && jsoninfo.has("longivalue")) {

                            aMap.clear();
                            LatLng now = new LatLng(Double.valueOf(jsoninfo.getString("lativalue")), Double.valueOf(jsoninfo.getString("longivalue")));
                            //转换坐标
                            LatLng trsnsPoint = WorldGS2MarsGS(now.latitude, now.longitude);
                            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(now, 16));
                            drawMarkerOnMap(now, "车锁", "编号:" + jsoninfo.getString("id"));
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        mapView = (MapView) findViewById(R.id.main_map);
        mapView.onCreate(savedInstanceState);

        btn_openconfig = (Button) findViewById(R.id.btn_openconfig);
        btn_conn = (Button) findViewById(R.id.btn_connect);
        btn_cut = (Button) findViewById(R.id.btn_cut);
        btn_scan = (Button) findViewById(R.id.btn_scan);
        btn_send = (Button) findViewById(R.id.btn_send);

        btn_scan_new = (Button) findViewById(R.id.btn_scan_new);

        btn_openconfig.setOnClickListener(this);
        btn_conn.setOnClickListener(this);
        btn_cut.setOnClickListener(this);
        btn_scan.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        btn_scan_new.setOnClickListener(this);

        et_lockid = (EditText) findViewById(R.id.et_lockid);

        info = (TextView) findViewById(R.id.main_info);


        serviceintent = new Intent(MainActivity.this, updateService.class);//原本的服务


        uuid = getUDID(this);


        share_app = super.getSharedPreferences(FILENAME_app, Activity.MODE_PRIVATE);
        editor_app = share_app.edit();
        checkPermissions();

        initmap();//应该放在权限获得之后


        if (share_app.getBoolean("isfirst", true)) {

            editor_app.putString("ip", "www.hhsoftware.cn");
            editor_app.putInt("port", 33333);

            editor_app.putBoolean("isfirst", false);
            editor_app.commit();
        }


    }


    /**
     * 初始化AMap对象
     */
    private void initmap() {
        if (aMap == null) {
            Log.i("chy", "map = null");
            aMap = mapView.getMap();
        }

        // 缩放级别（zoom）：地图缩放级别范围为【4-20级】，值越大地图越详细
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        //使用 aMap.setMapTextZIndex(2) 可以将地图底图文字设置在添加的覆盖物之上
        aMap.setMapTextZIndex(2);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_openconfig:
                Intent config = new Intent(MainActivity.this, setconfig.class);
                startActivity(config);
                break;


            case R.id.btn_connect:

//                注册广播
                IntentFilter filter = new IntentFilter();
                filter.addAction(ConstantValues.RECEIVEMESSGE);
//                filter.addCategory(serviceintent.CATEGORY_DEFAULT);
                registerReceiver(contentRecevier, filter);
                Log.i("chy", "btn_connect");
                startservice();

                break;


            case R.id.btn_cut:


                if (connecting) {
                    unregisterReceiver(contentRecevier);
//                    contentRecevier = null;
                    connecting = false;
                    stopService(serviceintent);//                    serviceintent = null;
                    btn_conn.setVisibility(View.VISIBLE);
                    btn_cut.setVisibility(View.GONE);
                    info.setText("手动断开");
                }

                break;


            case R.id.btn_scan:
                Intent intent = new Intent(this, CaptureActivity.class);
                startActivityForResult(intent, 0);

                break;


            case R.id.btn_scan_new:
                Intent intent2 = new Intent(this, CaptureActivity.class);
                startActivityForResult(intent2, 1);
                break;


            case R.id.btn_send:
                gounlock(et_lockid.getText().toString(), uuid);
                break;

        }
    }


    private void connect() {
//        注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstantValues.RECEIVEMESSGE);
//                filter.addCategory(serviceintent.CATEGORY_DEFAULT);
        registerReceiver(contentRecevier, filter);
        Log.i("chy", "btn_connect");
        startservice();
    }


    private void disconnect() {
        if (connecting) {
            unregisterReceiver(contentRecevier);
//                    contentRecevier = null;
            connecting = false;
            stopService(serviceintent);//                    serviceintent = null;
        }
    }


    private void gounlock(String lockid, String userid) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device", "app");
            jsonObject.put("id", userid);
            jsonObject.put("ins", "unlock");
            jsonObject.put("lockid", lockid);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        String info = jsonObject.toString();
        Log.i("chy", info);


        if (info.isEmpty())
            return;
        // 点击发送消息到服务器
        BroadcastHelper.sendBroadCast(MainActivity.this,
                ConstantValues.SENDMESSGE, "data_send", info);
//        content.setText("");

    }


    boolean connecting;

    private void startservice() {
        Boolean haveInternet = isNetworkAvailable(getBaseContext());
        if (haveInternet) {
            startService(serviceintent);
            connecting = true;
        } else {
            Toast.makeText(MainActivity.this, "请确保在有网络环境！", Toast.LENGTH_LONG).show();

        }
    }


    /**
     * @param context Context
     * @return true 表示网络可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    Log.i("chy", "net is Available");
                    return true;
                }
            }
        }
        Log.i("chy", "net is laji");
        return false;
    }


    private void checkPermissions() {
        permissionsHelper = new PermissionsHelper(this, PERMISSIONS, share_app.getBoolean("perIsFirst", true));
        editor_app.putBoolean("perIsFirst", false);
        editor_app.commit();


        if (permissionsHelper.checkAllPermissions(PERMISSIONS)) {
            permissionsHelper.onDestroy();
            //do nomarl
        } else {
            //申请权限
            permissionsHelper.startRequestNeedPermissions();
//            permissionsHelper.setParams(setDialogInfo(new DialogInfo()));

        }
        permissionsHelper.setonAllNeedPermissionsGrantedListener(new PermissionsHelper.onAllNeedPermissionsGrantedListener() {


            @Override
            public void onAllNeedPermissionsGranted() {
                Log.d("test", "onAllNeedPermissionsGranted");
            }

            @Override
            public void onPermissionsDenied() {
                Log.d("test", "onPermissionsDenied");
            }

            @Override
            public void hasLockForever() {

            }

            @Override
            public void onBeforeRequestFinalPermissions(PermissionsHelper helper) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private String bikeid;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        permissionsHelper.onActivityResult(requestCode, resultCode, data);


//    处理二维码回调
        if (resultCode == RESULT_OK && requestCode == 0) {
            Bundle mBundle = data.getExtras();
            String result = mBundle.getString("result");

            String[] resultStrArray = result.split("/");

            bikeid = resultStrArray[resultStrArray.length - 1];

            Toast.makeText(MainActivity.this, "扫描结果为:" + result + "\n车锁id为:" + bikeid, Toast.LENGTH_SHORT).show();

            gounlock(bikeid, uuid);

        } else if (resultCode == RESULT_OK && requestCode == 1) {

            Bundle mBundle = data.getExtras();
            String result = mBundle.getString("result");

            String[] resultStrArray = result.split("/");

            bikeid = resultStrArray[resultStrArray.length - 1];

            Toast.makeText(MainActivity.this, "扫描结果为:" + result + "\n车锁id为:" + bikeid, Toast.LENGTH_SHORT).show();


            getalldevice(bikeid, uuid);

            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(3000);

                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    getlockdevice(bikeid, uuid);

                }

            }.start();

        }


    }


    private void getlockdevice(String lockid, String userid) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device", "app");
            jsonObject.put("id", userid);
            jsonObject.put("ins", "getinfo");
            jsonObject.put("info", "ok");
            jsonObject.put("lockid", lockid);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        String info = jsonObject.toString();
        Log.i("chy", "do getlockdevice");

        if (info.isEmpty())
            return;
        // 点击发送消息到服务器
        BroadcastHelper.sendBroadCast(MainActivity.this,
                ConstantValues.SENDMESSGE, "data_send", info);
//        content.setText("");
    }


    private void getalldevice(String lockid, String userid) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device", "app");
            jsonObject.put("id", userid);
            jsonObject.put("ins", "getall");
            jsonObject.put("info", "ok");
            jsonObject.put("lockid", lockid);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        String info = jsonObject.toString();
        Log.i("chy", "do getalldevice");

        if (info.isEmpty())
            return;
        // 点击发送消息到服务器
        BroadcastHelper.sendBroadCast(MainActivity.this,
                ConstantValues.SENDMESSGE, "data_send", info);
//        content.setText("");
    }


    //放置marker
    private void drawMarkerOnMap(LatLng point, String id, String text) {

        if (aMap != null && point != null) {

            //设置图标
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.bike_test);


            MarkerOptions markerOptions = new MarkerOptions().anchor(0.5f, 1)
                    .position(point)
                    .title(id)
                    .snippet(text)
                    .icon(BitmapDescriptorFactory.fromBitmap(changeBitmap(bmp, 0.5f, 0.5f)));
            aMap.addMarker(markerOptions);
        }
//        return null;
    }

    private static Bitmap changeBitmap(Bitmap bitmap, float length, float width) {
        Matrix matrix = new Matrix();
        matrix.postScale(length, width); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }


    //获取or 生成唯一识别码
    protected static final String PREFS_FILE = "gank_device_id.xml";
    protected static final String PREFS_DEVICE_ID = "gank_device_id";
    protected static String uuid;

    public synchronized static String getUDID(Context mContext) {
        if (uuid == null) {
            if (uuid == null) {
                final SharedPreferences prefs = mContext.getApplicationContext().getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
                final String id = prefs.getString(PREFS_DEVICE_ID, null);

                if (id != null) {
                    // Use the ids previously computed and stored in the prefs file
                    uuid = id;
                } else {

                    final String androidId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
                    // Use the Android ID unless it's broken, in which case fallback on deviceId,
                    // unless it's not available, then fallback on a random number which we store
                    // to a prefs file
                    try {
                        if (!"9774d56d682e549c".equals(androidId)) {
                            uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8")).toString();
                        } else {
                            final String deviceId = ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                            uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")).toString() : UUID.randomUUID().toString();
                        }
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }

                    // Write the value out to the prefs file
                    prefs.edit().putString(PREFS_DEVICE_ID, uuid).commit();
                }
            }
        }
        return uuid;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        permissionsHelper.onDestroy();
        unregisterReceiver(contentRecevier);
    }


}
