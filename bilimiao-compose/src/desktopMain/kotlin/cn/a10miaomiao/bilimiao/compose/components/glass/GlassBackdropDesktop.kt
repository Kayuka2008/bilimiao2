package cn.a10miaomiao.bilimiao.compose.components.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** 桌面端没有 RenderEffect，用半透明圆角 + 亮边降级。 */
@Composable
actual fun ProvideGlassBackdrop(content: @Composable () -> Unit) {
    content()
}

@Composable
actual fun Modifier.glassBackdropSource(): Modifier = this

@Composable
actual fun Modifier.glassAmbientBlobs(
    topBlobs: Boolean,
    bottomBlobs: Boolean,
): Modifier = this

@Composable
actual fun Modifier.glassAppBarSurface(
    tint: Color,
    highlightColor: Color,
    blurRadius: Dp,
    cornerRadius: Dp,
    shadowElevation: Dp,
): Modifier = glassDesktopSurface(tint, highlightColor, cornerRadius)

@Composable
actual fun ProvideTopGlassBackdrop(content: @Composable () -> Unit) {
    content()
}

@Composable
actual fun Modifier.topGlassBackdropSource(): Modifier = this

@Composable
actual fun Modifier.glassTopBarSurface(
    tint: Color,
    highlightColor: Color,
    blurRadius: Dp,
    cornerRadius: Dp,
    shadowElevation: Dp,
): Modifier = glassDesktopSurface(tint, highlightColor, cornerRadius)

private fun Modifier.glassDesktopSurface(
    tint: Color,
    highlightColor: Color,
    cornerRadius: Dp,
): Modifier {
    val shape = RoundedCornerShape(cornerRadius)
    return this
        .background(tint, shape)
        .border(1.dp, highlightColor.copy(alpha = 0.45f), shape)
}
