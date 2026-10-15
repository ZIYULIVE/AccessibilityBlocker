# AccessibilityBlocker (LSPosed / Xposed module)

作用：阻止第三方应用读取系统可访问性相关信息（例如 Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES、AccessibilityManager 返回的已启用服务列表），用于提升隐私/防止应用滥用无障碍权限探测。

包名：com.ziyu.live

重要文件说明：
- app/src/main/java/com/ziyu/live/AccessibilityBlockerModule.java：模块实现（注册 Xposed hook）。
- module.prop：模块信息（LSPosed/Xposed 识别）。
- app/src/main/assets/xposed_init：Xposed 模块入口类列表。
- build.gradle：已配置签名（使用 keystore.jks），并包含 generateKeystore 任务自动生成 keystore（需要本机安装 keytool）。
- app/libs/xposed-api-82.jar：占位（请将 Xposed/LSPosed 提供的 xposed API jar 放入此目录）。

一键编译与签名（release）：
1. 把 xposed-api jar 放入 app/libs/xposed-api-82.jar
2. 在项目根目录执行：
   ./gradlew :app:assembleRelease
3. 第一次运行会触发 generateKeystore 任务；如果 keystore.jks 不存在，会自动生成。
4. 签名好的 APK 在：
   app/build/outputs/apk/release/app-release.apk

安装：
- 将 APK 安装到设备
- 设备需已安装 LSPosed 或 Xposed
- 在 LSPosed 中启用模块并重启进程/设备

兼容性说明：
- 本模块针对 Settings.Secure.getString(...) 与 AccessibilityManager.getEnabledAccessibilityServiceList(...) 做 Hook，适用于 Android 12/13/14 常见查询路径。
- 具体 ROM / 设备差异可能需要扩展更多 hook 点。

免责声明：
- 本模块涉及系统级 Hook / 可访问性控制，请仅在合法和合规场景中使用。
