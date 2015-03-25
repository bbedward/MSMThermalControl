package bberhent.msmthermalbattchange.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * CurrentSettingsUtil
 * Singleton Helper w/ HashMap for retaining current app settings
 *
 * I have a much more flexible version of this, but this app is simple
 *
 * @author Brandon Berhent
 */
public enum CurrentSettingsUtil {
    INSTANCE;

    private static final String TAG = "CurrentSettingsUtility";

    private Map<String, String> currentSettings = new ConcurrentHashMap<String, String>();

    public static final String KEY_CPU_FREQ_BAT = "CPU_FREQ_BATT";
    public static final String KEY_CPU_HOTPLUG_BAT = "CPU_HOTPLUG_BATT";
    public static final String KEY_GPU_FREQ_BAT = "GPU_FREQ_BATT";


    /**
     * addNewSetting - Add file's value to the Map.
     *
     * @param key
     *            : unique identifier for the setting
     * @param setting
     *            : value to set to the key
     */
    public void addNewSetting(String key, String setting) {
        currentSettings.put(key, setting);
    }

    public String getCurrentSetting(String key) {
        if (key == null)
            return null;
        return currentSettings.get(key);
    }

    public CopyOnWriteArrayList<String> getAllCurrentSettings() {
        return new CopyOnWriteArrayList<String>(currentSettings.values());
    }

    public void clearAllCurrentSettings() {
        currentSettings.clear();
    }
}