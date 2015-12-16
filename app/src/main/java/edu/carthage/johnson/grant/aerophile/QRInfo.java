package edu.carthage.johnson.grant.aerophile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Grant on 12/14/2015.
 */
public class QRInfo implements Serializable{
    private String projectID;
    private String ip;
    private int port;


    public QRInfo(String projectID, String ip, int port)
    {
        this.projectID = projectID;
        this.ip = ip;
        this.port = port;
    }

    public byte[] getBytes() throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream objectOS = new ObjectOutputStream(out);
        objectOS.writeObject(this);

        return out.toByteArray();
    }

    public String ObjToString() throws IOException
    {
        byte[] bytes = getBytes();
        String objString = new String(bytes);
        return objString;
    }

    public static QRInfo Deserialize(byte[] data) throws IOException, ClassNotFoundException
    {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream objectIS = new ObjectInputStream(in);
        return (QRInfo) objectIS.readObject();
    }

    public static QRInfo StringToObj(String string) throws  IOException, ClassNotFoundException
    {
        byte[] bytes = string.getBytes();
        QRInfo qrInfo = Deserialize(bytes);
        return qrInfo;
    }

    public String getProjectID() {
        return projectID;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
