package cn.a10miaomiao.bilimiao.compose.components.glass

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** 通用玻璃 TabRow，自带独立采样层；接口与原 TabRow 一致。 */
@Composable
fun GlassTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    scrollable: Boolean = false,
    content: @Composable RowScope.() -> Unit,
) {
    val dark = isSystemInDarkTheme()
    val tint = if (dark) {
        Color.White.copy(alpha = 0.14f)
    } else {
        Color.White.copy(alpha = 0.30f)
    }
    ProvideTopGlassBackdrop {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .height(48.dp),
        ) {
            // 录制层与玻璃同形状，避免光斑溢出
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .topGlassBackdropSource()
                    .clip(RoundedCornerShape(24.dp))
                    .glassAmbientBlobs(bottomBlobs = false)
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (scrollable) {
                            Modifier.horizontalScroll(rememberScrollState())
                        } else {
                            Modifier
                        }
                    )
                    .glassTopBarSurface(
                        tint = tint,
                        highlightColor = Color.White,
                        blurRadius = 18.dp,
                        cornerRadius = 24.dp,
                        shadowElevation = 0.dp,
                    ),
                horizontalArrangement = if (scrollable) {
                    Arrangement.Start
                } else {
                    Arrangement.SpaceEvenly
                },
                verticalAlignment = Alignment.CenterVertically,
                content = content,
            )
        }
    }
}
