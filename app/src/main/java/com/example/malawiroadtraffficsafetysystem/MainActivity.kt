package com.example.malawiroadtraffficsafetysystem

import ApplicationScreens.AuthenticationScreen
import ApplicationScreens.HomeScreen
import ApplicationScreens.PaymentScreen
import ApplicationScreens.ProfileUpdateScreen
import ApplicationScreens.ServicePage
import ApplicationScreens.SettingsScreen
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.malawiroadtraffficsafetysystem.ui.theme.MalawiRoadTraffficSafetySystemTheme
import services.EnrollmentScreen
import services.HighwayCodeScreen
import services.RenewalsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainApp()
        }
    }
}

@Composable
fun MainApp() {
    MalawiRoadTraffficSafetySystemTheme {
        val navController = rememberNavController()
        val context = LocalContext.current // For showing toasts

        NavHost(navController = navController, startDestination = "authentication") { // Start destination is now authentication

            composable("authentication") {
                AuthenticationScreen(
                    onLogInClick = { _, _ ->
                        navController.navigate("home") { // Navigate to home after login
                            popUpTo("authentication") { inclusive = true }
                        }
                    },
                    onSignUpClick = { _, _, _ ->
                        navController.navigate("home") { // Navigate to home after sign up
                            popUpTo("authentication") { inclusive = true }
                        }
                    }
                )
            }

            composable("home") {
                HomeScreen(
                    onNavigate = { route -> navController.navigate(route) },
                    onMenuClick = { Toast.makeText(context, "Menu clicked!", Toast.LENGTH_SHORT).show() },
                    onProfileClick = { navController.navigate("profile_update") }
                )
            }

            composable("services") {
                ServicePage(
                    onNavigate = { route ->
                        if (route != "services") {
                            navController.navigate(route)
                        }
                    },
                    onMenuClick = {
                        Toast.makeText(context, "Menu clicked - Drawer not implemented yet.", Toast.LENGTH_SHORT).show()
                    },
                    onProfileClick = { navController.navigate("profile_update") }
                )
            }

            composable("profile_update") {
                ProfileUpdateScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToChangePassword = { /* TODO: Navigate to a change password screen */ },
                    onSaveChanges = {
                        Toast.makeText(context, "Profile Saved!", Toast.LENGTH_SHORT).show()
                        navController.navigate("home") {
                            popUpTo("profile_update") { inclusive = true } // Go back to home and clear the profile screen from the back stack
                        }
                    }
                )
            }

            composable("settings") {
                SettingsScreen(
                    onNavigate = { route ->
                        if (route == "logout") {
                            navController.navigate("authentication") {
                                popUpTo(0) { inclusive = true }
                            }
                        } else {
                            navController.navigate(route)
                        }
                    }
                )
            }

            composable("payment") {
                PaymentScreen(
                    onConfirmPayment = { navController.popBackStack() },
                    onCancelPayment = {
                        navController.navigate("services") {
                            popUpTo("payment") { inclusive = true }
                        }
                    }
                )
            }

            composable("enrollment") {
                EnrollmentScreen(
                    onBackClick = { navController.popBackStack() },
                    onEnrollClick = { school ->
                        Toast.makeText(context, "Enrolled in ${school.name}", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                )
            }

            composable("renew_license") {
                RenewalsScreen(
                    onBackClick = { navController.popBackStack() },
                    onNavigateToPayment = { navController.navigate("payment") }
                )
            }

            // --- Highway Code Screen ---
            composable("highway_code") {
                HighwayCodeScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            // --- Placeholder screens for remaining features ---
            val placeholderRoutes = listOf(
                "notifications", "vehicle_details", "report_feedback", "cof_application",
                "learner_license", "citizen_reporting", "safety_awareness",
                "fine_payment", "report_incident", "vehicle_registration", "insurance_services"
            )

            placeholderRoutes.forEach { route ->
                composable(route) {
                    PlaceholderScreen(
                        screenName = route.replaceFirstChar { it.uppercase() },
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

@Composable
fun PlaceholderScreen(screenName: String, onBackClick: () -> Unit) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "$screenName Screen", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "This feature is not yet implemented.")
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onBackClick) {
                Text("Go Back")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainAppPreview() {
    MainApp()
}
