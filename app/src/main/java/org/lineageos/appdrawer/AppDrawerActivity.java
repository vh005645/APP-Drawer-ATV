/*
 * Copyright (C) 2015 The CyanogenMod Project
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

package org.lineageos.appdrawer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.ListAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppDrawerActivity extends Activity {
  private PackageManager packageManager = null;
  private List<ApplicationInfo> applist = null;
  private ApplicationAdapter listadaptor = null;
  private GridView grid;

  // ---- Goi TV Guard day moi khi vao man hinh chinh ----
  // Launcher nay la man hinh chinh nen he thong tu mo no moi lan bat TV / thuc day tu che do cho.
  // Moi lan do, mo muc khoi dong TRONG SUOT cua TV Guard (BootTrigger, khong ve gi len man hinh) de TV Guard chay lai.
  private static final String TVGUARD_PKG = "com.tvguard.app";
  private static final String TVGUARD_TRIGGER = "com.tvguard.app.BootTrigger";
  private static final long WAKE_COOLDOWN_MS = 2 * 60 * 1000L;
  private static long lastWake = -1;
  private final Handler handler = new Handler(Looper.getMainLooper());

  private void wakeTvGuard() {
    long now = SystemClock.elapsedRealtime();
    if (lastWake >= 0 && now >= lastWake && now - lastWake < WAKE_COOLDOWN_MS) return;
    lastWake = now;   // dat truoc de khong lap lai khi man hinh nay duoc hien lai
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        if (isFinishing()) return;
        try {
          Intent i = new Intent();
          i.setComponent(new ComponentName(TVGUARD_PKG, TVGUARD_TRIGGER));
          if (i.resolveActivity(getPackageManager()) != null) {
            startActivity(i);
          }
        } catch (Throwable ignored) {
          // Khong co TV Guard hoac bi chan: bo qua, launcher van hoat dong binh thuong
        }
      }
    }, 500);
  }

  @Override
  protected void onResume() {
    super.onResume();
    wakeTvGuard();
  }

  // La man hinh chinh: bam Back khong duoc thoat (neu thoat, he thong mo lai ngay)
  @Override
  public void onBackPressed() {
    // khong lam gi
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView((int) R.layout.activity_main);
    this.packageManager = getPackageManager();
    new LoadApplications().execute();
    this.grid = (GridView) findViewById(R.id.grid);
    this.grid.setAdapter((ListAdapter) this.listadaptor);
    this.grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        try {
          String packageName = AppDrawerActivity.this.applist.get(position).packageName;
          Intent intent = AppDrawerActivity.this.packageManager.getLaunchIntentForPackage(packageName);
          if (intent == null) {
            intent = AppDrawerActivity.this.packageManager.getLeanbackLaunchIntentForPackage(packageName);
          }
          if (intent != null) {
            AppDrawerActivity.this.startActivity(intent);
          }
        } catch (ActivityNotFoundException e) {
          Toast.makeText(AppDrawerActivity.this, e.getMessage(),  Toast.LENGTH_LONG).show();
        } catch (Exception e2) {
          Toast.makeText(AppDrawerActivity.this, e2.getMessage(), Toast.LENGTH_LONG).show();
        }
      }
    });
  }

  private class LoadApplications extends AsyncTask<Void, Void, Void> {
    private ProgressDialog progress = null;

    @Override
    protected Void doInBackground(Void... params) {
      applist = new ArrayList<>();

      Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
      mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
      List<ResolveInfo> launchables = packageManager.queryIntentActivities(mainIntent, 0);

      Intent leanbackIntent = new Intent(Intent.ACTION_MAIN, null);
      leanbackIntent.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);
      launchables.addAll(packageManager.queryIntentActivities(leanbackIntent, 0));

      Set<String> packageNames = new HashSet<>();
      for (ResolveInfo resolveInfo : launchables) {
        if (getPackageName().equals(resolveInfo.activityInfo.packageName)) continue;
        if (packageNames.add(resolveInfo.activityInfo.packageName)) {
          applist.add(resolveInfo.activityInfo.applicationInfo);
        }
      }

      Collections.sort(applist, new ApplicationInfo.DisplayNameComparator(packageManager));
      listadaptor = new ApplicationAdapter(AppDrawerActivity.this, R.layout.grid_item, applist);

      return null;
    }

    @Override
    protected void onCancelled() {
      super.onCancelled();
    }

    @Override
    protected void onPostExecute(Void result) {
      grid.setAdapter(listadaptor);
      if (progress != null) {
        try { progress.dismiss(); } catch (Exception ignored) { }
      }
      super.onPostExecute(result);
    }

    @Override
    protected void onPreExecute() {
      progress = ProgressDialog.show(AppDrawerActivity.this, null, getString(R.string.loading_apps));
      super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
      super.onProgressUpdate(values);
    }
  }
}
