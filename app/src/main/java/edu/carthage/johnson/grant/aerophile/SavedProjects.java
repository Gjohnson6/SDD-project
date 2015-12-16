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
public class SavedProjects implements Serializable, Parcelable {
    ArrayList<Project> projects;

    public SavedProjects()
    {
        projects = new ArrayList<>();
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

    public static SavedProjects readProjects(Context ctx, String filename)
    {
        SavedProjects savedProjects = null;

        try
        {
            FileInputStream fileInputStream = ctx.openFileInput(filename);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            savedProjects = (SavedProjects) objectInputStream.readObject();
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
            savedProjects = new SavedProjects();
        }
        return savedProjects;
    }

    public ArrayList<Project> getProjects()
    {
        return projects;
    }

    public void addProject(Project project)
    {
        projects.add(project);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(projects);
    }

    public static final Parcelable.Creator<SavedProjects> CREATOR = new Parcelable.Creator<SavedProjects>() {
        public SavedProjects createFromParcel(Parcel in) {
            return new SavedProjects(in);
        }

        public SavedProjects[] newArray(int size) {
            return new SavedProjects[size];
        }
    };

    private SavedProjects(Parcel in)
    {
        projects = new ArrayList<>();
        in.readTypedList(projects, Project.CREATOR);
    }

}
