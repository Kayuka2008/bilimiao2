package cn.a10miaomiao.bilimiao.compose.components.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.colorFilter
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.highlight.HighlightStyle
import com.kyant.backdrop.shadow.InnerShadow
import com.kyant.backdrop.shadow.Shadow
import com.kyant.shapes.RoundedCornerStyle
import com.kyant.shapes.RoundedRectangle

/* 玻璃绘制：底栏与顶栏共用一套效果，各自读独立的 Backdrop。 */

private val RimLight = Color(0xFFFFFFFF)
private val RimDark = Color(0xFFEAF1FF)
private val DyeLight = Color(0x382F6FED)
private val DyeAltLight = Color(0x29FF8A3D)
private val DyeDark = Color(0x3DB0C8FF)
private val DyeAltDark = Color(0x2EFFB870)
private val InnerShadowColor = Color(0xFF0A1220)
private val OuterShadowColor = Color(0xFF0B1220)

@Composable
actual fun ProvideGlassBackdrop(content: @Composable () -> Unit) {
    val backdrop = rememberLayerBackdrop()
    CompositionLocalProvider(LocalGlassBackdrop provides backdrop) {
        content()
    }
}

@Composable
actual fun ProvideTopGlassBackdrop(content: @Composable () -> Unit) {
    val backdrop = rememberLayerBackdrop()
    CompositionLocalProvider(LocalTopGlassBackdrop provides backdrop) {
        content()
    }
}

@Composable
actual fun Modifier.glassBackdropSource(): Modifier {
    val backdrop = LocalGlassBackdrop.current as? LayerBackdrop ?: return this
    return this.layerBackdrop(backdrop)
}

@Composable
actual fun Modifier.topGlassBackdropSource(): Modifier {
    val backdrop = LocalTopGlassBackdrop.current as? LayerBackdrop ?: return this
    return this.layerBackdrop(backdrop)
}

/** 按位置补光斑，避免上下两组叠加。 */
@Composable
actual fun Modifier.glassAmbientBlobs(
    topBlobs: Boolean,
    bottomBlobs: Boolean,
): Modifier = this.drawWithContent {
    drawContent()
    val w = size.width
    val h = size.height

    if (topBlobs) {
        val t1 = Offset(w * 0.12f, 20.dp.toPx())
        val tr1 = w * 0.62f
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0xFF7C6BFF).copy(alpha = 0.13f), Color.Transparent),
                center = t1,
                radius = tr1,
            ),
            radius = tr1,
            center = t1,
        )
        val t2 = Offset(w * 0.94f, 74.dp.toPx())
        val tr2 = w * 0.52f
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0xFFFF8A3D).copy(alpha = 0.11f), Color.Transparent),
                center = t2,
                radius = tr2,
            ),
            radius = tr2,
            center = t2,
        )
    }

    if (bottomBlobs) {
        val b1 = Offset(w * 0.10f, h - 6.dp.toPx())
        val br1 = w * 0.70f
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0xFF8B5CF6).copy(alpha = 0.17f), Color.Transparent),
                center = b1,
                radius = br1,
            ),
            radius = br1,
            center = b1,
        )
        val b2 = Offset(w * 0.96f, h - 30.dp.toPx())
        val br2 = w * 0.60f
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0xFFFF8A3D).copy(alpha = 0.15f), Color.Transparent),
                center = b2,
                radius = br2,
            ),
            radius = br2,
            center = b2,
        )
    }
}

@Composable
actual fun Modifier.glassAppBarSurface(
    tint: Color,
    highlightColor: Color,
    blurRadius: Dp,
    cornerRadius: Dp,
    shadowElevation: Dp,
): Modifier = glassSurface(
    backdrop = LocalGlassBackdrop.current as? Backdrop,
    tint = tint,
    highlightColor = highlightColor,
    blurRadius = blurRadius,
    cornerRadius = cornerRadius,
    shadowElevation = shadowElevation,
)

@Composable
actual fun Modifier.glassTopBarSurface(
    tint: Color,
    highlightColor: Color,
    blurRadius: Dp,
    cornerRadius: Dp,
    shadowElevation: Dp,
): Modifier = glassSurface(
    backdrop = LocalTopGlassBackdrop.current as? Backdrop,
    tint = tint,
    highlightColor = highlightColor,
    blurRadius = blurRadius,
    cornerRadius = cornerRadius,
    shadowElevation = shadowElevation,
)

@Composable
private fun Modifier.glassSurface(
    backdrop: Backdrop?,
    tint: Color,
    highlightColor: Color,
    blurRadius: Dp,
    cornerRadius: Dp,
    shadowElevation: Dp,
): Modifier {
    val shape: Shape = RoundedRectangle(
        cornerRadius = cornerRadius,
        style = RoundedCornerStyle.Continuous,
    )
    if (backdrop == null) {
        return this.background(tint, shape)
    }
    val dark = isSystemInDarkTheme()
    val dye = if (dark) DyeDark else DyeLight
    val dyeAlt = if (dark) DyeAltDark else DyeAltLight
    val rim = if (dark) RimDark else RimLight

    return this.drawBackdrop(
        backdrop = backdrop,
        shape = { shape },
        effects = {
            vibrancy()
            colorFilter(ColorFilter.colorMatrix(glassMixMatrix()))
            blur(blurRadius.toPx())
            lens(
                16.dp.toPx(),
                28.dp.toPx(),
                depthEffect = true,
                chromaticAberration = true,
            )
        },
        highlight = {
            Highlight(
                width = 1.6.dp,
                blurRadius = 1.1.dp,
                alpha = 0.80f,
                style = HighlightStyle.Default(
                    color = if (highlightColor == Color.White) rim else highlightColor,
                    blendMode = BlendMode.Plus,
                    angle = 90f,
                    falloff = 1.35f,
                ),
            )
        },
        shadow = {
            Shadow(
                radius = shadowElevation * 2.6f,
                offset = DpOffset(0.dp, shadowElevation),
                color = OuterShadowColor,
                alpha = if (shadowElevation > 0.dp) 0.30f else 0f,
                blendMode = BlendMode.SrcOver,
            )
        },
        innerShadow = {
            InnerShadow(
                radius = 10.dp,
                offset = DpOffset(0.dp, 3.dp),
                color = InnerShadowColor,
                alpha = 0.18f,
                blendMode = BlendMode.SrcOver,
            )
        },
        layerBlock = { },
        onDrawSurface = {
            drawRect(tint)
            drawRect(
                brush = Brush.linearGradient(listOf(dye, dyeAlt)),
                blendMode = BlendMode.Overlay,
            )
        },
    )
}

/*
 * 混色矩阵：输出 = C · (S · B) · 输入
 * 行和恒为 1，白色过完仍是白色；第 5 列量纲是 0..255。
 */
private fun glassMixMatrix(): ColorMatrix {
    val saturation = 1.24f
    val brightness = 0.018f
    val contrast = 1.075f
    val bleed = 0.021f

    val inv = 1f - saturation
    val lr = 0.213f * inv
    val lg = 0.715f * inv
    val lb = 0.072f * inv
    val sr = floatArrayOf(lr + saturation, lg, lb)
    val sg = floatArrayOf(lr, lg + saturation, lb)
    val sb = floatArrayOf(lr, lg, lb + saturation)

    val keep = 1f - 3f * bleed
    fun mixed(row: FloatArray) =
        floatArrayOf(row[0] * keep + bleed, row[1] * keep + bleed, row[2] * keep + bleed)
    val r = mixed(sr)
    val g = mixed(sg)
    val b = mixed(sb)

    val off = (0.5f * (1f - contrast) + brightness) * 255f
    return ColorMatrix(
        floatArrayOf(
            r[0] * contrast, r[1] * contrast, r[2] * contrast, 0f, off,
            g[0] * contrast, g[1] * contrast, g[2] * contrast, 0f, off,
            b[0] * contrast, b[1] * contrast, b[2] * contrast, 0f, off,
            0f, 0f, 0f, 1f, 0f,
        )
    )
}
