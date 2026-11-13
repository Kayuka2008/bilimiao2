package cn.a10miaomiao.bilimiao.compose.components.appbar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.a10miaomiao.bilimiao.compose.components.glass.glassAppBarSurface

/**
 * 大屏（平板 / 横屏）用的竖排玻璃 Rail，和竖屏的底部 Dock 是同一套语言：
 *
 * - 上块：返回 / 抽屉、标题、导航项或页面菜单
 * - 下块：指针、交换这类工具按钮
 *
 * 两块之间留 8dp 缝，页面滚动时整条 Rail 向左移出屏幕。
 */
@Composable
fun AppBarHorizontal(
    title: String?,
    showBack: Boolean = true,
    showPointer: Boolean = false,
    pointerOrientation: Boolean = true,
    showExchange: Boolean = false,
    menus: List<MenuItemData> = emptyList(),
    isNavigationMenu: Boolean = false,
    checkedKey: Int? = null,
    appBarState: AppBarState,
    onBackClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onMenuItemClick: (MenuItemData) -> Unit = {},
    onPointerClick: () -> Unit = {},
    onExchangeClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val safePadding = WindowInsets.safeDrawing.only(
        WindowInsetsSides.Vertical + WindowInsetsSides.Left
    ).asPaddingValues()
    val dark = isSystemInDarkTheme()
    val glassTint = if (dark) Color.White.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.30f)
    val buttonTint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
    val hasTools = showPointer || showExchange

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(AppBarConfig.MenuWidth)
            .graphicsLayer {
                val p = appBarState.collapseProgress
                translationX = -size.width * p
                alpha = 1f - p
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(safePadding)
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    // 按内容高度收缩，别让整条 Rail 拉满屏幕只剩空玻璃
                    .weight(1f, fill = false)
                    .heightIn(max = 520.dp)
                    .glassAppBarSurface(
                        tint = glassTint,
                        highlightColor = Color.White,
                        blurRadius = 18.dp,
                        cornerRadius = 26.dp,
                        shadowElevation = 5.dp,
                    ),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    NavigationIcon(
                        icon = if (showBack) AppBarNavigationIcon.Back else AppBarNavigationIcon.Menu,
                        onClick = if (showBack) onBackClick else onMenuClick,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    if (!title.isNullOrEmpty()) {
                        Text(
                            modifier = Modifier.padding(
                                start = 4.dp,
                                end = 4.dp,
                                bottom = AppBarConfig.NavigationPadding,
                            ),
                            text = title.replace("\n", " "),
                            color = LocalContentColor.current.copy(alpha = 0.78f),
                            fontSize = AppBarConfig.TitleTextSize.value.sp,
                            lineHeight = AppBarConfig.TitleTextSize.value.sp,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    if (isNavigationMenu) {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            items(items = menus, key = { it.key }) { menuItem ->
                                AppBarNavigationItem(
                                    data = menuItem,
                                    checked = checkedKey == menuItem.key,
                                    onClick = onMenuItemClick,
                                    modifier = Modifier.animateItem(),
                                )
                            }
                        }
                    } else if (menus.isNotEmpty()) {
                        Box(modifier = Modifier.weight(1f)) {
                            HorizontalAppBarMenus(
                                menus = menus,
                                isNavigationMenu = isNavigationMenu,
                                checkedKey = checkedKey,
                                appBarState = appBarState,
                                onMenuItemClick = onMenuItemClick,
                            )
                        }
                    }
                }
            }

            if (hasTools) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (showPointer && showExchange) 104.dp else 58.dp)
                        .glassAppBarSurface(
                            tint = glassTint,
                            highlightColor = Color.White,
                            blurRadius = 14.dp,
                            cornerRadius = 24.dp,
                            shadowElevation = 3.dp,
                        ),
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        if (showPointer) {
                            RailIconButton(
                                icon = Icons.Default.Tune,
                                contentDescription = "指针",
                                buttonTint = buttonTint,
                                rotation = if (pointerOrientation) 0f else 180f,
                                onClick = onPointerClick,
                            )
                        }
                        if (showExchange) {
                            RailIconButton(
                                icon = Icons.Default.SwapVert,
                                contentDescription = "交换",
                                buttonTint = buttonTint,
                                onClick = onExchangeClick,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RailIconButton(
    icon: ImageVector,
    contentDescription: String,
    buttonTint: Color,
    rotation: Float = 0f,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(buttonTint)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = false),
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = LocalContentColor.current.copy(alpha = 0.72f),
            modifier = Modifier
                .size(20.dp)
                .rotate(rotation),
        )
    }
}
