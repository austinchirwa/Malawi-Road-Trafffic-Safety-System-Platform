package ApplicationScreens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DriveEta
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.malawiroadtraffficsafetysystem.ui.theme.MalawiRoadTraffficSafetySystemTheme

/**
 * The main home screen for the RTSS platform.
 * Provides quick access to essential services and safety information.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigate: (route: String) -> Unit,
    onMenuClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val bottomNavItems = remember {
        listOf(
            BottomNavItem("Home", Icons.Default.Home, "home"),
            BottomNavItem("Services", Icons.Default.DriveEta, "services"),
            BottomNavItem("Notifications", Icons.Default.Notifications, "notifications"),
            BottomNavItem("Settings", Icons.Default.Settings, "settings")
        )
    }
    var selectedItem by remember { mutableIntStateOf(0) } // Home is selected by default

    Scaffold(
        topBar = {
            TopAppBar(
                title = { /* Title Removed for a cleaner look */ },
                navigationIcon = { IconButton(onClick = onMenuClick) { Icon(Icons.Default.Menu, "Menu") } },
                actions = { IconButton(onClick = onProfileClick) { Icon(Icons.Default.Person, "Profile") } }
            )
        },
        bottomBar = {
            BottomAppBar {
                NavigationBar {
                    bottomNavItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedItem == index,
                            onClick = {
                                selectedItem = index
                                onNavigate(item.route) // Use the route for navigation
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Welcome Header
            item {
                Text(
                    text = "Welcome, Driver!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Your central hub for road safety and services in Malawi.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Quick Actions Grid
            item {
                SectionHeader("Quick Actions")
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        QuickActionItem(Modifier.weight(1f), Icons.Default.DriveEta, "Renew License", "renew_license", onNavigate)
                        QuickActionItem(Modifier.weight(1f), Icons.Default.AccountBalanceWallet, "Pay Fine", "fine_payment", onNavigate)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        QuickActionItem(Modifier.weight(1f), Icons.Default.Report, "Report Incident", "report_incident", onNavigate)
                        QuickActionItem(Modifier.weight(1f), Icons.Default.Gavel, "Highway Code", "highway_code", onNavigate)
                    }
                }
            }

            // Featured Safety Campaign/Alert
            item {
                FeaturedAlertCard()
            }

            // Safety Tip of the Day
            item {
                SectionHeader("Safety Tip of the Day")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Always wear your seatbelt. It is your first line of defense in a crash.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            // Emergency Contacts
            item {
                SectionHeader("Emergency Contacts")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    EmergencyContactItem("Police", "997", Icons.Default.LocalPolice)
                    EmergencyContactItem("Ambulance", "998", Icons.Default.Call)
                }
            }

            // Affiliated Authorities
            item {
                SectionHeader("Recognized Authorities")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AuthorityLogo(icon = Icons.Default.LocalPolice, name = "Malawi Police")
                    AuthorityLogo(icon = Icons.Default.AccountBalance, name = "Local Government")
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun FeaturedAlertCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Warning, "Safety Alert", modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            Column {
                Text("Featured Safety Campaign", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Be aware of new speed limits on the M1 road. Drive safely!", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun AuthorityLogo(icon: ImageVector, name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // To use your own logos, replace this Icon with an Image composable:
        // Image(painter = painterResource(id = R.drawable.your_logo), contentDescription = name)
        Icon(imageVector = icon, contentDescription = name, modifier = Modifier.size(50.dp), tint = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = name, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

@Composable
private fun QuickActionItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    route: String,
    onNavigate: (String) -> Unit
) {
    Surface(
        modifier = modifier.clickable { onNavigate(route) },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun EmergencyContactItem(name: String, number: String, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = name, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            Text(text = name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Text(text = number, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    MalawiRoadTraffficSafetySystemTheme {
        HomeScreen(onNavigate = {}, onMenuClick = {}, onProfileClick = {})
    }
}
