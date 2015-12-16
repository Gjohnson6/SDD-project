package edu.carthage.johnson.grant.aerophile;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
            QRInfo qrInfo = new QRInfo("", "", 1);
            if(qrInfo != null) {
                try {
                    String[] info = contents.split("¥");
                    qrInfo = new QRInfo(info[2], info[0], Integer.parseInt(info[1]));
                    ip = qrInfo.getIp();
                    Toast toast = Toast.makeText(ctx, ip, Toast.LENGTH_SHORT);
                    toast.show();
                    port = qrInfo.getPort();
                    System.out.println(ip + ":" + port);
                    Toast toast2 = Toast.makeText(ctx, port, Toast.LENGTH_LONG);
                    toast2.show();
                    Socket toHost = new Socket(ip, port);
                    ObjectOutputStream outToHost = new ObjectOutputStream(toHost.getOutputStream());
                    MessageToHost messageToHost = new MessageToHost(true, qrInfo.getProjectID(), getIP());//Tell the host we want to connect and what we want to connect to
                    outToHost.writeObject(messageToHost);//Send the message to the host
                    PartnerProxy partnerProxy = new PartnerProxy();
                    partnerProxy.doInBackground(null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //use qrInfo to connect to host


            //get the file info from host

            //create project based on that
        }
    }

    private class PartnerProxy extends AsyncTask<Void, Void, FileStructure>{

        @Override
        protected FileStructure doInBackground(Void... params)
        {
            FileStructure fileStruct = null;
            try
            {
                socket = new Socket(ip, port);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try
            {
                ServerSocket partnerSocket = new ServerSocket(12345);//Socket to accept FileStructures from the host
                Socket mySocket = partnerSocket.accept();
                ObjectInputStream objIS = new ObjectInputStream(mySocket.getInputStream());
                fileStruct = (FileStructure) objIS.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
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
