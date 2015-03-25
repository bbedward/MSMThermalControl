package bberhent.msmthermalbattchange.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import bberhent.msmthermalbattchange.MSMThermal;
import bberhent.msmthermalbattchange.db.MSMThermalObj;

/**
 * MSMThermalParser
 *
 * Parse an msm thermal configuration file and return MSMThermal objects
 */
public class MSMThermalParser {
    private static final String BAT_SOC_CPUFREQ_HEAD = "[BAT-SOC-CPUFREQ]";
    private static final String BAT_SOC_HOTPLUG_HEAD = "[BAT-SOC-HOTPLUG]";
    private static final String BAT_SOC_GPUFREQ_HEAD = "[BAT-SOC-GPU]";

    public void putMSMThermalRawInSettingsMap(File fileToRead, String delimiter) {
        CurrentSettingsUtil settingsRaw = CurrentSettingsUtil.INSTANCE;
        FileInputStream fis = null;
        BufferedReader br = null;

        if (!fileToRead.exists() || !fileToRead.canRead())
            return;

        try {
            fis = new FileInputStream(fileToRead);
            br = new BufferedReader(new InputStreamReader(fis));

            String nextLine;
            String relevantLine = null;

            String current_location = null;
            boolean beginRelvantLine = false;
            int count = 0;

            while ((nextLine = br.readLine()) != null) {
                // TODO We are expecting 3 things. Very inflexible if this isn't the case
                if (count == 3) break;

                if (nextLine.contains(BAT_SOC_CPUFREQ_HEAD)) {
                    current_location = BAT_SOC_CPUFREQ_HEAD;
                    beginRelvantLine = true;
                }

                if (beginRelvantLine) {
                    // TODO This is a dirty parser we are assuming these all come up in the same order. If they don't errors will happen.
                    if (nextLine.contains("thresholds")) {
                        relevantLine = nextLine + delimiter;
                    } else if (nextLine.contains("thresholds_clr")) {
                        relevantLine += nextLine + delimiter;
                    } else if (nextLine.contains("actions")) {
                        relevantLine += nextLine + delimiter;
                    } else if (nextLine.contains("action_info")) {
                        relevantLine += nextLine;
                        if (current_location == BAT_SOC_CPUFREQ_HEAD)
                            settingsRaw.addNewSetting(CurrentSettingsUtil.KEY_CPU_FREQ_BAT, relevantLine);
                        else if (current_location == BAT_SOC_HOTPLUG_HEAD)
                            settingsRaw.addNewSetting(CurrentSettingsUtil.KEY_CPU_HOTPLUG_BAT, relevantLine);
                        else if (current_location == BAT_SOC_GPUFREQ_HEAD)
                            settingsRaw.addNewSetting(CurrentSettingsUtil.KEY_GPU_FREQ_BAT, relevantLine);
                        beginRelvantLine = false;
                        count++;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<MSMThermalObj> getMSMThermalList() {
        String CPUFreqRaw = CurrentSettingsUtil.INSTANCE.getCurrentSetting(CurrentSettingsUtil.KEY_CPU_FREQ_BAT);
        String CPUHotplugRaw = CurrentSettingsUtil.INSTANCE.getCurrentSetting(CurrentSettingsUtil.KEY_CPU_HOTPLUG_BAT);
        String GPUFreqRaw = CurrentSettingsUtil.INSTANCE.getCurrentSetting(CurrentSettingsUtil.KEY_GPU_FREQ_BAT);

        ArrayList<MSMThermalObj> retVal = new ArrayList<MSMThermalObj>();

        // This is giving us an array of each particular CPUFreq thing (action, threshold, etc)
        String[] CPUFreqArray = CPUFreqRaw.split(";");
        MSMThermalObj CPUObj = new MSMThermalObj();
        CPUObj.setType(MSMThermalObj.ThermalType.CPUBattFrequency);

        for (String cpu : CPUFreqArray) {
            // This breaks everything down to specific arrays for thresholds, etc.
            String[] setting = cpu.split("\\s+|\\t+");
            if (setting[0].equals("thresholds")) {
                ArrayList<Integer> thresholdList = new ArrayList<Integer>();
                for (int i=1; i < setting.length; i++) {
                    thresholdList.add(Integer.parseInt(setting[i]));
                }
                CPUObj.setThresholds(thresholdList);
            } else if (setting[0].equals("thresholds_clr")) {
                ArrayList<Integer> thresholdListClr = new ArrayList<Integer>();
                for (int i=1; i < setting.length; i++) {
                    thresholdListClr.add(Integer.parseInt(setting[i]));
                }
                CPUObj.setThresholdsClr(thresholdListClr);
            } else if (setting[0].equals("actions")) {
                // TODO For hotplugging this will need to be an array of actions
                CPUObj.setAction(setting[1]);
            } else if (setting[0].equals("action_info")) {
                ArrayList<Integer> actionInfoList = new ArrayList<Integer>();
                for (int i=1; i < setting.length; i++) {
                    actionInfoList.add(Integer.parseInt(setting[i]));
                }
                CPUObj.setActionParams(actionInfoList);
            }
        }
        retVal.add(CPUObj);

        // TODO Add Hotplug, GPU, CPU

        return retVal;
    }
}
