package edu.carthage.johnson.grant.aerophile;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Grant on 12/13/2015.
 */
public class SavedPartnerProjects implements Serializable, Parcelable {
    ArrayList<PartnerProject> partnerProjects;

    public SavedPartnerProjects()
    {
        partnerProjects = new ArrayList<>();
    }

    public void saveProjects(Context ctx, String filename)
    {
        try
        {
            FileOutputStream fileOutputStream = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(this);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SavedPartnerProjects readProjects(Context ctx, String filename)
    {
        SavedPartnerProjects savedProjects = null;

        try
        {
            FileInputStream fileInputStream = ctx.openFileInput(filename);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            savedProjects = (SavedPartnerProjects) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();

        } catch (IOException e)
        {
            e.printStackTrace();
        } catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        if(savedProjects == null)
        {
            savedProjects = new SavedPartnerProjects();
        }
        return savedProjects;
    }

    public ArrayList<PartnerProject> getProjects()
    {
        return partnerProjects;
    }

    public void addProject(PartnerProject project)
    {
        partnerProjects.add(project);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(partnerProjects);
    }

    public static final Parcelable.Creator<SavedPartnerProjects> CREATOR = new Parcelable.Creator<SavedPartnerProjects>() {
        public SavedPartnerProjects createFromParcel(Parcel in) {
            return new SavedPartnerProjects(in);
        }

        public SavedPartnerProjects[] newArray(int size) {
            return new SavedPartnerProjects[size];
        }
    };

    private SavedPartnerProjects(Parcel in)
    {
        partnerProjects = new ArrayList<>();
        in.readTypedList(partnerProjects, PartnerProject.CREATOR);
    }
}
