package edu.carthage.johnson.grant.aerophile;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Formatter;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.util.Locale;
import java.util.UUID;

public class QRGenerated extends ActionBarActivity {
    Project project;
    private Context ctx = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrgenerated);
        HostProxy hostProxy = new HostProxy();
        hostProxy.execute();
        //Find screen size
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3/4;
        String qrInputText = "";
        if(getIntent().hasExtra("toGen"))
        {
            qrInputText = getIntent().getStringExtra("toGen");
        }

        if(getIntent().hasExtra("Project"))
        {
            project = getIntent().getParcelableExtra("Project");
        }

        //Encode with a QR Code image
        QREncoder qrCodeEncoder = new QREncoder(qrInputText,
                null,
                Contents.Type.TEXT,
                BarcodeFormat.QR_CODE.toString(),
                smallerDimension);
        try {
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            ImageView myImage = (ImageView) findViewById(R.id.theCode);
            myImage.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }


    }

    private class HostProxy extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                while(true) {
                    System.out.println("Beginning of while");

                    //Bluetooth
                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    //Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    //discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    //startActivity(discoverableIntent);
                    UUID uuid = UUID.fromString("29ae6c19-ab42-477a-9236-49750c158494");
                    BluetoothServerSocket bluetoothServerSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("Aerophile", uuid);


                    BluetoothSocket partnerSocket = bluetoothServerSocket.accept();

                    while(partnerSocket.isConnected()) {
                        ObjectInputStream inFromPartner = new ObjectInputStream(partnerSocket.getInputStream());
                        MessageToHost messageToHost = (MessageToHost) inFromPartner.readObject();

                        System.out.println(messageToHost.getPartnerIP());
                        BluetoothDevice partnerDevice = bluetoothAdapter.getRemoteDevice(messageToHost.getPartnerIP());

                        BluetoothSocket socketToPartner = partnerDevice.createInsecureRfcommSocketToServiceRecord(uuid);


                        socketToPartner.connect();
                        ObjectOutputStream outToPartner = new ObjectOutputStream(socketToPartner.getOutputStream());

                        if (messageToHost.isToConnect()) {

                            PartnerProject partnerProject = new PartnerProject(project.getProjectName(), project.getFilepath());
                            partnerProject.setId(project.getId());
                            partnerProject.setAddress(bluetoothAdapter.getAddress());
                            outToPartner.writeObject(partnerProject);
                        } else {
                            //This is the issue. IT's making a new file instead of reading the file at that path
                            File dir = Environment.getExternalStorageDirectory();
                            String fileString = messageToHost.getFilename().replace("/storage/emulated/0/", "");
                            File fileToSend = new File(dir, fileString);
                            
                            RandomAccessFile f = new RandomAccessFile(fileToSend, "r");
                            try
                            {
                                long longlength = f.length();
                                int length = (int) longlength;

                                byte[] data = new byte[length];
                                f.readFully(data);

                                FileToPartner fileToPartner = new FileToPartner(data, fileToSend);
                                outToPartner.writeObject(fileToPartner);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }

                        System.out.println(project.getProjectName());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}
