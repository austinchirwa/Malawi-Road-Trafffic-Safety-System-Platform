package ApplicationScreens

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * A shared data class for bottom navigation bar items.
 * Ensures consistency across different screens.
 */
data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)
