package ApplicationScreens

import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DriveEta
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.malawiroadtraffficsafetysystem.ui.theme.MalawiRoadTraffficSafetySystemTheme
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Data class to define the structure for a service item.
 */
data class ServiceItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String // Added for type-safe navigation
)

/**
 * The main screen that displays a list of available traffic services.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicePage(
    onNavigate: (route: String) -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    // --- STATE MANAGEMENT ---
    var searchQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var isMenuExpanded by remember { mutableStateOf(false) }

    // --- Show scroll-to-top button only when the user has scrolled down ---
    val showScrollToTopButton by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0
        }
    }

    // --- SERVICE DATA ---
    val allServices = remember {
        listOf(
            ServiceItem("Enroll in Driving Schools", "Find and enroll in certified driving schools.", Icons.Default.School, "enrollment"),
            ServiceItem("Access Highway Code", "Official road safety rules and regulations.", Icons.Default.Gavel, "highway_code"),
            ServiceItem("COF Application", "Apply for a new Certificate of Fitness.", Icons.Default.VerifiedUser, "cof_application"),
            ServiceItem("Renew License", "Renew your driver's or vehicle license.", Icons.Default.DriveEta, "renew_license"),
            ServiceItem("Driver's Learner License", "Apply for your learner's permit to practice.", Icons.AutoMirrored.Filled.LibraryBooks, "learner_license"),

            ServiceItem("Road Safety Awareness", "Access educational materials and campaigns.", Icons.Default.School, "safety_awareness"),
            ServiceItem("Traffic Fine Payment & Appeals", "Pay fines or appeal a traffic ticket.", Icons.Default.AccountBalanceWallet, "fine_payment"),
            ServiceItem("Report Incident", "Report a traffic accident to authorities.", Icons.Default.Report, "report_incident"),
            ServiceItem("Vehicle Registration", "Register a new vehicle or transfer ownership.", Icons.Default.AppRegistration, "vehicle_registration"),
            ServiceItem("Insurance Services", "Manage your vehicle's insurance policies.", Icons.Default.Shield, "insurance_services")
        )
    }

    // --- MENU DATA ---
    val menuItems = remember {
        listOf(
            DrawerItem(Icons.Default.Notifications, "Notifications", "notifications"),
            DrawerItem(Icons.Default.Badge, "License Details", "license_details"),
            DrawerItem(Icons.Default.Event, "Schedule COF", "schedule_cof"),
            DrawerItem(Icons.Default.Shield, "Insurance Service", "insurance_services"),
            DrawerItem(Icons.Default.Call, "Emergency Contacts", "emergency_contacts")
        )
    }

    // --- BOTTOM NAVIGATION DATA ---
    val bottomNavItems = remember {
        listOf(
            BottomNavItem("Home", Icons.Default.Home, "home"),
            BottomNavItem("Services", Icons.Default.DriveEta, "services"),
            BottomNavItem("Notifications", Icons.Default.Notifications, "notifications"),
            BottomNavItem("Settings", Icons.Default.Settings, "settings")
        )
    }
    var selectedItem by remember { mutableIntStateOf(1) } // Default to 'Services'

    // --- Filtered list based on the search query ---
    val filteredServices = if (searchQuery.isEmpty()) {
        allServices
    } else {
        val query = searchQuery.lowercase(Locale.getDefault())
        allServices.filter {
            it.title.lowercase(Locale.getDefault()).contains(query) ||
                    it.description.lowercase(Locale.getDefault()).contains(query)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("RTSS SERVICES", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    Box {
                        IconButton(onClick = { isMenuExpanded = true }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = isMenuExpanded,
                            onDismissRequest = { isMenuExpanded = false }
                        ) {
                            menuItems.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item.label) },
                                    onClick = {
                                        isMenuExpanded = false
                                        onNavigate(item.route)
                                    },
                                    leadingIcon = { Icon(item.icon, contentDescription = item.label) }
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                }
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
        },
        floatingActionButton = {
            if (showScrollToTopButton) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            listState.animateScrollToItem(index = 0)
                        }
                    }
                ) {
                    Icon(Icons.Default.ArrowUpward, contentDescription = "Scroll to top")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- SEARCH BAR ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                label = { Text("Search services") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                singleLine = true
            )

            // --- LAZY COLUMN (SCROLLABLE LIST) ---
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                items(filteredServices) { service ->
                    HciOptimizedServiceCard(
                        service = service,
                        onClick = { onNavigate(service.route) }
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

/**
 * An HCI-optimized card that provides a clear, balanced, and aesthetically
 * pleasing representation of a service item.
 */
@Composable
fun HciOptimizedServiceCard(
    service: ServiceItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = service.icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = service.title,
                    style = MaterialTheme.typography.titleLarge
                )
                MarqueeText(
                    text = service.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Go to ${service.title}",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ServicePagePreview() {
    MalawiRoadTraffficSafetySystemTheme {
        ServicePage()
    }
}

@Composable
fun MarqueeText(
    text: String,
    style: androidx.compose.ui.text.TextStyle = LocalTextStyle.current,
    color: androidx.compose.ui.graphics.Color = LocalTextStyle.current.color,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var shouldAnimate by remember { mutableStateOf(true) }

    LaunchedEffect(shouldAnimate) {
        if (shouldAnimate) {
            scrollState.animateScrollTo(
                scrollState.maxValue,
                animationSpec = tween(durationMillis = 5000, delayMillis = 1000)
            )
            scrollState.scrollTo(0)
        }
    }

    Text(
        text = text,
        style = style,
        color = color,
        modifier = modifier
            .horizontalScroll(scrollState, false)
            .onGloballyPositioned {
                // Stop animating if the text fits
                shouldAnimate = it.size.width > (it.parentLayoutCoordinates?.size?.width ?: 0)
            },
        maxLines = 1,
        overflow = TextOverflow.Clip
    )
}
