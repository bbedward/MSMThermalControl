package bberhent.msmthermalbattchange;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;


public class MSMThermal extends ActionBarActivity {
    private Context mContext;

    // Toolbar designed to replace ActionBar
    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msmthermal);

        /*
         * Set Toolbar as Actionbar
         */
        mToolBar = (Toolbar)findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolBar);

        // Set title
        getActionBar().setTitle(getResources().getString(R.string.title_activity_msmthermal_main));


        LinearLayout mCPUFreqContainer = (LinearLayout)findViewById(R.id.cpu_freq_container);
        // Create ID
        LinearLayout cpuFreqIntegerDialog = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.item_row_integerdialog, null);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_msmthermal, menu);
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
}
