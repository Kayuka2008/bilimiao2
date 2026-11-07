package cn.a10miaomiao.bilimiao.compose.components.appbar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Tune
import cn.a10miaomiao.bilimiao.compose.components.glass.glassAppBarSurface

/**
 * 竖屏下的悬浮玻璃底栏：标题条 + 菜单行，左导航 / 中菜单 / 右工具。
 * 底栏常驻，不随列表滚动收起；外层 bounds 由 ComposeScaffoldLayoutModel 预留悬浮空间。
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
    // 水平安全区在这里处理，底部安全区交给外层悬浮 padding，避免重复叠加。
    val horizontalSafe = WindowInsets.safeDrawing.only(
        WindowInsetsSides.Horizontal
    ).asPaddingValues()
    val bottomSafe = WindowInsets.safeDrawing.only(
        WindowInsetsSides.Bottom
    ).asPaddingValues().calculateBottomPadding()
    val interactionSource = remember { MutableInteractionSource() }
    val dark = isSystemInDarkTheme()
    // 白色半透明染色，低版本没有模糊时也可独立成立。
    val glassTint = if (dark) {
        Color.White.copy(alpha = 0.14f)
    } else {
        Color.White.copy(alpha = 0.30f)
    }
    val buttonTint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)

    // 不裁剪，让玻璃的外阴影可以画到 bounds 之外。
    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = bottomSafe + 10.dp)
                .glassAppBarSurface(
                    tint = glassTint,
                    highlightColor = Color.White,
                    blurRadius = 18.dp,
                    cornerRadius = 30.dp,
                    shadowElevation = 8.dp,
                ),
        ) {
            Column(modifier = Modifier.padding(horizontalSafe)) {
                // 标题条：收起后是唯一可点击入口，点击重新展开菜单行
                AppBarTitle(
                    title = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(AppBarConfig.TitleHeight)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                        ) {
                            appBarState.expandAppBar()
                        }
                )

                AppBarMenuRow(
                    canBack = canBack,
                    showPointer = showPointer,
                    pointerOrientation = pointerOrientation,
                    showExchange = showExchange,
                    menus = menus,
                    isNavigationMenu = isNavigationMenu,
                    checkedKey = checkedKey,
                    appBarState = appBarState,
                    onBackClick = onBackClick,
                    onMenuClick = onMenuClick,
                    onMenuItemClick = onMenuItemClick,
                    onPointerClick = onPointerClick,
                    onExchangeClick = onExchangeClick,
                    buttonTint = buttonTint,
                )
            }
        }
    }
}

/** 标题条，点击可展开菜单行。 */
@Composable
private fun AppBarTitle(
    title: String?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
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

/** 菜单行：左导航、中间菜单区、右工具按钮。 */
@Composable
private fun AppBarMenuRow(
    canBack: Boolean,
    showPointer: Boolean,
    pointerOrientation: Boolean,
    showExchange: Boolean,
    menus: List<MenuItemData>,
    isNavigationMenu: Boolean,
    checkedKey: Int?,
    appBarState: AppBarState,
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit,
    onMenuItemClick: (MenuItemData) -> Unit,
    onPointerClick: () -> Unit,
    onExchangeClick: () -> Unit,
    buttonTint: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(AppBarConfig.MenuHeight)
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NavigationIcon(
            icon = if (canBack) AppBarNavigationIcon.Back else AppBarNavigationIcon.Menu,
            onClick = if (canBack) onBackClick else onMenuClick,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(buttonTint),
        )

        // 菜单区：导航模式用选中态胶囊列表，普通模式用可弹子菜单的菜单树
        if (isNavigationMenu) {
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End,
            ) {
                items(
                    items = menus,
                    key = { it.key },
                ) { menuItem ->
                    AppBarNavigationItem(
                        data = menuItem,
                        checked = checkedKey == menuItem.key,
                        onClick = onMenuItemClick,
                        modifier = Modifier.animateItem(),
                    )
                }
            }
        } else {
            Box(modifier = Modifier.weight(1f)) {
                VerticalAppBarMenus(
                    menus = menus,
                    isNavigationMenu = isNavigationMenu,
                    checkedKey = checkedKey,
                    appBarState = appBarState,
                    onMenuItemClick = onMenuItemClick,
                )
            }
        }

        if (showPointer) {
            GlassIconButton(
                icon = Icons.Default.Tune,
                contentDescription = "指针",
                rotation = if (pointerOrientation) 0f else 180f,
                buttonTint = buttonTint,
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
    }
}

/** 圆形底的工具按钮。 */
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
