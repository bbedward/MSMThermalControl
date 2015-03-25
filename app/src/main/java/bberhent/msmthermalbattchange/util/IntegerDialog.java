package bberhent.msmthermalbattchange.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import bberhent.msmthermalbattchange.R;

/**
 * IntegerDialog
 * A custom dialog that implements an EditText and a SeekBar for changing
 * integer values.
 *
 * @author Brandon Berhent
 */
public class IntegerDialog implements OnSeekBarChangeListener, TextWatcher,
        OnClickListener {
    /*
     * In ms, the intervals to delay increment when holding down the inc/dec.
     * button
     */
    private static final int INITIAL_INCREMENT_DELAY = 400;
    private static final int REPEATED_INCREMENT_DELAY = 100;

    private EditText mIntegerEditText;
    private TextView mIntegerDecrement;
    private TextView mIntegerIncrement;
    private SeekBar mIntegerSeekbar;

    private int mMinVal;
    private int mMaxVal;
    private int mIncrementStep;
    private int mDownArrowColor;

    private int mCurrentVal;

    private Context mContext;

    private View mIntegerDialogView;

    private AlertDialog.Builder mDialogBuilder;

    public IntegerDialog(Context c, int maxVal, int minVal, int defaultVal,
                         int incrementStep, int downArrowColor) {
        this.mContext = c;
        this.mMinVal = minVal;
        this.mMaxVal = maxVal;
        this.mIncrementStep = incrementStep;

        this.mCurrentVal = defaultVal;
        this.mDownArrowColor = downArrowColor;
    }

    public AlertDialog getIntegerDialog(String title, boolean cancelable,
                                        DialogInterface.OnClickListener confirmListener) {
        createIntegerDialogView();

        this.mDialogBuilder = new AlertDialog.Builder(this.mContext);
        this.mDialogBuilder.setView(this.mIntegerDialogView);

        this.mDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        mDialogBuilder.setPositiveButton("OK", confirmListener);
        mDialogBuilder.setTitle(title);
        mDialogBuilder.setCancelable(cancelable);

        return mDialogBuilder.create();
    }

    public int getIntegerValue() {
        return mCurrentVal;
    }

    private void createIntegerDialogView() {
        this.mIntegerDialogView = LayoutInflater.from(this.mContext).inflate(
                R.layout.integer_dialog, null);

        this.mIntegerEditText = (EditText) this.mIntegerDialogView
                .findViewById(R.id.integer_edit);
        this.mIntegerSeekbar = (SeekBar) this.mIntegerDialogView
                .findViewById(R.id.integer_seekbar);
        this.mIntegerDecrement = (TextView) this.mIntegerDialogView
                .findViewById(R.id.integer_decrement);
        this.mIntegerIncrement = (TextView) this.mIntegerDialogView
                .findViewById(R.id.integer_increment);

        // Initialize increment/decrementers
        this.mIntegerDecrement.setText("-");
        this.mIntegerDecrement.setOnTouchListener(new RepeatListener(
                INITIAL_INCREMENT_DELAY, REPEATED_INCREMENT_DELAY, this));
        this.mIntegerIncrement.setText("+");
        this.mIntegerIncrement.setOnTouchListener(new RepeatListener(
                INITIAL_INCREMENT_DELAY, REPEATED_INCREMENT_DELAY, this));

        // Initialize EditText
        this.mIntegerEditText.setText(Integer.toString(this.mCurrentVal));
        this.mIntegerEditText.addTextChangedListener(this);

        // Initialize SeekBar
        this.mIntegerSeekbar.setMax(this.mMaxVal);
        this.mIntegerSeekbar.setProgress(this.mCurrentVal);

        this.mIntegerSeekbar.setOnSeekBarChangeListener(this);
    }

    /* SeekBar Listener */
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        // Only snap values if they are sliding the bar directly
        if (fromUser) {
			/*
			 * This will snap to the closest multiple of incrementStep, by
			 * dividing the progress by increment step to get a whole number
			 * then multiplying by increment step
			 */
            if (progress <= 0)
                progress = mMinVal;
            else
                progress = (progress / mIncrementStep) * mIncrementStep;

            mCurrentVal = progress;
            seekBar.setProgress(progress);
            mIntegerEditText.setText(Integer.toString(progress));
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    /**
     * TODO: set the actual EditText with the corrected value in this function
     * when the keyboard disappears. There is no sure implementation for this
     * yet in android, but there may be a better alternative.
     */

	/* EditText Listener */
    public void afterTextChanged(Editable e) {
        int newVal = mIncrementStep;
        try {
            newVal = Integer.parseInt(e.toString());
        } catch (Exception invalid) {
            newVal = mIncrementStep;
        }

        if (newVal > mMaxVal)
            newVal = mMaxVal;
        else if (newVal < mMinVal)
            newVal = mMinVal;
        else
            newVal = (newVal / mIncrementStep) * mIncrementStep;

        mIntegerSeekbar.setProgress(newVal);
        mCurrentVal = newVal;
    }

    public void beforeTextChanged(CharSequence text, int start, int count,
                                  int after) {
    }

    public void onTextChanged(CharSequence text, int start, int before,
                              int count) {
    }

    /* Inc/Dec. Listener */
    public void onClick(View v) {
        if (v.getId() == R.id.integer_decrement && mCurrentVal > mMinVal)
            mCurrentVal -= mIncrementStep;
        else if (v.getId() == R.id.integer_increment && mCurrentVal < mMaxVal)
            mCurrentVal += mIncrementStep;
        else
            return;

        mIntegerEditText.setText(Integer.toString(mCurrentVal));
        mIntegerSeekbar.setProgress(mCurrentVal);
    }

    /**
     * RepeatListener
     *
     * Based on code in:
     * http://stackoverflow.com/questions/4284224/android-hold-
     * button-to-repeat-action
     */
    private class RepeatListener implements OnTouchListener {

        private Handler handler = new Handler();

        private int initialInterval;
        private final int normalInterval;
        private final OnClickListener clickListener;

        private Runnable handlerRunnable = new Runnable() {
            public void run() {
                clickListener.onClick(downView);
                handler.postDelayed(this, normalInterval);
            }
        };

        private View downView;
        private TextView tv;
        private int origTextColor;

        /**
         * @param initialInterval
         *            The interval after first click event
         * @param normalInterval
         *            The interval after second and subsequent click events
         * @param clickListener
         *            The OnClickListener, that will be called periodically
         */
        public RepeatListener(int initialInterval, int normalInterval,
                              OnClickListener clickListener) {
            if (clickListener == null)
                throw new IllegalArgumentException("null runnable");
            if (initialInterval < 0 || normalInterval < 0)
                throw new IllegalArgumentException("negative interval");

            this.initialInterval = initialInterval;
            this.normalInterval = normalInterval;
            this.clickListener = clickListener;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                handler.removeCallbacks(handlerRunnable);
                clickListener.onClick(view);
                handler.postDelayed(handlerRunnable, initialInterval);
                downView = view;
                if (view.getId() == R.id.integer_decrement)
                    tv = mIntegerDecrement;
                else
                    tv = mIntegerIncrement;
                origTextColor = tv.getCurrentTextColor();
                tv.setTextColor(mDownArrowColor);
                return true;
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                handler.removeCallbacks(handlerRunnable);
                tv.setTextColor(origTextColor);
                downView = null;
            }
            return false;
        }

    }
}