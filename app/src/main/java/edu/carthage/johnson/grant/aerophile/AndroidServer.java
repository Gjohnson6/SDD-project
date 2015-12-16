package edu.carthage.johnson.grant.aerophile;

import android.app.Activity;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import java.net.InetAddress;
import java.util.Locale;

import edu.carthage.johnson.grant.aerophile.ClientProxyThread;


public class AndroidServer extends Activity {

    private TextView ip = null;
    private TextView port = null;
    private TextView connect_flag = null;
    private String true_or_false = null;
    private int port_num = 55555;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_server);
        connect_flag = (TextView)findViewById(R.id.connect_flag);
        //Button buttonSend = (Button)findViewById(R.id.send);
        ip = (TextView)findViewById(R.id.ip_address);
        port = (TextView)findViewById(R.id.port_number);

        String result2 = "port: " + port_num;
        port.setText(result2);
        //this code is an example of threading which may be useful later on in development
        //and it proves that ive been working on this
        /*Thread thread = new Thread(new Runnable(){
            public void run(){
                InetAddress ip_inet = null;
                try {
                ip_inet = InetAddress.getLocalHost();
                final String result1 = "ip: " + ip_inet.getHostAddress();
                runOnUiThread(new Thread(new Runnable() {
                    public void run() {
                        ip.setText(result1);
                    }}));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        thread.start();*/
        String result1 = "ip: " + getIP();
        ip.setText(result1);
    }

    public void onClick(View arg0) {
        new ClientProxyThread(port_num, this).execute();
    }
    /**
     * Get the IP of current Wi-Fi connection
     * @return IP as string
     */
    //http://itekblog.com/android-get-mobile-ip-address/
    private String getIP() {
        try {
            WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            return String.format(Locale.getDefault(), "%d.%d.%d.%d",
                    (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                    (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        } catch (Exception ex) {
            //Log.e(TAG, ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }
}
