package com.example.tasksapp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextParams;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.tasksapp.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    Button backButton;
    ImageButton addButton;
    Button createButton;
    EditText newTask;
    ListView taskList;
    CustomAdapter adapter;
    CheckBox showNotification;
    SharedPreferences sharedPreferences;
    public static ArrayList<String> tasks;
    Set<String> taskSet;
    SharedPreferences.Editor editor;
    Button refreshButton;
    static final int notificationsRequestCode = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        notificationsRequestCode);
            }
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskList = findViewById(R.id.tasksList);
        addButton = findViewById(R.id.addButton);
        tasks = new ArrayList<>();





        adapter = new CustomAdapter(this, tasks);
        taskList.setAdapter(adapter);
        sharedPreferences = getSharedPreferences("TasksAppPrefs", MODE_PRIVATE);
        taskSet = sharedPreferences.getStringSet("task_list", new HashSet<>());
        tasks.clear();
        tasks.addAll(taskSet);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.addnewtaskscreen);
                closeButtonHandler();
                newTaskHandler();
            }
        });
    }

    public void closeButtonHandler(){
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setContentView(R.layout.activity_main);
                addButton = findViewById(R.id.addButton);
                taskList = findViewById(R.id.tasksList);

                adapter = new CustomAdapter(MainActivity.this, tasks);
                taskList.setAdapter(adapter);

                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setContentView(R.layout.addnewtaskscreen);
                        closeButtonHandler();
                        newTaskHandler();
                    }
                });
            }
        });
    }

    public void newTaskHandler() {
        createButton = findViewById(R.id.createButton);
        newTask = findViewById(R.id.newTaskTextBar);
        showNotification = findViewById(R.id.makeTheTaskAnnoying);

        sharedPreferences = getSharedPreferences("TasksAppPrefs", MODE_PRIVATE);

        editor = sharedPreferences.edit();
        editor.putStringSet("task_list", new HashSet<>(tasks));
        editor.apply();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.POST_NOTIFICATIONS},
                            notificationsRequestCode);
                }
            }




        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String taskText = newTask.getText().toString();
                if (!taskText.isEmpty()) {
                    tasks.add(taskText);
                    editor.putStringSet("task_list", new HashSet<>(tasks));
                    editor.apply();
                    if(showNotification.isChecked()){
                        createNotificationChannel();
                        sendUndestroyableNotification(taskText);
                    }
                }


                setContentView(R.layout.activity_main);
                taskList = findViewById(R.id.tasksList);
                addButton = findViewById(R.id.addButton);



                adapter = new CustomAdapter(MainActivity.this, tasks);
                taskList.setAdapter(adapter);

                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setContentView(R.layout.addnewtaskscreen);
                        closeButtonHandler();
                        newTaskHandler();
                    }
                });
            }
        });
    }


    public class CustomAdapter extends ArrayAdapter<String> {

        public CustomAdapter(Context context, ArrayList<String> tasks) {
            super(context, 0, tasks);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            }
            String item = getItem(position);

            TextView textView = convertView.findViewById(R.id.text_view);
            CheckBox checkBox = convertView.findViewById(R.id.check_box);

            textView.setText(item);

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tasks.remove(position);
                    editor = getContext().getSharedPreferences("TasksAppPrefs", MODE_PRIVATE).edit();
                    editor.putStringSet("task_list", new HashSet<>(tasks));
                    editor.apply();
                    setContentView(R.layout.activity_main);
                    addButton = findViewById(R.id.addButton);
                    taskList = findViewById(R.id.tasksList);

                    adapter = new CustomAdapter(MainActivity.this, tasks);
                    taskList.setAdapter(adapter);

                    addButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setContentView(R.layout.addnewtaskscreen);
                            closeButtonHandler();
                            newTaskHandler();
                        }
                    });
                }
            });

            return convertView;
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Persistent Channel";
            String description = "Channel for persistent notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("PERSISTENT_CHANNEL_ID", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private void sendUndestroyableNotification(String task) {

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "PERSISTENT_CHANNEL_ID")
                .setSmallIcon(R.drawable.baseline_add_24)
                .setContentTitle("DO IT NOW!!!!!")
                .setContentText(task)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true)
                .setAutoCancel(false)
                .setContentIntent(pendingIntent);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(1, builder.build());
    }


}
