package comm.hyperonline.techsh.fcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class AppIndexingUpdateReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null
        ) {
            // Schedule the job to be run in the background.
            AppIndexingUpdateService.enqueueWork(context);
        }
    }

}

