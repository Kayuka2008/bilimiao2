package cn.a10miaomiao.bilimiao.compose.components.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* 顶栏使用独立采样层：录制层与玻璃栏是兄弟节点，避免采样到自身。 */

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
 * 悬浮玻璃顶栏，替掉原来全宽贴边的 Material3 TabRow（实色背景 + 硬下划线指示器）。
 *
 * 选中态改成玻璃内的高亮胶囊，和底栏的选中胶囊同一套语言。
 * [topInset] 是状态栏高度，由页面从 localContentInsets() 传入。
 */
@Composable
fun GlassTopBar(
    tabs: List<String>,
    selectedIndex: Int,
    onTabClick: (Int) -> Unit,
    topInset: Dp,
    modifier: Modifier = Modifier,
) {
    val dark = isSystemInDarkTheme()
    val tint = if (dark) {
        Color.White.copy(alpha = 0.14f)
    } else {
        Color.White.copy(alpha = 0.22f)
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = topInset + 4.dp)
            .height(48.dp)
            .glassTopBarSurface(
                tint = tint,
                highlightColor = Color.White,
                blurRadius = 22.dp,
                cornerRadius = 24.dp,
                shadowElevation = 0.dp,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 5.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            tabs.forEachIndexed { index, name ->
                val selected = index == selectedIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(vertical = 5.dp, horizontal = 2.dp)
                        .clip(RoundedCornerShape(19.dp))
                        .background(
                            if (selected) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                            } else {
                                Color.Transparent
                            }
                        )
                        .clickable { onTabClick(index) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = name,
                        color = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
                        },
                        fontSize = 14.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}
