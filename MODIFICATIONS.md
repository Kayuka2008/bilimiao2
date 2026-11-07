# 改动记录

基于 [10miaomiao/bilimiao2](https://github.com/10miaomiao/bilimiao2) @ `f92e01b`（2.5.2.2），2026-11-07。
改动都在 `feat/liquid-glass` 分支，`master` 保持与上游一致。

## 做了什么

把竖屏底栏从贴边的实色 Surface 换成悬浮圆角玻璃面板，页面顶部的 Material3 TabRow
换成统一的玻璃顶栏组件。玻璃效果用 `io.github.kyant0:backdrop`：vibrancy、高斯模糊、
边缘折射、上下缘高光和内外阴影。底栏常驻，按钮排成左导航 / 中菜单 / 右工具。

顶栏在页面内容子树里，直接复用底栏的采样层会把玻璃自己录进去，所以另配了一套独立
Backdrop，录制层和玻璃栏是兄弟节点。配套调整了底栏 bounds，内容层另补了柔和光斑供折射。

构建上补了 `compileSdkMinor = 0`（backdrop AAR 要求），把 Gradle daemon 与 generator
的 JDK toolchain 从 21 降到 17。

## 文件清单

| 文件 | 状态 | 改动 |
| --- | --- | --- |
| `bilimiao-compose/build.gradle.kts` | 修改 | androidMain 增加 kyant backdrop/shapes 依赖；加 compileSdkMinor=0 |
| `bilimiao-compose/src/commonMain/kotlin/cn/a10miaomiao/bilimiao/compose/MainComposeHost.kt` | 修改 | 用 ProvideGlassBackdrop 包住界面内容，向内容层与底栏共享采样句柄 |
| `bilimiao-compose/src/commonMain/kotlin/cn/a10miaomiao/bilimiao/compose/components/appbar/AppBar.kt` | 修改 | 底部实色 Surface 改为悬浮圆角液态玻璃；按钮改为左导航/中菜单/右工具；取消 quick-return 收缩 |
| `bilimiao-compose/src/commonMain/kotlin/cn/a10miaomiao/bilimiao/compose/components/layout/ComposeScaffold.kt` | 修改 | 内容层链首挂 glassBackdropSource，并补玻璃用光斑 |
| `bilimiao-compose/src/commonMain/kotlin/cn/a10miaomiao/bilimiao/compose/components/layout/ComposeScaffoldLayoutModel.kt` | 修改 | 为悬浮底栏额外预留 18dp bounds 与 contentInsets，避免文字裁切 |
| `bilimiao-compose/src/commonMain/kotlin/cn/a10miaomiao/bilimiao/compose/pages/dynamic/components/DynamicPageScaffold.kt` | 修改 | 首页用 GlassTopBar / 其余页面用通用 GlassTabRow 替换原 Material3 TabRow，去掉实色背景与硬下划线 |
| `bilimiao-compose/src/commonMain/kotlin/cn/a10miaomiao/bilimiao/compose/pages/filter/FilterSettingPage.kt` | 修改 | 首页用 GlassTopBar / 其余页面用通用 GlassTabRow 替换原 Material3 TabRow，去掉实色背景与硬下划线 |
| `bilimiao-compose/src/commonMain/kotlin/cn/a10miaomiao/bilimiao/compose/pages/home/HomePage.kt` | 修改 | 硬顶 TabRow 改为悬浮玻璃顶栏；内容上移与玻璃重叠以产生可模糊内容 |
| `bilimiao-compose/src/commonMain/kotlin/cn/a10miaomiao/bilimiao/compose/pages/mine/MyBangumiPage.kt` | 修改 | 首页用 GlassTopBar / 其余页面用通用 GlassTabRow 替换原 Material3 TabRow，去掉实色背景与硬下划线 |
| `bilimiao-compose/src/commonMain/kotlin/cn/a10miaomiao/bilimiao/compose/pages/mine/MyFollowPage.kt` | 修改 | 首页用 GlassTopBar / 其余页面用通用 GlassTabRow 替换原 Material3 TabRow，去掉实色背景与硬下划线 |
| `bilimiao-compose/src/commonMain/kotlin/cn/a10miaomiao/bilimiao/compose/pages/search/SearchResultPage.kt` | 修改 | 首页用 GlassTopBar / 其余页面用通用 GlassTabRow 替换原 Material3 TabRow，去掉实色背景与硬下划线 |
| `bilimiao-compose/src/commonMain/kotlin/cn/a10miaomiao/bilimiao/compose/pages/time/TimeRegionDetailPage.kt` | 修改 | 首页用 GlassTopBar / 其余页面用通用 GlassTabRow 替换原 Material3 TabRow，去掉实色背景与硬下划线 |
| `bilimiao-compose/src/commonMain/kotlin/cn/a10miaomiao/bilimiao/compose/pages/user/UserFavouritePage.kt` | 修改 | 首页用 GlassTopBar / 其余页面用通用 GlassTabRow 替换原 Material3 TabRow，去掉实色背景与硬下划线 |
| `bilimiao-compose/src/commonMain/kotlin/cn/a10miaomiao/bilimiao/compose/pages/user/UserSpacePage.kt` | 修改 | 首页用 GlassTopBar / 其余页面用通用 GlassTabRow 替换原 Material3 TabRow，去掉实色背景与硬下划线 |
| `bilimiao-compose/src/commonMain/kotlin/cn/a10miaomiao/bilimiao/compose/pages/user/UserSpaceSearchPage.kt` | 修改 | 首页用 GlassTopBar / 其余页面用通用 GlassTabRow 替换原 Material3 TabRow，去掉实色背景与硬下划线 |
| `bilimiao-compose/src/commonMain/kotlin/cn/a10miaomiao/bilimiao/compose/pages/video/VideoDetailPage.kt` | 修改 | 首页用 GlassTopBar / 其余页面用通用 GlassTabRow 替换原 Material3 TabRow，去掉实色背景与硬下划线 |
| `bilimiao-grpc/generator/build.gradle.kts` | 修改 | 本地构建适配：jvmToolchain 21→17 |
| `gradle/gradle-daemon-jvm.properties` | 修改 | 本地构建适配：daemon toolchain 21→17 |
| `gradle/libs.versions.toml` | 修改 | 新增 kyantBackdrop / kyantShapes 版本与别名 |
| `bilimiao-compose/src/androidMain/java/cn/a10miaomiao/bilimiao/compose/components/glass/GlassBackdropAndroid.kt` | 新增 | 新增 Android 端真实玻璃实现（vibrancy / blur / lens / 高光 / 阴影） |
| `bilimiao-compose/src/commonMain/kotlin/cn/a10miaomiao/bilimiao/compose/components/glass/GlassBackdrop.kt` | 新增 | 新增跨平台玻璃抽象层（expect） |
| `bilimiao-compose/src/commonMain/kotlin/cn/a10miaomiao/bilimiao/compose/components/glass/GlassTabRow.kt` | 新增 | 新增通用玻璃 TabRow 组件（自带独立采样层） |
| `bilimiao-compose/src/commonMain/kotlin/cn/a10miaomiao/bilimiao/compose/components/glass/GlassTopBar.kt` | 新增 | 新增首页悬浮玻璃顶栏组件 |
| `bilimiao-compose/src/desktopMain/kotlin/cn/a10miaomiao/bilimiao/compose/components/glass/GlassBackdropDesktop.kt` | 新增 | 新增桌面端降级实现 |

## 授权

原项目是 GPL-3.0，本 fork 的修改同样以 GPL-3.0 提供；`LICENSE` 和所有源文件版权头原样保留。
这是非官方修改版，与原作者无关联。应用名和图标沿用上游，若要正式分发建议自行更换。

Releases 里的 debug APK 与本仓库源码一同提供，本分支源码即为该二进制的对应源码。
