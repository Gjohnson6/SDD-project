package edu.carthage.johnson.grant.aerophile;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Telephony;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Grant on 12/15/2015.
 */
public class PartnerProject implements Serializable, Parcelable{
    private String projectName;
    private int partnerCount = 0;
    private FileStructure fileStructure;
    private String id;
    private String address; //Address to host for bluetooth

    public PartnerProject(String projectName,String filepath){
        this.projectName = projectName;
        this.fileStructure = new FileStructure(new File(filepath));
        this.id = "";
        this.address = "";
    }

    public FileStructure getFileStructure()
    {
        return fileStructure;
    }
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getPartnerCount() {
        return partnerCount;
    }

    public void setPartnerCount(int partnerCount) {
        this.partnerCount = partnerCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(projectName);
        dest.writeInt(partnerCount);
        dest.writeSerializable(fileStructure);
        dest.writeString(id);
        dest.writeString(address);
    }

    public static final Parcelable.Creator<PartnerProject> CREATOR = new Parcelable.Creator<PartnerProject>() {
        public PartnerProject createFromParcel(Parcel in) {
            return new PartnerProject(in);
        }

        public PartnerProject[] newArray(int size) {
            return new PartnerProject[size];
        }
    };


    private PartnerProject(Parcel in)
    {
        String test = Project.class.getCanonicalName();
        String temp = in.readString();
        if(test.equals(temp))
        {
            projectName = in.readString();
        }
        else
        {
            projectName = temp;
        }
        partnerCount = in.readInt();
        fileStructure = (FileStructure) in.readSerializable();
        id = in.readString();
        address = in.readString();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
