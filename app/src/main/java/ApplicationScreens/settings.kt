package ApplicationScreens // Corrected package name to match directory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.MiscellaneousServices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.malawiroadtraffficsafetysystem.ui.theme.MalawiRoadTraffficSafetySystemTheme

/**
 * Data class to represent a single item in the settings list.
 * @param title The primary text for the setting item.
 * @param description A brief explanation of what the setting does.
 * @param icon The icon to display for the setting.
 * @param route The unique navigation route for this setting.
 */
data class SettingItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String
)

/**
 * The main screen for displaying app settings and navigation options.
 * It uses a Scaffold for layout and a LazyColumn for the list of settings.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigate: (route: String) -> Unit = {}
) {
    val settingsList = remember {
        listOf(
            SettingItem("Profile", "Manage your personal information", Icons.Default.AccountCircle, "profile"),
            SettingItem("Authentication", "Manage password and security", Icons.Default.Security, "authentication"),
            SettingItem("Notifications", "Configure push notifications", Icons.Default.Notifications, "notifications"),
            SettingItem("Driver & Vehicle Details", "View and update your documents", Icons.Default.DirectionsCar, "vehicle_details"),
            SettingItem("Services", "Access all traffic services", Icons.Default.MiscellaneousServices, "services"),
            SettingItem("Report and Feedback", "Send us your feedback or report an issue", Icons.Default.Feedback, "report_feedback"),
            SettingItem("Log Out", "Sign out of your account", Icons.AutoMirrored.Filled.Logout, "logout")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(settingsList) { setting ->
                SettingCard(
                    setting = setting,
                    onClick = {
                        // When an item is clicked, use the onNavigate callback
                        // to trigger navigation or a specific action (like showing a dialog for logout).
                        onNavigate(setting.route)
                    }
                )
                if (settingsList.last() != setting) {
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}

/**
 * A composable for displaying a single setting item.
 * @param setting The data for the setting to display.
 * @param onClick The action to perform when the item is clicked.
 */
@Composable
fun SettingCard(
    setting: SettingItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            // Increased vertical padding for a larger touch target and more space
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = setting.icon,
            contentDescription = null, // Decorative
            // Increased icon size
            modifier = Modifier.size(32.dp),
            tint = if (setting.route == "logout") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = setting.title,
                // Increased font size for better readability
                style = MaterialTheme.typography.titleLarge,
                color = if (setting.route == "logout") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = setting.description,
                // Increased font size for better readability
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = "Navigate to ${setting.title}",
            // Increased icon size slightly to balance the layout
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsScreenPreview() {
    MalawiRoadTraffficSafetySystemTheme {
        SettingsScreen()
    }
}
