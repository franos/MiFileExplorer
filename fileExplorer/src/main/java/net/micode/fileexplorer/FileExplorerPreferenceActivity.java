/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * This file is part of FileExplorer.
 *
 * FileExplorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FileExplorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.micode.fileexplorer;

import java.io.File;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.TextUtils;

/**
 * 这是一个设置界面
 *
 * @author ShunLi
 */
public class FileExplorerPreferenceActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
    private static final String PRIMARY_FOLDER = "pref_key_primary_folder";
    private static final String READ_ROOT = "pref_key_read_root";
    private static final String SHOW_REAL_PATH = "pref_key_show_real_path";
    private static final String SYSTEM_SEPARATOR = File.separator;
    //主页文件夹编辑框
    private EditTextPreference mEditTextPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        //绑定
        mEditTextPreference = (EditTextPreference) findPreference(PRIMARY_FOLDER);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Setup the initial values设置初始值
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();

        mEditTextPreference.setSummary(
                this.getString(R.string.pref_primary_folder_summary,
                sharedPreferences.getString(PRIMARY_FOLDER, GlobalConsts.ROOT_PATH))
        );

        // Set up a listener whenever a key changes设置监听器监听变化
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedpreferences, String key) {
        if (PRIMARY_FOLDER.equals(key)) {
            mEditTextPreference.setSummary(this.getString(
                    R.string.pref_primary_folder_summary,
                    sharedpreferences.getString(PRIMARY_FOLDER, GlobalConsts.ROOT_PATH)));
        }
    }

    public static String getPrimaryFolder(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String primaryFolder = settings.getString(PRIMARY_FOLDER, context.getString(R.string.default_primary_folder, GlobalConsts.ROOT_PATH));

        if (TextUtils.isEmpty(primaryFolder)) { // setting primary folder = empty("")
            primaryFolder = GlobalConsts.ROOT_PATH;
        }

        // it's remove the end char of the home folder setting when it with the '/' at the end.如果以斜杠结尾（这会导致向上跳出出现bug），移除
        // if has the backslash at end of the home folder, it's has minor bug at "UpLevel" function.
        int length = primaryFolder.length();
        if (length > 1 && SYSTEM_SEPARATOR.equals(primaryFolder.substring(length - 1))) { // length = 1, ROOT_PATH
            return primaryFolder.substring(0, length - 1);
        } else {
            return primaryFolder;
        }
    }

    /**
     * 是否读取根目录
     * @param context
     * @return
     */
    public static boolean isReadRoot(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        //获取设定值
        boolean isReadRootFromSetting = settings.getBoolean(READ_ROOT, false);
        //当首页文件夹中不以/mnt/sdcard开始的时，为真
        boolean isReadRootWhenSettingPrimaryFolderWithoutSdCardPrefix = !getPrimaryFolder(context).startsWith(Util.getSdDirectory());//(/mnt/sdcard)

        return isReadRootFromSetting || isReadRootWhenSettingPrimaryFolderWithoutSdCardPrefix;
    }

    /**
     * 是否显示真实路径
     * @param context
     * @return
     */
    public static boolean showRealPath(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getBoolean(SHOW_REAL_PATH, false);
    }

}
