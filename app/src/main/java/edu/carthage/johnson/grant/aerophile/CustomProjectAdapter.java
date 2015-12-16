package edu.carthage.johnson.grant.aerophile;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class CustomProjectAdapter extends ArrayAdapter<Project> {

    private Context mContext;
    private String UserId;
    public CustomProjectAdapter(Context context, ArrayList<Project> projects, Context ctx){
        super(context, 0, projects);
        this.mContext = ctx;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final Project project = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_custom_project_listview, parent, false);
        }


        TextView projectName = (TextView) convertView.findViewById(R.id.projectNameTextView);
        TextView partnerCount = (TextView) convertView.findViewById(R.id.partnerCountTextView);

        projectName.setText(project.getProjectName());
        partnerCount.setText(Integer.toString(project.getPartnerCount()) + " Partner" + (project.getPartnerCount() == 1 ? "" : "s"));
        convertView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getContext(),ProjectHost.class);
                intent.putExtra("Project", (Parcelable) project);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

}
