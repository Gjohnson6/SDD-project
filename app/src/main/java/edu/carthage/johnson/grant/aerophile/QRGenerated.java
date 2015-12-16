package edu.carthage.johnson.grant.aerophile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;

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
                ServerSocket hostSocket = new ServerSocket(0);//Socket to accept message from the partner
                String ip = "";
                try {
                    WifiManager wifiManager = (WifiManager) ctx.getSystemService(WIFI_SERVICE);
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
                System.out.println("Blocked");
                System.out.println(ip);
                Socket mySocket = hostSocket.accept();//Blocking call
                Toast toast = Toast.makeText(ctx, "Connected", Toast.LENGTH_LONG);
                toast.show();
                System.out.println("Connected");
                System.out.println("Connected");
                System.out.println("Connected");
                System.out.println("Connected");
                System.out.println("Connected");
                System.out.println("Connected");
                System.out.println("Connected");
                System.out.println("Connected");
                System.out.println("Connected");
                System.out.println("Connected");
                System.out.println("Connected");
                System.out.println("Connected");
                System.out.println("Connected");
                System.out.println("Connected");
                System.out.println("Connected");
                System.out.println("Connected");
                System.out.println("Connected");
                System.out.println("Connected");
                System.out.println("Connected");
                System.out.println("Connected");
                System.out.println("Connected");
                System.out.println("Connected");
                System.out.println("Connected");
                System.out.println("Connected");
                System.out.println("Connected");
                System.out.println("Connected");
                System.out.println("Connected");
                System.out.println("Connected");
                System.out.println("Connected");
                System.out.println("Connected");
                System.out.println("Connected");
                System.out.println("Connected");
                ObjectInputStream objIS = new ObjectInputStream(mySocket.getInputStream());
                MessageToHost messageFromPartner = (MessageToHost) objIS.readObject();

                hostSocket.close();
                //Now we have the message from the partner, we should be able to send them a filestructure. Hopefully.
                if(messageFromPartner.isToConnect())//If the partner wants to connect (ie, be sent the file structure)
                {
                    Socket toPartner = new Socket(messageFromPartner.getPartnerIP(), 55555);

                    FileStructure fileStructure = new FileStructure(new File(project.getFilepath()));
                    ObjectOutputStream objOS = new ObjectOutputStream(toPartner.getOutputStream());
                    objOS.writeObject(fileStructure);

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
