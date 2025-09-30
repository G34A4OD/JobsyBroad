package com.example.jobsybroad;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MainActivity extends AppCompatActivity {
    private static final int JOB_ID = 1;
    private TextView textBroadcastDinamico;

    private BroadcastReceiver airplaneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isAirplaneModeOn = intent.getBooleanExtra("state", false);
            String mensaje;
            if(isAirplaneModeOn){
                mensaje = "Modo Avión Activado";
            } else {
                mensaje = "Modo Avión Desactivado";
            }

            // Actualizar el TextView directamente
            textBroadcastDinamico.setText(mensaje);

            JobInfo jobInfo = getJobInfo(MainActivity.this);
            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            if (scheduler != null){
                scheduler.cancel(JOB_ID);

                int result = scheduler.schedule(jobInfo);
                if (result == JobScheduler.RESULT_SUCCESS){
                    Toast.makeText(context, "Job programado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Error al programar el job", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    // BroadcastReceiver para recibir actualizaciones del JobService
    private BroadcastReceiver jobUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("JOB_UPDATE".equals(intent.getAction())) {
                String message = intent.getStringExtra("message");
                textBroadcastDinamico.setText(message);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar el TextView
        textBroadcastDinamico = findViewById(R.id.textBroadcastDinamico);
    }

    private static JobInfo getJobInfo(Context context) {
        ComponentName componentName = new ComponentName(context, JobNotificacion.class);
        return new JobInfo.Builder(JOB_ID, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setMinimumLatency(2000)
                .setOverrideDeadline(5000)
                .setPersisted(false)
                .build();
    }

    @Override
    protected void onStart(){
        super.onStart();
        // Registrar receiver para modo avión
        IntentFilter filter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        registerReceiver(airplaneReceiver, filter);

        // Registrar receiver para actualizaciones del JobService
        IntentFilter jobFilter = new IntentFilter("JOB_UPDATE");
        LocalBroadcastManager.getInstance(this).registerReceiver(jobUpdateReceiver, jobFilter);
    }

    @Override
    protected void onStop(){
        super.onStop();
        unregisterReceiver(airplaneReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(jobUpdateReceiver);
    }
}