package com.android.settings.preferences.ui

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemProperties
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.ImageView
import androidx.preference.Preference
import androidx.preference.PreferenceScreen
import com.android.settings.R
import com.android.settingslib.core.AbstractPreferenceController
import com.android.settingslib.DeviceInfoUtils
import com.android.settingslib.widget.LayoutPreference
import com.android.settings.utils.SpecUtils

class OrionInfoPreferenceController(context: Context) : AbstractPreferenceController(context) {

    private val defaultFallback = mContext.getString(R.string.device_info_default)

    private fun getProp(propName: String): String {
        return SystemProperties.get(propName, defaultFallback)
    }

    private fun getProp(propName: String, customFallback: String): String {
        val propValue = SystemProperties.get(propName)
        return if (propValue.isNotEmpty()) propValue else SystemProperties.get(customFallback, "Unknown")
    }

    private fun getDeviceName(): String {
        return "${Build.MANUFACTURER} ${Build.MODEL}"
    }

    private fun getOrionChipset(): String {
        return getProp(PROP_ORION_CHIPSET)
    }

    private fun getOrionBuildVersion(): String {
        return getProp(PROP_ORION_BUILD_VERSION)
    }

    private fun getOrionVersion(): String {
        return getProp(PROP_ORION_VERSION, defaultFallback)
    }

    private fun getOrionReleaseType(): String {
        val releaseType = getProp(PROP_ORION_RELEASETYPE)
        return releaseType.substring(0, 1).uppercase() +
               releaseType.substring(1).lowercase()
    }

    private fun getOrionBuildStatus(releaseType: String): String {
        return mContext.getString(if (releaseType == "OFFICIAL") R.string.build_offi_title else R.string.build_unoff_title)
    }

    private fun getOrionMaintainer(releaseType: String): String {
        val orionMaintainer = getProp(PROP_ORION_MAINTAINER)
        if (orionMaintainer.equals("Unknown", ignoreCase = true)) {
            return mContext.getString(R.string.unknown_maintainer)
        }
        return mContext.getString(R.string.maintainer_summary, orionMaintainer)
    }

    override fun displayPreference(screen: PreferenceScreen) {
        super.displayPreference(screen)

        val releaseType = getProp(PROP_ORION_RELEASETYPE).uppercase()
        val orionMaintainer = getOrionMaintainer(releaseType)
        val isOfficial = releaseType == "OFFICIAL"

        val hwInfoPreference = screen.findPreference<LayoutPreference>(KEY_HW_INFO)!!
        val statusPreference = screen.findPreference<LayoutPreference>(KEY_BUILD_STATUS)!!
        
	val setTitle  = statusPreference.findViewById<TextView>(R.id.setTitle)
	val setIcon  = statusPreference.findViewById<ImageView>(R.id.setIcon)
        val setMaintainer  = statusPreference.findViewById<TextView>(R.id.setMaintainer)

        setTitle.text = getOrionBuildStatus(releaseType)
        setMaintainer.text = orionMaintainer
        setIcon.setImageResource(if (isOfficial) R.drawable.checkmark else R.drawable.crossmark)

        hwInfoPreference.apply {
            findViewById<TextView>(R.id.device_chipset)?.text = getOrionChipset()
            findViewById<TextView>(R.id.device_battery_capacity)?.text = SpecUtils.getBatteryCapacity(mContext)
            findViewById<TextView>(R.id.device_resolution)?.text = SpecUtils.getScreenResolution(mContext)
            findViewById<TextView>(R.id.device_ram).text = "${SpecUtils.getTotalRam()}"
        }
    }

    override fun isAvailable(): Boolean {
        return true
    }

    override fun getPreferenceKey(): String {
        return KEY_DEVICE_INFO
    }

    companion object {
        private const val KEY_HW_INFO = "my_device_hw_header"
        private const val KEY_BUILD_STATUS = "my_device_info_header"
	private const val KEY_DEVICE_INFO = "my_device_info_header"

        private const val PROP_ORION_BUILD_TYPE = "ro.orion.build.variant"
        private const val PROP_ORION_VERSION = "ro.orion.modversion"
        private const val PROP_ORION_RELEASETYPE = "ro.orion.release.type"
        private const val PROP_ORION_MAINTAINER = "ro.orion.maintainer"
        private const val PROP_ORION_BUILD_VERSION = "ro.orion.version"
        private const val PROP_ORION_CHIPSET = "ro.orion.chipset"
    }
}
