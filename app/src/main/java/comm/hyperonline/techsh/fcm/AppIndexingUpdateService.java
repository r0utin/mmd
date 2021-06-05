package comm.hyperonline.techsh.fcm;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

public class AppIndexingUpdateService extends JobIntentService {

    // Job-ID must be unique across your whole app.
    private static final int UNIQUE_JOB_ID = 42;

    public static void enqueueWork(Context context) {
        enqueueWork(context, AppIndexingUpdateService.class, UNIQUE_JOB_ID, new Intent());
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        // TODO Insert your Indexable objects — for example, the recipe notes look as follows:


//        for (Home.AllCategory recipe : getAllRecipes()) {
//            Note note = recipe.getNote();
//            if (note != null) {
//
//            }
//        }

    }

    // ...
}
