package com.example.jobsybroad;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JobNotificacion extends JobService {

    private ExecutorService executorService;

    @Override
    public boolean onStartJob(JobParameters params) {
        if (executorService == null) {
            executorService = Executors.newSingleThreadExecutor();
        }

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    Thread.sleep(3000);
                    sendUpdateToActivity("Job ejecutado exitosamente");

                } catch (InterruptedException e) {
                    Log.e("JobNotificacionStatus", "Hilo interrumpido, hubo error", e);
                    sendUpdateToActivity("Error en la ejecución del Job");
                } finally {
                    jobFinished(params, false);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (executorService != null) {
            executorService.shutdownNow();
        }
        return true;
    }

    private void sendUpdateToActivity(String message) {
        Intent intent = new Intent("JOB_UPDATE");
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}