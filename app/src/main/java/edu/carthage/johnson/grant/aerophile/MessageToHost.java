package edu.carthage.johnson.grant.aerophile;

import java.io.Serializable;

/**
 * Created by Grant on 12/14/2015.
 */
public class MessageToHost implements Serializable {
    private String filename;
    private boolean toConnect = false;//This determines whether the message is to be added to the project (true) or to get a file (false)
    private String projectID;
    private String partnerIP;

    public MessageToHost(String filename, String partnerIP)
    {
        this.filename = filename;
        this.partnerIP = partnerIP;
    }

    public MessageToHost(boolean toConnect, String projectID, String partnerIP)
    {
        this.toConnect = toConnect;
        this.projectID = projectID;
        this.partnerIP = partnerIP;
    }

    public String getProjectID() {
        return projectID;
    }

    public boolean isToConnect() {
        return toConnect;
    }

    public String getFilename() {
        return filename;
    }

    public String getPartnerIP() {
        return partnerIP;
    }
}
