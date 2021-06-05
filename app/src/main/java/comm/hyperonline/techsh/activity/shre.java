package comm.hyperonline.techsh.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.StrictMode;

import comm.hyperonline.techsh.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class shre
{



    public static boolean share(Context context)
    {
        try
        {
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build());



            String pn = context.getPackageName();
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(pn, 0);
            ApplicationInfo ai = pm.getApplicationInfo(pn, 0);


            String appName = context.getString(R.string.app_name);
            String appVersion= pi.versionName;
            String sendTitle = "اشتراک گذاری برنامه : "+appName+"_"+appVersion;
            String apkTitle = appName+"_"+appVersion;


            File apkFile = new File(ai.publicSourceDir);
            File apkFileNew = new File(context.getExternalCacheDir()+"/"+apkTitle+".apk");


            fileCreate(apkFileNew);
            fileCopy(apkFile, apkFileNew);
            fileSend(context, apkFileNew, sendTitle);


            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }




    private static boolean fileSend(Context context, File file, String title)
    {
        try
        {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("application/*");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            context.startActivity(Intent.createChooser(intent, title));
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
    private static boolean fileCreate(File file)
    {
        try
        {
            if (!file.exists())
            {
                if (!file.createNewFile())
                {
                    return false;
                }
            }
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
    private static boolean fileCopy(File fileIn, File fileOu)
    {
        try
        {

            InputStream in = new FileInputStream(fileIn);
            OutputStream ou = new FileOutputStream(fileOu);

            byte[] buffer = new byte[1024];
            int le;
            while ((le = in.read(buffer)) > 0)
            {
                ou.write(buffer, 0, le);
            }

            in.close();
            ou.close();

            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }



}
