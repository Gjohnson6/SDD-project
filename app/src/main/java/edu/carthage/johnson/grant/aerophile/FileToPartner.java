package edu.carthage.johnson.grant.aerophile;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Grant on 12/16/2015.
 */
public class FileToPartner implements Serializable {
    private byte[] bytes;
    private File file;

    public FileToPartner(byte[] bytes, File file)
    {
        this.bytes = bytes;
        this.file = file;
    }

    public byte[] getBytes()
    {
        return bytes;
    }

    public File getFile()
    {
        return file;
    }
}
