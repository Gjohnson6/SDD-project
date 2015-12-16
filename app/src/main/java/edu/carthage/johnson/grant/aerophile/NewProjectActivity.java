package edu.carthage.johnson.grant.aerophile;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;


public class NewProjectActivity extends ActionBarActivity {
    TextView filePathTV;
    TextView projectNameTV;

    SavedProjects savedProjects;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project);
        filePathTV = (TextView) this.findViewById(R.id.projectPathText);
        projectNameTV = (TextView) this.findViewById(R.id.projectNameTextView);
        if(getIntent().hasExtra("BasePath"))
        {
            filePathTV.setText(getIntent().getStringExtra("BasePath"));
        }

        if(getIntent().hasExtra("ProjectName"));
        {
            projectNameTV.setText(getIntent().getStringExtra("ProjectName"));
        }

        if(getIntent().hasExtra("SavedProjects"))
        {
            savedProjects = (SavedProjects) getIntent().getParcelableExtra("SavedProjects");
        }
        else
        {
            savedProjects = new SavedProjects();
        }
        String b = "d";

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_project, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void Browse(View view)
    {
        //opens file browser
        Intent moveToBrowser = new Intent(this, NewProjectBrowser.class);
        String projectName = projectNameTV.getText().toString();
        moveToBrowser.putExtra("ProjectName", projectNameTV.getText().toString());
        moveToBrowser.putExtra("SavedProjects", (Parcelable) savedProjects);
        startActivity(moveToBrowser);
        //does stuff
    }

    public void CreateProject(View view)
    {
        //create project
        String projectName = projectNameTV.getText().toString();
        String filepath = filePathTV.getText().toString();
        if(projectName == null || projectName.equals(""))
        {
            Toast toast = Toast.makeText(this, "Enter in a project name", Toast.LENGTH_SHORT);
            toast.setText("Enter in a project name");
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
        else if (filepath == null || filepath.equals(""))
        {
            Toast toast = Toast.makeText(this, "Enter in a file path", Toast.LENGTH_SHORT);
            toast.setText("Enter in a file path");
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
        else
        {
            Project newProject = new Project(projectName, filepath);

            //Generate ID
            newProject.setId(UUID.randomUUID().toString());
            savedProjects.addProject(newProject);
            savedProjects.saveProjects(this, "HostedProjects.ser");

            Intent moveToHost = new Intent(this, HostActivity.class);
            moveToHost.putExtra("SavedProjects", (Parcelable) savedProjects);

            startActivity(moveToHost);
        }
    }
}
