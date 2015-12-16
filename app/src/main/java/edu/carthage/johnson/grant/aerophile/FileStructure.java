package edu.carthage.johnson.grant.aerophile;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Grant on 12/15/2015.
 */
public class FileStructure implements Serializable, Parcelable{
    private String filepath;
    private String name;
    private boolean isFile = false;
    private ArrayList<FileStructure> files = new ArrayList<>();

    public FileStructure(File file)
    {
        this.filepath = file.getAbsolutePath();
        this.name = file.getName();
        if(file.listFiles() != null)
        {
            for(File fileInList : file.listFiles())
            {
                files.add(new FileStructure(fileInList));
            }
        }
        else
        {
            isFile = true;
        }
    }

    public ArrayList<FileStructure> getFiles()
    {
        return files;
    }

    public String getFilepath()
    {
        return filepath;
    }

    public String getName()
    {
        return name;
    }

    public boolean isFile() {
        return isFile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(filepath);
        dest.writeString(name);
        boolean arr[] = {isFile};
        dest.writeBooleanArray(arr);
        dest.writeSerializable(files);
    }


    public static final Parcelable.Creator<FileStructure> CREATOR = new Parcelable.Creator<FileStructure>() {
        public FileStructure createFromParcel(Parcel in) {
            return new FileStructure(in);
        }

        public FileStructure[] newArray(int size) {
            return new FileStructure[size];
        }
    };

    private FileStructure(Parcel in)
    {
        String test = FileStructure.class.getCanonicalName();
        String temp = in.readString();
        if(test.equals(temp))
        {
            filepath = in.readString();
        }
        else
        {
            filepath = temp;
        }
        name = in.readString();
        boolean arr[] = {false};
        in.readBooleanArray(arr);
        isFile = arr[0];
        files = (ArrayList<FileStructure>) in.readSerializable();
    }

}
