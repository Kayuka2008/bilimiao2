package cn.a10miaomiao.bilimiao.compose.components.glass

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 液态玻璃底栏的采样句柄。
 *
 * backdrop 依赖 Android 的 RenderEffect / AGSL RuntimeShader，是 Android 专属能力，
 * 类型不能直接出现在 commonMain；用 Any? 承载，由各平台 actual 自行转型。
 */
val LocalGlassBackdrop = staticCompositionLocalOf<Any?> { null }

/** 创建 Backdrop 并向下提供，必须包住「录制层」和「玻璃底栏」的共同父组合。 */
@Composable
expect fun ProvideGlassBackdrop(content: @Composable () -> Unit)

/**
 * 把页面内容标记为玻璃的采样源（Android 上 = layerBackdrop）。
 * 必须挂在内容层 Modifier 的**链首**，否则录不到整棵子树。
 */
@Composable
expect fun Modifier.glassBackdropSource(): Modifier

/** 在内容层补柔和光斑，给玻璃提供可折射的颜色；桌面端 no-op。 */
@Composable
expect fun Modifier.glassAmbientBlobs(
    topBlobs: Boolean = true,
    bottomBlobs: Boolean = true,
): Modifier

/**
 * 玻璃底栏背景。
 * - Android：vibrancy + 混色 + 高斯模糊 + 边缘折射 + 镜面高光 + 内/外阴影
 * - 桌面端：半透明圆角 + 亮边
 *
 * [tint] 必须是**白色半透明**，不能用 surfaceContainer 那种灰：
 * 灰 tint 会把背后内容压成灰板，正是「只有阴影没有玻璃」的成因。
 */
@Composable
expect fun Modifier.glassAppBarSurface(
    tint: Color,
    highlightColor: Color,
    blurRadius: Dp = 18.dp,
    cornerRadius: Dp = 30.dp,
    shadowElevation: Dp = 8.dp,
): Modifier
