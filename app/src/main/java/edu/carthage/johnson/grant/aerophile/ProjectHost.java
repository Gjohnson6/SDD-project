package edu.carthage.johnson.grant.aerophile;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;


public class ProjectHost extends ActionBarActivity{

    private String baseFilePath;
    private String currentFilePath;
    private String projectID;
    private File currentFile;
    private ListView listView;
    private FileBrowser fileBrowser;
    private Project currProj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browser);
        listView = (ListView) findViewById(R.id.list);
        fileBrowser = new FileBrowser();

        baseFilePath = Environment.getExternalStorageDirectory().getPath();

        if(getIntent().hasExtra("Project"))
        {
            currProj = getIntent().getParcelableExtra("Project");
            baseFilePath = currProj.getFilepath();
            setTitle(currProj.getProjectName());
            projectID = currProj.getId();
        }
        currentFilePath = baseFilePath;
        currentFile = new File(currentFilePath);

        fileBrowser.PopulateList(this, baseFilePath, listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String fileName = (String) ((ListView)parent).getAdapter().getItem(position);
                String newFilePath;
                if(currentFilePath.endsWith(File.separator))
                {
                    newFilePath = currentFilePath + fileName;
                }
                else
                {
                    newFilePath  = currentFilePath + File.separator + fileName;
                }

                File newFile = new File(newFilePath);
                if(newFile.isDirectory())
                {
                    currentFilePath = newFilePath;
                    currentFile = newFile;
                    fileBrowser.PopulateList(parent.getContext(), currentFilePath, listView);
                }
            }
        });
    }

    private void PopulateList(String path)
    {

        ArrayList files = new ArrayList();
        File directory = new File(path);
        directory.setReadable(true);
        boolean readable = directory.canRead();
        String[] list = directory.list();
        if(list != null)
        {
            for(String file : list)
            {
                if(!file.startsWith("."))
                {
                    files.add(file);
                }
            }
        }
        Collections.sort(files);

        ArrayAdapter adapter = new CustomFileAdapter(this, files);
        listView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed()
    {
        try {
            if (!currentFilePath.equals(baseFilePath)) {
                String parentPath = currentFile.getCanonicalFile().getParentFile().getCanonicalPath();

                PopulateList(parentPath);
                currentFilePath = parentPath;
                currentFile = new File(currentFilePath);
            } else {
                super.onBackPressed();
            }
        }
        catch (Exception e)
        {
            Toast toast = new Toast(this);
            toast.setText(e.getMessage());
            toast.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_project_menu, menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.qr_code, menu);
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

        if(id == R.id.qr_code) {
            //IP address
            //Port
            //Project ID
            String ip = "";
            try {
                WifiManager wifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                int ipAddress = wifiInfo.getIpAddress();
                String ip2 = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
                ip = String.format(Locale.getDefault(), "%d.%d.%d.%d",
                        (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                        (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
            } catch (Exception ex) {
                //Log.e(TAG, ex.getMessage());
                ex.printStackTrace();
            }

            int port = 55555;
            ip = BluetoothAdapter.getDefaultAdapter().getAddress();
            QRInfo qrInfo = new QRInfo(projectID, ip, port);

            String qrInputText = "";
            try {
                qrInputText = qrInfo.getIp() + "¥" + qrInfo.getPort() + "¥" + qrInfo.getProjectID();
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            Intent printOut = new Intent(this, QRGenerated.class);
            printOut.putExtra("toGen", qrInputText);

            if(currProj != null)
            {
                printOut.putExtra("Project", (Parcelable) currProj);
            }
            startActivity(printOut);
        }
        return super.onOptionsItemSelected(item);
    }
}
