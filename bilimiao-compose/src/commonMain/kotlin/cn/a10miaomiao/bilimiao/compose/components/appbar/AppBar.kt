package cn.a10miaomiao.bilimiao.compose.components.appbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
 * 竖屏底栏，分两层：
 *
 * - 导航层（常驻，54dp）：返回 / 抽屉、当前页导航项或标题、展开开关
 * - 操作层（可展开，46dp）：页面下发的菜单、指针 / 交换等工具
 *
 * 两层各自是一块玻璃，中间留 6dp 缝，滚动时整条 Dock 一起下沉，
 * 只留顶部一条玻璃边在屏幕底部；上滑或点标题即恢复。
 */
@Composable
fun AppBar(
    title: String?,
    canBack: Boolean = false,
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
    val horizontalSafe = WindowInsets.safeDrawing.only(
        WindowInsetsSides.Horizontal
    ).asPaddingValues()
    val bottomSafe = WindowInsets.safeDrawing.only(
        WindowInsetsSides.Bottom
    ).asPaddingValues().calculateBottomPadding()
    val interactionSource = remember { MutableInteractionSource() }
    val dark = isSystemInDarkTheme()
    val glassTint = if (dark) Color.White.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.30f)
    val buttonTint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)

    // 导航模式下 menus 已经画在导航层，托盘里不再重复一遍
    val trayMenus = if (isNavigationMenu) emptyList() else menus
    val hasTray = trayMenus.isNotEmpty() || showPointer || showExchange
    var trayExpanded by remember { mutableStateOf(false) }
    // 换页后菜单内容会变，托盘收回去，避免停在上一个页面的菜单上
    LaunchedEffect(menus, isNavigationMenu) { trayExpanded = false }
    // 首次进入或切页时把 Dock 展开，避免延续上一个页面的收起态
    LaunchedEffect(title, menus) { appBarState.expandAppBar() }
    val trayArrow by animateFloatAsState(
        targetValue = if (trayExpanded) 180f else 0f,
        label = "trayArrow",
    )

    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .graphicsLayer {
                    // 滚动收起：下沉的同时淡出，避免停在「图标被裁一半」的中间态
                    val p = appBarState.collapseProgress
                    // 完全移出屏幕：只靠 alpha 隐藏的话，透明 Dock 仍会吃掉点击
                    translationY = 120.dp.toPx() * p
                    alpha = 1f - p
                },
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AnimatedVisibility(
                visible = trayExpanded && hasTray,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 520.dp)
                        .height(AppBarConfig.TrayHeight)
                        .glassAppBarSurface(
                            tint = glassTint,
                            highlightColor = Color.White,
                            blurRadius = 14.dp,
                            cornerRadius = 24.dp,
                            shadowElevation = 3.dp,
                        ),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (showPointer) {
                            GlassIconButton(
                                icon = Icons.Default.Tune,
                                contentDescription = "指针",
                                buttonTint = buttonTint,
                                rotation = if (pointerOrientation) 0f else 180f,
                                onClick = onPointerClick,
                            )
                        }
                        if (showExchange) {
                            GlassIconButton(
                                icon = Icons.Default.SwapVert,
                                contentDescription = "交换",
                                buttonTint = buttonTint,
                                onClick = onExchangeClick,
                            )
                        }
                        if (trayMenus.isNotEmpty()) {
                            Box(modifier = Modifier.weight(1f)) {
                                VerticalAppBarMenus(
                                    menus = trayMenus,
                                    isNavigationMenu = isNavigationMenu,
                                    checkedKey = checkedKey,
                                    appBarState = appBarState,
                                    onMenuItemClick = onMenuItemClick,
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 520.dp)
                    .height(AppBarConfig.DockHeight)
                    .glassAppBarSurface(
                        tint = glassTint,
                        highlightColor = Color.White,
                        blurRadius = 18.dp,
                        cornerRadius = 30.dp,
                        shadowElevation = 6.dp,
                    ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 6.dp)
                        .padding(horizontalSafe),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    NavigationIcon(
                        icon = if (canBack) AppBarNavigationIcon.Back else AppBarNavigationIcon.Menu,
                        onClick = if (canBack) onBackClick else onMenuClick,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(buttonTint),
                    )

                    if (isNavigationMenu) {
                        LazyRow(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.End,
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
                    } else {
                        AppBarTitle(
                            title = title,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                ) { appBarState.expandAppBar() },
                        )
                    }

                    if (hasTray) {
                        GlassIconButton(
                            icon = if (trayExpanded) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                            contentDescription = if (trayExpanded) "收起菜单" else "展开菜单",
                            buttonTint = buttonTint,
                            rotation = 0f,
                            onClick = { trayExpanded = !trayExpanded },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(bottomSafe + 10.dp))
        }
    }
}

/** 标题，单行省略；点击恢复到展开态。 */
@Composable
private fun AppBarTitle(
    title: String?,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (!title.isNullOrEmpty()) {
            Text(
                text = title.replace("\n", " "),
                color = LocalContentColor.current.copy(alpha = 0.78f),
                fontSize = AppBarConfig.TitleTextSize.value.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/** 圆形工具按钮。 */
@Composable
private fun GlassIconButton(
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
