package edu.carthage.johnson.grant.aerophile;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Grant on 12/15/2015.
 */
public class FileStructure {
    private String filepath;
    private String name;
    private ArrayList<FileStructure> files;

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
}
