/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.weex;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.multidex.MultiDex;

import com.alibaba.android.bindingx.plugin.weex.BindingX;
import com.alibaba.weex.commons.adapter.DefaultWebSocketAdapterFactory;
import com.alibaba.weex.commons.adapter.ImageAdapter;
import com.alibaba.weex.commons.adapter.JSExceptionAdapter;
import com.alibaba.weex.commons.adapter.PicassoBasedDrawableLoader;
import com.alibaba.weex.extend.adapter.ApmGenerator;
import com.alibaba.weex.extend.adapter.DefaultAccessibilityRoleAdapter;
import com.alibaba.weex.extend.adapter.InterceptWXHttpAdapter;
import com.alibaba.weex.extend.adapter.WXAnalyzerDemoListener;
import com.alibaba.weex.extend.component.WXComponentSyncTest;
import com.alibaba.weex.extend.component.WXMask;
import com.alibaba.weex.extend.component.WXParallax;
import com.alibaba.weex.extend.module.GeolocationModule;
import com.alibaba.weex.extend.module.MyModule;
import com.alibaba.weex.extend.module.RenderModule;
import com.alibaba.weex.extend.module.SyncTestModule;
import com.alibaba.weex.extend.module.WXEventModule;
import com.alibaba.weex.extend.module.WXTitleBar;
import com.alibaba.weex.extend.module.WXWsonTestModule;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.taobao.weex.InitConfig;
import com.taobao.weex.WXEnvironment;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.WXSDKManager;
import com.taobao.weex.bridge.WXBridgeManager;
import com.taobao.weex.common.WXException;
import com.taobao.weex.performance.WXAnalyzerDataTransfer;

public class WXApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    MultiDex.install(this);
    /**
     * Set up for fresco usage.
     * Set<RequestListener> requestListeners = new HashSet<>();
     * requestListeners.add(new RequestLoggingListener());
     * ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
     *     .setRequestListeners(requestListeners)
     *     .build();
     * Fresco.initialize(this,config);
     **/
//    initDebugEnvironment(true, false, "DEBUG_SERVER_HOST");
    WXBridgeManager.updateGlobalConfig("wson_on");
    WXEnvironment.setOpenDebugLog(true);
    WXEnvironment.setApkDebugable(true);
    WXSDKEngine.addCustomOptions("appName", "WXSample");
    WXSDKEngine.addCustomOptions("appGroup", "WXApp");
    WXSDKEngine.initialize(this,
                           new InitConfig.Builder()
                               //.setImgAdapter(new FrescoImageAdapter())// use fresco adapter
                               .setImgAdapter(new ImageAdapter())
                                   .setDrawableLoader(new PicassoBasedDrawableLoader(getApplicationContext()))
                               .setWebSocketAdapterFactory(new DefaultWebSocketAdapterFactory())
                               .setJSExceptionAdapter(new JSExceptionAdapter())
                               .setHttpAdapter(new InterceptWXHttpAdapter())
                               .setApmGenerater(new ApmGenerator())
                               .build()
                          );
    WXSDKManager.getInstance().addWXAnalyzer(new WXAnalyzerDemoListener());
    WXAnalyzerDataTransfer.isOpenPerformance = false;

    WXSDKManager.getInstance().setAccessibilityRoleAdapter(new DefaultAccessibilityRoleAdapter());

    try {
      Fresco.initialize(this);
      WXSDKEngine.registerComponent("synccomponent", WXComponentSyncTest.class);
      WXSDKEngine.registerComponent(WXParallax.PARALLAX, WXParallax.class);

      WXSDKEngine.registerModule("render", RenderModule.class);
      WXSDKEngine.registerModule("event", WXEventModule.class);
      WXSDKEngine.registerModule("syncTest", SyncTestModule.class);

      WXSDKEngine.registerComponent("mask",WXMask.class);
      WXSDKEngine.registerModule("myModule", MyModule.class);
      WXSDKEngine.registerModule("geolocation", GeolocationModule.class);

      WXSDKEngine.registerModule("titleBar", WXTitleBar.class);

      WXSDKEngine.registerModule("wsonTest", WXWsonTestModule.class);

      BindingX.register();

      /**
       * override default image tag
       * WXSDKEngine.registerComponent("image", FrescoImageComponent.class);
       */

      //Typeface nativeFont = Typeface.createFromAsset(getAssets(), "font/native_font.ttf");
      //WXEnvironment.setGlobalFontFamily("bolezhusun", nativeFont);

    } catch (WXException e) {
      e.printStackTrace();
    }

    registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
      @Override
      public void onActivityCreated(Activity activity, Bundle bundle) {

      }

      @Override
      public void onActivityStarted(Activity activity) {

      }

      @Override
      public void onActivityResumed(Activity activity) {

      }

      @Override
      public void onActivityPaused(Activity activity) {

      }

      @Override
      public void onActivityStopped(Activity activity) {

      }

      @Override
      public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

      }

      @Override
      public void onActivityDestroyed(Activity activity) {
        // The demo code of calling 'notifyTrimMemory()'
        if (false) {
          // We assume that the application is on an idle time.
          WXSDKManager.getInstance().notifyTrimMemory();
        }
        // The demo code of calling 'notifySerializeCodeCache()'
        if (false) {
          WXSDKManager.getInstance().notifySerializeCodeCache();
        }
      }
    });

  }

  /**
   *@param connectable debug server is connectable or not.
   *               if true, sdk will try to connect remote debug server when init WXBridge.
   *
   * @param debuggable enable remote debugger. valid only if host not to be "DEBUG_SERVER_HOST".
   *               true, you can launch a remote debugger and inspector both.
   *               false, you can  just launch a inspector.
   * @param host the debug server host, must not be "DEBUG_SERVER_HOST", a ip address or domain will be OK.
   *             for example "127.0.0.1".
   */
  private void initDebugEnvironment(boolean connectable, boolean debuggable, String host) {
    if (!"DEBUG_SERVER_HOST".equals(host)) {
      WXEnvironment.sDebugServerConnectable = connectable;
      WXEnvironment.sRemoteDebugMode = debuggable;
      WXEnvironment.sRemoteDebugProxyUrl = "ws://" + host + ":8088/debugProxy/native";
    }
  }

}
