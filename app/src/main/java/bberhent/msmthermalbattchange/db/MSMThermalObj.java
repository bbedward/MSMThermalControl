package bberhent.msmthermalbattchange.db;

import java.util.ArrayList;

/**
 * MSMThermal
 *
 * Simple object for storing msm_thermal battery settings
 */
public class MSMThermalObj {
    public static enum ThermalType {
        CPUBattFrequency,
        CPUBattHotplug,
        GPUBattFrequency
    }

    private ThermalType type;
    private String action;
    private ArrayList<Integer> thresholds;
    private ArrayList<Integer> thresholds_clr;
    private ArrayList<Integer> actionParams;

    public MSMThermalObj() {}

    public ThermalType getType() {
        return type;
    }

    public void setType(ThermalType type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public ArrayList<Integer> getThresholds() {
        return this.thresholds;
    }

    public void setThresholds(ArrayList<Integer> thresholds) {
        this.thresholds = thresholds;
    }

    public ArrayList<Integer> getThresholdsClr() {
        return this.thresholds_clr;
    }

    public void setThresholdsClr(ArrayList<Integer> thresholdsClr) {
        this.thresholds_clr = thresholdsClr;
    }

    public ArrayList<Integer> getActionParams() {
        return this.actionParams;
    }

    public void setActionParams(ArrayList<Integer> actionParams) {
        this.actionParams = actionParams;
    }
}
