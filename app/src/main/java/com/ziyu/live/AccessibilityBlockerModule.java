package com.ziyu.live;

import android.content.ContentResolver;
import android.provider.Settings;

import java.util.Collections;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * LSPosed / Xposed module to block apps from reading Accessibility info.
 * Package name: com.ziyu.live
 */
public class AccessibilityBlockerModule implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        final String pkg = lpparam.packageName;
        if (pkg == null) return;

        if (pkg.equals("android") || pkg.startsWith("com.android") || pkg.equals("com.ziyu.live")) {
            return;
        }

        try {
            Class<?> secureCls = XposedHelpers.findClass("android.provider.Settings$Secure", lpparam.classLoader);
            XposedHelpers.findAndHookMethod(secureCls, "getString", ContentResolver.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Object nameObj = param.args[1];
                    if (nameObj instanceof String) {
                        String name = (String) nameObj;
                        if ("enabled_accessibility_services".equals(name) || Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES.equals(name)) {
                            XposedBridge.log("AccessibilityBlocker: blocked Settings.Secure.getString for " + name + " in " + pkg);
                            param.setResult(null);
                        }
                    }
                }
            });
        } catch (Throwable t) {
            XposedBridge.log("AccessibilityBlocker: failed to hook Settings.Secure.getString: " + t.getMessage());
        }

        try {
            Class<?> amCls = XposedHelpers.findClass("android.view.accessibility.AccessibilityManager", lpparam.classLoader);
            XposedHelpers.findAndHookMethod(amCls, "getEnabledAccessibilityServiceList", int.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("AccessibilityBlocker: returning empty accessibility service list for " + pkg);
                    param.setResult(Collections.emptyList());
                }
            });
        } catch (Throwable t) {
            XposedBridge.log("AccessibilityBlocker: failed to hook AccessibilityManager.getEnabledAccessibilityServiceList: " + t.getMessage());
        }
    }
}
