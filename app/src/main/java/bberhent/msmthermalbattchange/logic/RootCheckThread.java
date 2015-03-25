package bberhent.msmthermalbattchange.logic;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * RootCheckThread
 *
 * Creates a root shell and determines if the user's UID is 0. If it is, we were
 * able to obtain root. Otherwise, we were unable to confirm we can obtain root.
 *
 * @author Brandon Berhent
 */
public class RootCheckThread extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = RootCheckThread.class.getSimpleName();

    @Override
    protected Boolean doInBackground(Void... params) {
		/*
		 * Check root access.
		 */
        Process p;
        DataOutputStream os = null;
        BufferedReader br = null;
        boolean hasRootResult = false;

        try {
            p = Runtime.getRuntime().exec("su");
            String uuid = null;

            os = new DataOutputStream(p.getOutputStream());
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));

			/* Get UID from shell */
            os.writeBytes("id\n");
            os.flush();

			/* Read result of id command */
            uuid = br.readLine();
            os.writeBytes("exit\n");
            os.flush();

            try {
                p.waitFor();
                if (uuid != null && uuid.contains("uid=0"))
                    hasRootResult = true;
            } catch (InterruptedException e) {
                hasRootResult = false;
            }
        } catch (IOException e) {
            hasRootResult = false;
            Log.e(TAG, e.getMessage());
        } finally {
            try {
                if (br != null) br.close();
                if (os != null) os.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return hasRootResult;
    }
}