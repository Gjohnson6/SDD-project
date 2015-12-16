package edu.carthage.johnson.grant.aerophile;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;


public class PartnerBrowser extends ActionBarActivity {
    PartnerProject partnerProject;
    FileStructure currFS;
    ListView listView;
    FileBrowser fileBrowser;
    Context ctx = this;
    private final static int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browser);
        listView = (ListView) findViewById(R.id.list);
        fileBrowser = new FileBrowser();

        if(getIntent().hasExtra("PartnerProject"))
        {
            partnerProject = getIntent().getParcelableExtra("PartnerProject");
            currFS = partnerProject.getFileStructure();
        }

        fileBrowser.PopulateList(this, partnerProject.getFileStructure(), listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Find which FileStructure was clicked

                String filename = (String) ((ListView)parent).getAdapter().getItem(position);
                for(FileStructure files : currFS.getFiles())
                {
                    if(filename.equals(files.getName()))
                    {
                        currFS = files;
                        break;
                    }
                }

                if(currFS.isFile())
                {
                    //send to host
                    PartnerProxy partnerProxy = new PartnerProxy();
                    partnerProxy.execute();
                    Toast.makeText(ctx,currFS.getFilepath(), Toast.LENGTH_LONG ).show();

                }
                else
                {
                    fileBrowser.PopulateList(ctx, currFS, listView);
                }
            }
        });
    }

    private class PartnerProxy extends AsyncTask<QRInfo, Void, FileStructure> {

        @Override
        protected FileStructure doInBackground(QRInfo... params)
        {
            FileStructure fileStruct = null;

            //Get our bluetoothAdapter
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            //Make sure bluetooth is enabled
            if(bluetoothAdapter == null)
            {
                Toast.makeText(ctx, "Bluetooth is disabled", Toast.LENGTH_LONG).show();
            }

            //If it's not, tell the user
            if(!bluetoothAdapter.isEnabled())
            {
                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
            }

            //bluetoothAdapter.startDiscovery();

            //Use the address we got from the QR to get the host's device
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(partnerProject.getAddress());

            //Static uuid
            UUID uuid = UUID.fromString("29ae6c19-ab42-477a-9236-49750c158494");
            try
            {
                //Create a socket to the host
                BluetoothSocket socketToHost = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);
                socketToHost.connect();//Connect to the host
                ObjectOutputStream outToHost = new ObjectOutputStream(socketToHost.getOutputStream());//Open an output stream to the host
                //Create a message to send to the host
                MessageToHost messageToHost = new MessageToHost(currFS.getFilepath(), BluetoothAdapter.getDefaultAdapter().getAddress());//Tell the host what file we want and what out address is
                outToHost.writeObject(messageToHost);//Send the file to the host

                //Accept object from host

                BluetoothServerSocket bluetoothServerSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("Aerophile", uuid);
                BluetoothSocket partnerSocket = bluetoothServerSocket.accept();
                ObjectInputStream inFromPartner = new ObjectInputStream(partnerSocket.getInputStream());
                File fileFromHost = (File) inFromPartner.readObject();
                System.out.println("Got File: " + fileFromHost.getName());
                //Check if external storage is available

                    try
                    {
                        System.out.println("Before Create New File");
                        fileFromHost.getParentFile().mkdirs();
                        fileFromHost.createNewFile();


                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_partner_browser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
