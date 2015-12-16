package edu.carthage.johnson.grant.aerophile;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;


public class PartnerName extends ActionBarActivity {

    private Context ctx = this;
    private SavedPartnerProjects savedProjects;
    private Socket socket;
    private String ip;
    private int port;
    private QRInfo qrInfo;

    private final static int REQUEST_ENABLE_BT = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_name);

        ArrayList<PartnerProject> projects = new ArrayList<>();

        try
        {
            savedProjects = savedProjects.readProjects(this, "PartneredProjects.ser");
        } catch (Exception e)
        {
            savedProjects = new SavedPartnerProjects();
        }
        projects = savedProjects.getProjects();

        //Will read in from storage

        CustomPartnerProjectAdapter adapter = new CustomPartnerProjectAdapter(getApplicationContext(), projects, ctx );
        ListView listView = (ListView) findViewById(R.id.ProjectListView);
        //listView.setItemsCanFocus(false);
        listView.setAdapter(adapter);
    }

    public void PartnerNewProject(View view)
    {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String contents = scanResult.getContents();
            qrInfo = new QRInfo("", "", 1);
            if(qrInfo != null) {
                try {
                    String[] info = contents.split("¥");
                    System.out.println(info[1]);

                    qrInfo = new QRInfo(info[2], info[0], (int)Integer.parseInt(info[1]));

                    ip = qrInfo.getIp();
                    port = qrInfo.getPort();


                    PartnerProxy partnerProxy = new PartnerProxy();
                    System.out.println("Executing");
                    partnerProxy.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            System.out.println("BroadcastReceiver");

            if(BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            }
        }
    };

    private class PartnerProxy extends AsyncTask<QRInfo, Void, FileStructure>{

        @Override
        protected FileStructure doInBackground(QRInfo... params)
        {
            FileStructure fileStruct = null;
            System.out.println("In partnerproxy");


            System.out.println("IP: " + qrInfo.getIp());
            System.out.println("Port: " + qrInfo.getPort());

            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(bluetoothAdapter == null)
            {
                Toast.makeText(ctx, "Bluetooth is disabled", Toast.LENGTH_LONG).show();
            }

            if(!bluetoothAdapter.isEnabled())
            {
                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
            }

            //bluetoothAdapter.startDiscovery();
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(qrInfo.getIp());

            UUID uuid = UUID.fromString("29ae6c19-ab42-477a-9236-49750c158494");
            try
            {
                BluetoothSocket socketToHost = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);
                socketToHost.connect();
                ObjectOutputStream outToHost = new ObjectOutputStream(socketToHost.getOutputStream());
                System.out.println(BluetoothAdapter.getDefaultAdapter().getAddress());
                MessageToHost messageToHost = new MessageToHost(true, qrInfo.getProjectID(), BluetoothAdapter.getDefaultAdapter().getAddress());//Tell the host we want to connect and what we want to connect to
                outToHost.writeObject(messageToHost);

                //Accept object from host

                //UUID uuid = UUID.fromString("29ae6c19-ab42-477a-9236-49750c158494");
                BluetoothServerSocket bluetoothServerSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("Aerophile", uuid);
                BluetoothSocket partnerSocket = bluetoothServerSocket.accept();

                ObjectInputStream inFromPartner = new ObjectInputStream(partnerSocket.getInputStream());
                PartnerProject project = (PartnerProject) inFromPartner.readObject();

                savedProjects.addProject(project);
                savedProjects.saveProjects(ctx, "PartneredProjects.ser");

            } catch (Exception e)
            {
                e.printStackTrace();
            }

            //Socket toHost = new Socket("10.200.122.255", qrInfo.getPort());
            //System.out.println("Past socket");
            //ObjectOutputStream outToHost = new ObjectOutputStream(toHost.getOutputStream());
            System.out.println("Past outToHost");
            System.out.println("Past message");
            //outToHost.writeObject(messageToHost);//Send the message to the host
            System.out.println("Sent");


            return fileStruct;
        }
    }



    protected void onPostExecute(FileStructure fileStructure)
    {
        //populate listview
    }

    public void onConnectButton()
    {
        //do something
    }

    private String getIP()
    {
        String ip = "";
        try {
            WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            ip = String.format(Locale.getDefault(), "%d.%d.%d.%d",
                    (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                    (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        } catch (Exception ex) {
            //Log.e(TAG, ex.getMessage());
            ex.printStackTrace();
        }
        return ip;
    }
}
