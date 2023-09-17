package com.creativesaif.expert_internet_admin.TaskList;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.creativesaif.expert_internet_admin.ClientList.ExpiredClient;
import com.creativesaif.expert_internet_admin.ClientList.RegisteredClient;
import com.creativesaif.expert_internet_admin.ClientList.TabAdapter;
import com.creativesaif.expert_internet_admin.ClientList.UnRegisteredClient;
import com.creativesaif.expert_internet_admin.Employees.Employee;
import com.creativesaif.expert_internet_admin.Employees.Employee_add;
import com.creativesaif.expert_internet_admin.R;

public class TaskList extends AppCompatActivity {

    private com.creativesaif.expert_internet_admin.ClientList.TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new PendingTask(), "Pending");
        adapter.addFragment(new CompletedTask(), "Completed");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.task_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (item.getItemId() == R.id.action_task_add) {

            startActivity(new Intent(TaskList.this, Task_Add.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
