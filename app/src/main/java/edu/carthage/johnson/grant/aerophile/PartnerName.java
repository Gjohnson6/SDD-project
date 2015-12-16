package edu.carthage.johnson.grant.aerophile;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Formatter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Locale;

public class PartnerName extends ActionBarActivity {

    private Context ctx = this;
    private SavedProjects savedProjects;
    private Socket socket;
    private String ip;
    private int port;
    private QRInfo qrInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_name);

        ArrayList<Project> projects = new ArrayList<>();

        try
        {
            savedProjects = savedProjects.readProjects(this, "PartneredProjects.ser");
        } catch (Exception e)
        {
            savedProjects = new SavedProjects();
        }
        projects = savedProjects.getProjects();

        //Will read in from storage
        Project project = new Project("Name", "/");
        projects.add(project);

        CustomProjectAdapter adapter = new CustomProjectAdapter(getApplicationContext(), projects, ctx );
        final ListView listView = (ListView) findViewById(R.id.ProjectListView);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //Message to host (Filename)
                String fileName = (String) ((ListView)parent).getAdapter().getItem(position);
                MessageToHost messageToHost = new MessageToHost(fileName, getIP());


                //Connect to host
                ServerProxyThread serverProxyThread = new ServerProxyThread();


                //Send object

                return false;
            }
        });
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
            System.out.println(contents);
            System.out.println(contents);
            System.out.println(contents);
            System.out.println(contents);
            System.out.println(contents);
            System.out.println(contents);
            System.out.println(contents);
            System.out.println(contents);
            System.out.println(contents);
            System.out.println(contents);
            System.out.println(contents);
            System.out.println(contents);
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
            //use qrInfo to connect to host


            //get the file info from host

            //create project based on that
        }
    }

    private class PartnerProxy extends AsyncTask<QRInfo, Void, FileStructure>{

        @Override
        protected FileStructure doInBackground(QRInfo... params)
        {
            FileStructure fileStruct = null;
            System.out.println("In partnerproxy");
            try
            {

                System.out.println("IP: " + qrInfo.getIp());
                System.out.println("Port: " + qrInfo.getPort());

                
                Socket toHost = new Socket("10.200.122.255", qrInfo.getPort());
                System.out.println("Past socket");
                ObjectOutputStream outToHost = new ObjectOutputStream(toHost.getOutputStream());
                System.out.println("Past outToHost");
                MessageToHost messageToHost = new MessageToHost(true, qrInfo.getProjectID(), getIP());//Tell the host we want to connect and what we want to connect to

                System.out.println("Past message");
                outToHost.writeObject(messageToHost);//Send the message to the host
                System.out.println("Sent");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

//            try
  //          {
    //            ServerSocket partnerSocket = new ServerSocket(12345);//Socket to accept FileStructures from the host
      //          Socket mySocket = partnerSocket.accept();
        //        ObjectInputStream objIS = new ObjectInputStream(mySocket.getInputStream());
          //      fileStruct = (FileStructure) objIS.readObject();
///            } catch (IOException e) {
   //             e.printStackTrace();
     //       } catch (ClassNotFoundException e) {
        //        e.printStackTrace();
       //     }
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
