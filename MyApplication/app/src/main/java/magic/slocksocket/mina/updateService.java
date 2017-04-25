package magic.slocksocket.mina;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.Charset;

/**
 * Created by chenhaoyu on 17/1/2.
 */
public class updateService extends Service {


    private String HOST_IP;
    private int HOST_PORT;

    private DataHandler dataHandler;
    private IoSession session;
    private IoConnector connector;

    private TextLineCodecFactory myTextLineCodecFactory;


    private static final String FILENAME = "appconfigure";
    private SharedPreferences share;
//    private SharedPreferences.Editor editor;


    private BroadcastReceiver sendMessage = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {

                String data = intent.getStringExtra("data_send");

                session.write(data);
//                session.write(URLEncoder.encode(data, "UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };




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
        Log.i("chy", "net is null");
        return false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {


        Log.i("chy", "service start");

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstantValues.SENDMESSGE);
//        filter.addCategory(Intent.CATEGORY_DEFAULT);


        share = super.getSharedPreferences(FILENAME, Activity.MODE_PRIVATE);
//        editor = share.edit();

        HOST_IP = share.getString("ip", "0.0.0.0");
        HOST_PORT = share.getInt("port", 8888);


        dataHandler = new DataHandler();
        registerReceiver(sendMessage, filter);

        myTextLineCodecFactory = new TextLineCodecFactory(Charset.forName("UTF-8"), LineDelimiter.WINDOWS
                .getValue(), LineDelimiter.WINDOWS
                .getValue());
        myTextLineCodecFactory.setDecoderMaxLineLength(40960);//设置最大40k


        new Thread(new Runnable() {

            @Override
            public void run() {
                connector = new NioSocketConnector();

                connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(myTextLineCodecFactory));


                connector.getSessionConfig().setReadBufferSize(1024);


                connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE,
                        10);

                connector.setHandler(dataHandler);


                try {

                    // 这里是异步操作 连接后立即返回
                    ConnectFuture future = connector.connect(new InetSocketAddress(
                            HOST_IP, HOST_PORT));
                    future.awaitUninterruptibly();// 等待连接创建完成

                    session = future.getSession();

                    session.getCloseFuture().awaitUninterruptibly();// 等待连接断开
//                connector.dispose();

                } catch (Exception e) {
                    e.printStackTrace();
                    BroadcastHelper.sendBroadCast(getBaseContext(),
                            ConstantValues.RECEIVEMESSGE, "data", "连接失败");
                }


            }
        }).start();
        super.onCreate();
    }


    @Override
    public void onDestroy() {
        unregisterReceiver(sendMessage);
        sendMessage = null;
        connector.dispose();

        super.onDestroy();
    }


    private class DataHandler extends IoHandlerAdapter {

        @Override
        public void sessionCreated(IoSession session) throws Exception {
        }

        @Override
        public void sessionOpened(IoSession session) throws Exception {
//            session.write("connected");

            BroadcastHelper.sendBroadCast(getBaseContext(),
                    ConstantValues.RECEIVEMESSGE, "data", "sessionOpened");
        }

        @Override
        public void sessionClosed(IoSession session) throws Exception {
//            Log.i("chy", "closed");


            BroadcastHelper.sendBroadCast(getBaseContext(),
                    ConstantValues.RECEIVEMESSGE, "data", "sessionClosed");
//            因为服务本身被干掉了。。所以这条发布出去

        }

        @Override
        public void sessionIdle(IoSession session, IdleStatus status)
                throws Exception {
        }

        @Override
        public void exceptionCaught(IoSession session, Throwable cause)
                throws Exception {
        }

        @Override
        public void messageReceived(IoSession session, Object message)
                throws Exception {
            //处理从服务端接收到的消息


//            Log.i("chy", "服务器返message：\n" + message + "\n");
            String result = null;
            result = URLDecoder.decode(message.toString(), "UTF-8");
            Log.i("chy", "服务器返回数据：\n" + result + "\n");

            BroadcastHelper.sendBroadCast(getBaseContext(),
                    ConstantValues.RECEIVEMESSGE, "data", result);

        }

        @Override
        public void messageSent(IoSession session, Object message)
                throws Exception {

        }
    }

}
