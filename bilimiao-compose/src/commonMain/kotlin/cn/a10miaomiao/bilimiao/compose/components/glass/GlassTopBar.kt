package cn.a10miaomiao.bilimiao.compose.components.glass

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.a10miaomiao.bilimiao.compose.components.appbar.LocalAppBarState
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/*
 * 顶栏使用独立采样层：录制层与玻璃栏是兄弟节点，避免采样到自身。
 */

val LocalTopGlassBackdrop = staticCompositionLocalOf<Any?> { null }

@Composable
expect fun ProvideTopGlassBackdrop(content: @Composable () -> Unit)

@Composable
expect fun Modifier.topGlassBackdropSource(): Modifier

@Composable
expect fun Modifier.glassTopBarSurface(
    tint: Color,
    highlightColor: Color,
    blurRadius: Dp = 22.dp,
    cornerRadius: Dp = 24.dp,
    shadowElevation: Dp = 6.dp,
): Modifier

/**
 * 悬浮玻璃顶栏。
 *
 * 选中块可以横向拖动跟手移动，松手按最近格吸附；位移没超过 touchSlop 就当点击。
 * 页面滚动时整条顶栏上移淡出，把空间让给内容，和底栏 Dock 的下沉形成呼应。
 */
@Composable
fun GlassTopBar(
    tabs: List<String>,
    selectedIndex: Int,
    onTabClick: (Int) -> Unit,
    topInset: Dp,
    modifier: Modifier = Modifier,
) {
    if (tabs.isEmpty()) return
    val appBarState = LocalAppBarState.current
    val position = remember { Animatable(selectedIndex.toFloat()) }
    var dragging by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val settle = remember { spring<Float>(dampingRatio = 0.72f, stiffness = 420f) }

    LaunchedEffect(selectedIndex) {
        if (!dragging) position.animateTo(selectedIndex.toFloat(), settle)
    }

    val dark = isSystemInDarkTheme()
    val tint = if (dark) Color.White.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.30f)
    val activeColor = MaterialTheme.colorScheme.primary
    val idleColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
    val pillShape = remember { RoundedCornerShape(19.dp) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = topInset + 4.dp)
            .height(48.dp)
            .graphicsLayer {
                val p = appBarState?.collapseProgress ?: 0f
                translationY = -12.dp.toPx() * p
                alpha = 1f - 0.4f * p
            }
            .glassTopBarSurface(
                tint = tint,
                highlightColor = Color.White,
                blurRadius = 22.dp,
                cornerRadius = 24.dp,
                shadowElevation = 0.dp,
            ),
    ) {
        val slot = maxWidth / tabs.size
        val slotPx = with(LocalDensity.current) { slot.toPx() }

        // 选中块
        Box(
            modifier = Modifier
                .offset { IntOffset((slotPx * position.value).roundToInt(), 0) }
                .width(slot)
                .fillMaxHeight()
                .padding(vertical = 5.dp, horizontal = 3.dp)
                .graphicsLayer {
                    val stretch = if (dragging) 0.04f else 0f
                    scaleX = 1f + stretch
                    scaleY = 1f - stretch
                }
                .clip(pillShape)
                .background(
                    Brush.verticalGradient(
                        listOf(activeColor.copy(alpha = 0.20f), activeColor.copy(alpha = 0.10f))
                    )
                )
                .border(0.8.dp, activeColor.copy(alpha = 0.22f), pillShape),
        )

        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            tabs.forEachIndexed { index, name ->
                val activation = (1f - abs(position.value - index)).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = name,
                        color = lerp(idleColor, activeColor, activation),
                        fontSize = 14.sp,
                        fontWeight = if (activation > 0.5f) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1,
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(tabs.size, slotPx) {
                    awaitEachGesture {
                        val down = awaitFirstDown()
                        val startSlot = (down.position.x / slotPx).toInt().coerceIn(0, tabs.size - 1)
                        var moved = false
                        dragging = true
                        scope.launch { position.stop() }
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            if (!change.pressed) break
                            val dx = change.position.x - down.position.x
                            if (!moved && abs(dx) > viewConfiguration.touchSlop) moved = true
                            if (moved) {
                                scope.launch {
                                    position.snapTo(
                                        (startSlot + dx / slotPx)
                                            .coerceIn(0f, (tabs.size - 1).toFloat())
                                    )
                                }
                                change.consume()
                            }
                        }
                        dragging = false
                        val target = if (moved) {
                            position.value.roundToInt().coerceIn(0, tabs.size - 1)
                        } else {
                            startSlot
                        }
                        if (target != selectedIndex) onTabClick(target)
                        scope.launch { position.animateTo(target.toFloat(), settle) }
                    }
                },
        )
    }
}
