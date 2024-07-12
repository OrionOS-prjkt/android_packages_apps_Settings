/*
 * Copyright (C) 2023 The risingOS Android Project
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

package com.android.settings.utils;

import android.content.Context;
import android.os.SystemProperties;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

import com.android.settings.R;

public class DeviceCodenameTextView extends TextView {

    private static final String KEY_DEVICE_CODENAME = "ro.product.device";

    private String deviceCodenameText;
    private Context mContext;

    public DeviceCodenameTextView(Context context) {
        super(context);
        mContext = context;
    }

    public DeviceCodenameTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public DeviceCodenameTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        init();
    }

    private void init() {
        setText(getDeviceCodename());
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setText(getDeviceCodename());
                handler.postDelayed(this, 2000);
            }
        }, 2000);
    }

    private String getDeviceCodename() {
        String deviceCodenameText = SystemProperties.get(KEY_DEVICE_CODENAME);
        return deviceCodenameText;
    }
}