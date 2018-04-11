package lynn.andr.webserver;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import lynn.andr.webserver.util.Log;
import lynn.andr.webserver.util.NetUtil;

public class MainActivity extends Activity {

    private TextView mHostIpTv;
    private SocketService mService;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = ((SocketService.SocketServiceBinder) iBinder).getService();
            mService.startHttpServer(new SocketService.OnHttpServerStartListener() {
                @Override
                public void onHttpServerStart(final String hostIp) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mHostIpTv.setText("try visit http://" + hostIp + ":" + SocketService.PORT);
                        }
                    });

                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i("MainActivity", "onServiceDisconnected=" + componentName);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonStart = (Button) findViewById(R.id.btn_start);
        mHostIpTv = (TextView) findViewById(R.id.text_ip);
        buttonStart.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(checkHotspot()){
                    startAndBindSocketService();
                }else{
                    //openAPUI();
                    Toast.makeText(MainActivity.this,"Wi-Fi 热点未打开",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }

    private boolean checkHotspot(){
        return NetUtil.isWifiApOpen(MainActivity.this);
    }

    /**
     * 打开网络共享与热点设置页面
     */
    private void openAPUI() {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //打开网络共享与热点设置页面
        ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings$TetherSettingsActivity");
        intent.setComponent(comp);
        startActivity(intent);
    }


    private void startAndBindSocketService() {
        Intent bindService = new Intent(MainActivity.this, SocketService.class);
        bindService(bindService, conn, BIND_AUTO_CREATE);
        mHostIpTv.setText("starting webserver......");
    }

}
