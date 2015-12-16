package edu.carthage.johnson.grant.aerophile;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;


public class HostActivity extends ActionBarActivity {
    SavedProjects savedProjects;
    private Context ctx = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        ArrayList<Project> projects = new ArrayList<>();
        savedProjects = new SavedProjects();

        if(getIntent().hasExtra("SavedProjects"))
        {
            savedProjects = (SavedProjects) getIntent().getParcelableExtra("SavedProjects");
        }
        else
        {
            savedProjects = savedProjects.readProjects(this, "HostedProjects.ser");
        }
        projects = savedProjects.getProjects();

//        Project project = new Project("New Project", "/");
  //      project.setPartnerCount(5);
    //    project.setProjectName("New Project");
      //  projects.add(project);
       // Project project2 = new Project("Frog on a bicycle", Environment.getExternalStorageDirectory().toString());
//        project2.setPartnerCount(3);
  //      project2.setProjectName("Frog on a bicycle");
    //    projects.add(project2);

        //Read saved projects
      //  savedProjects.addProject(project);
       // savedProjects.addProject(project2);


        CustomProjectAdapter adapter = new CustomProjectAdapter(getApplicationContext(), projects, ctx );
        ListView listView = (ListView) findViewById(R.id.ProjectListView);
        listView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_host, menu);
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

    public void CreateNewProject(View view)
    {
        Intent moveToNewProj = new Intent(this, NewProjectActivity.class);
        if(savedProjects != null)
        {
            moveToNewProj.putExtra("SavedProjects", (Parcelable) savedProjects);
        }
        startActivity(moveToNewProj);
    }
}
