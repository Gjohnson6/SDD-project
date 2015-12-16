package edu.carthage.johnson.grant.aerophile;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by Grant on 12/16/2015.
 */
public class FileStructureAdapter extends ArrayAdapter<String>{
    Context mContext;
    FileStructure fileStructure;

    public FileStructureAdapter(Context context, ArrayList<String> files, FileStructure fs){
        super(context, 0, files);
        this.mContext = context;
        this.fileStructure = fs;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
       // final File file = new File(getItem(position));
        FileStructure fileStructure1 = null;
        for(FileStructure file : fileStructure.getFiles())
        {
            if(file.getName() == getItem(position))
            {
                fileStructure1 = file;
            }
        }

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_custom_file_listview, parent, false);
        }

        TextView projectName = (TextView) convertView.findViewById(R.id.fileNameTextView);
        TextView fileSize = (TextView) convertView.findViewById(R.id.fileSizeTextView);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.fileTypeImageView);
        if(fileStructure1.isFile())
        {
            imageView.setImageResource(R.drawable.ic_description);
        }
        else
        {
            imageView.setImageResource(R.drawable.ic_action_folder_closed);
            fileSize.setText("");
        }

        projectName.setText(fileStructure1.getName());

        return convertView;
    }
}