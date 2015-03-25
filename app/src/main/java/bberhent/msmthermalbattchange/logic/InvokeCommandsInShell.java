package bberhent.msmthermalbattchange.logic;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;

public class InvokeCommandsInShell extends AsyncTask<String, Void, Void> {
    private static final String TAG = InvokeCommandsInShell.class.getSimpleName();

    private Context mContext;

    public InvokeCommandsInShell(Context context) {
        this.mContext = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        Process p;
        DataOutputStream os = null;
        try {
            p = Runtime.getRuntime().exec("su");

            os = new DataOutputStream(p.getOutputStream());

            for (String commandToInvoke : params) {
                os.writeBytes(commandToInvoke + "\n");
                os.flush();
            }
            os.writeBytes("exit\n");
            os.flush();

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            try {
                if (os != null) os.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        return null;
    }

    public Context getContext() {
        return this.mContext;
    }
}