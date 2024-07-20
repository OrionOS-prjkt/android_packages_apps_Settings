/*
 * Copyright (C) 2023 the DroidxOS Android Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.deviceinfo.aboutphone

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.SystemProperties
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.view.View
import android.view.LayoutInflater
import android.util.Log
import androidx.preference.Preference
import androidx.preference.PreferenceScreen
import com.android.settings.R
import com.android.settingslib.core.AbstractPreferenceController
import com.android.settingslib.DeviceInfoUtils
import com.android.settingslib.widget.LayoutPreference

import com.airbnb.lottie.LottieAnimationView

import com.android.settings.utils.SpecUtils

class AkihabaraDeviceInfoPreferenceController(context: Context) : AbstractPreferenceController(context) {

    private val defaultFallback = mContext.getString(R.string.device_info_default)

    private fun getProp(propName: String): String {
        return SystemProperties.get(propName, defaultFallback)
    }

    private fun getPropertyOrDefault(propName: String): String {
        return SystemProperties.get(propName, defaultFallback)
    }

    private fun getProp(propName: String, customFallback: String): String {
        val propValue = SystemProperties.get(propName)
        return if (propValue.isNotEmpty()) propValue else SystemProperties.get(customFallback, "Unknown")
    }

    private fun getDeviceChipset(): String {
        return getProp(KEY_DEVICE_CHIPSET)
    }

    private fun getOrionVersion(): String {
        val propOrionVer = getProp(PROP_ORION_VERSION)
        val propOrionCodeVer = getProp(PROP_ORION_CODE_VERSION)

        return "OrionOS Version : $propOrionVer $propOrionCodeVer"
    }

    private fun getOrionBuildStatus(releaseType: String): String {
        return mContext.getString(if (releaseType == "official") R.string.official_title else R.string.unofficial_title)
    }

    override fun displayPreference(screen: PreferenceScreen) {
        super.displayPreference(screen)

        val releaseType = getProp(PROP_ORION_RELEASETYPE).lowercase()
        val isOfficial = releaseType == "official"
        val orionVersion = getOrionVersion()

        val hwInfoPreference = screen.findPreference<LayoutPreference>(KEY_HW_INFO)!!
        val buildStatusPreference = screen.findPreference<Preference>(KEY_BUILD_STATUS_INFO)!!

        val layoutInflater = LayoutInflater.from(buildStatusPreference.context)
        val buildStatusCard = layoutInflater.inflate(if (releaseType == "official") R.layout.build_status_layout_accent else R.string.build_status_layout)

        buildStatusPreference.setTitle(getOrionBuildStatus(releaseType).lowercase())
        buildStatusPreference.setSummary(orionVersion)
        buildStatusPreference.setLayoutResource(buildStatusCard)

        hwInfoPreference.apply {
            findViewById<TextView>(R.id.akihabara_device_chipset).text = getDeviceChipset()
            findViewById<TextView>(R.id.akihabara_device_ram).text = "${SpecUtils.getTotalRam()}"
        }

        buildStatusPreference.apply {
            val lottieView = findViewById<LottieAnimationView>(R.id.rom_status_lottie)
            if (isOfficial) lottieView.setAnimation(R.raw.verified) else lottieView.setAnimation(R.raw.unverified)
            lottieView.playAnimation()
        }
    }

    override fun isAvailable(): Boolean {
        return true
    }

    override fun getPreferenceKey(): String {
        return KEY_DEVICE_INFO
    }

    companion object {

        private const val KEY_HW_INFO = "akihabara_device_hw"
        private const val KEY_ROM_STATUS_INFO = "device_verified_rom_status"
        private const val KEY_DEVICE_INFO = "my_device_info_header"

        private const val KEY_DEVICE_CHIPSET = "ro.board.platform"

        private const val PROP_ORION_RELEASETYPE = "ro.orion.release.type"
        private const val PROP_ORION_VERSION = "ro.orion.version"
        private const val PROP_ORION_CODE_VERSION = "ro.modversion"

    }
}