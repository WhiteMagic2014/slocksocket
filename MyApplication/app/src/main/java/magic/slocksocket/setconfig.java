package magic.slocksocket;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by chenhaoyu on 2017/4/3.
 */

public class setconfig extends Activity implements View.OnClickListener {


    private EditText et_ip, et_port;
    private Button btn_save, btn_exit;
    private TextView info;

    private static final String FILENAME = "appconfigure";
    private SharedPreferences share;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setconfig);


        share = super.getSharedPreferences(FILENAME, Activity.MODE_PRIVATE);
        editor = share.edit();





//        isfirst = share.getBoolean("first", true);

        et_ip = (EditText) findViewById(R.id.et_ip);
        et_port = (EditText) findViewById(R.id.et_port);

        btn_exit = (Button) findViewById(R.id.btn_exit);
        btn_save = (Button) findViewById(R.id.btn_save);

        info = (TextView) findViewById(R.id.tv_setinfo);

        btn_exit.setOnClickListener(this);
        btn_save.setOnClickListener(this);


//        HOST_IP = share.getString("ip", "0.0.0.0");
//        HOST_PORT = share.getInt("port", 8888);


        info.setText(getString(R.string.nowip) + share.getString("ip", "0.0.0.0") + "\n" +
                getString(R.string.nowport) + share.getInt("port", 8888));

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_save:

                editor.putString("ip", et_ip.getText().toString());
                editor.putInt("port", Integer.parseInt(et_port.getText().toString()));
                editor.commit();

                info.setText(getString(R.string.savechange)+"\n"+ getString(R.string.nowip)  + share.getString("ip", "0.0.0.0") + "\n" +
                        getString(R.string.nowport) + share.getInt("port", 8888));


                break;


            case R.id.btn_exit:

                finish();
                break;

        }

    }
}
