package bberhent.msmthermalbattchange;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import java.io.File;

import bberhent.msmthermalbattchange.logic.InvokeCommandsInShell;
import bberhent.msmthermalbattchange.logic.RootCheckThread;
import bberhent.msmthermalbattchange.util.MSMThermalParser;


public class LoadingActivity extends Activity {
    private static final String TAG = LoadingActivity.class.getSimpleName();

    public static final String workingDir = "/msm_thermal_workdir";
    public static final String msmThermalConfig = "/system/etc/thermal-engine-shamu.conf";
    public static final String msmThermalBackupName = "thermal-engine-shamu-backup.conf";
    public static final String msmThermalWorkName = "thermal_work.conf";

    private boolean mUserHasRoot = false;

    private TextView mLoadingText;

    private Context mContext;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_loading);

        mLoadingText = (TextView) findViewById(R.id.loading_text);
        this.mContext = this;
        this.mActivity = this;

        setProgressBarIndeterminateVisibility(true);

        // Start the RootCheckThread, which should chain to the next one in onPostExecute()
        startRootCheckThread();
    }

    private void startRootCheckThread() {
        mLoadingText.setText("Checking Root Access");

        new RootCheckThread() {
            @Override
            public void onPostExecute(Boolean result) {
                mUserHasRoot = result;

                if (!mUserHasRoot) {
                    Log.i(TAG, "Device does not have root access");
                    if (!mUserHasRoot) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                        builder.setCancelable(false);
                        builder.setNeutralButton("Exit", new AlertDialog.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        builder.setTitle("No Root");
                        builder.setMessage("This application requires superuser access to function.");
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        copyMsmThermalConfigToWorkingDirectory();
                    }
                }
            }
        }.execute();
    }

    private void copyMsmThermalConfigToWorkingDirectory() {
        // Should we make a backup or do we already have one
        boolean backupNeeded = false;

        String workingDirPath = Environment.getExternalStorageState().toString() + workingDir;
        File w = new File(workingDir);
        w.mkdirs();
        final File workFile = new File(workingDirPath + "/" + msmThermalWorkName);
        File backupFile = new File(workingDirPath + "/" + msmThermalBackupName);

        if (!backupFile.exists()) {
            backupNeeded = true;
        }

        if (workFile.exists())
            workFile.delete();

        // Commands to send in root shell
        String[] commands;
        if (backupNeeded)
            commands = new String[] { "cp " + msmThermalConfig + " " + workingDirPath + "/" + msmThermalWorkName,
                    "cp " + msmThermalConfig + " " + workingDirPath + "/" + msmThermalBackupName };
        else
            commands = new String[] { "cp " + msmThermalConfig + " " + workingDirPath + "/" + msmThermalWorkName };

        // Make sure we copy the config to working directory as a
        new InvokeCommandsInShell(mContext) {
            public void onPostExecute(Void result) {
                if (!workFile.exists()) {
                    Log.e(TAG, "Failed to create work file");
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setCancelable(false);
                    builder.setNeutralButton("Exit", new AlertDialog.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    builder.setTitle("Fatal Error");
                    builder.setMessage("Could not copy msm_thermal config to working directory.");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    MSMThermalParser msmTP = new MSMThermalParser();
                    msmTP.putMSMThermalRawInSettingsMap(workFile, ";");
                    startMainActivity();
                }
            }
        }.execute(commands);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_loading, menu);
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

    private void startMainActivity() {
        // Initiate the tabs/pager and close this activity
        Intent i = new Intent(LoadingActivity.this, MSMThermal.class);
        startActivity(i);

        this.finish();
    }
}
