package com.example.malawiroadtraffficsafetysystem

import ApplicationScreens.AuthenticationScreen
import ApplicationScreens.ChangePasswordScreen
import ApplicationScreens.ForgotPasswordScreen
import ApplicationScreens.HomeScreen
import ApplicationScreens.LearnerLicenseScreen
import ApplicationScreens.NotificationsScreen
import ApplicationScreens.PaymentScreen
import ApplicationScreens.ProfileUpdateScreen
import ApplicationScreens.ServicePage
import ApplicationScreens.SettingsScreen
import ApplicationScreens.SignUpScreen
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.malawiroadtraffficsafetysystem.ui.theme.MalawiRoadTraffficSafetySystemTheme
import services.CofApplicationScreen
import services.CofDashboardScreen
import services.DrivingSchoolDetailScreen
import services.EnrollmentScreen
import services.HighwayCodeScreen
import services.RenewalsScreen
import services.ReportIncidentScreen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

import ApplicationScreens.LicenseDetailsScreen
import ApplicationScreens.ScheduleCOFScreen
import ApplicationScreens.InsuranceServicesScreen
import ApplicationScreens.EmergencyContactsScreen

// ... existing imports ...

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

        NavHost(navController = navController, startDestination = "authentication") {

            composable("authentication") {
                AuthenticationScreen(
                    onLoginSuccess = {
                        navController.navigate("home") { 
                            popUpTo("authentication") { inclusive = true }
                        }
                    },
                    onSignUpClick = {
                        navController.navigate("signup")
                    },
                    onForgotPasswordClick = {
                        navController.navigate("forgot_password")
                    }
                )
            }

            composable("signup") {
                SignUpScreen(
                    onSignUpSuccess = {
                        navController.navigate("home") {
                            popUpTo("authentication") { inclusive = true }
                        }
                    },
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }

            composable("forgot_password") {
                ForgotPasswordScreen(
                    onBackToLoginClick = { navController.popBackStack() }
                )
            }

            composable("home") {
                HomeScreen(
                    onNavigate = { route -> navController.navigate(route) },
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
                    onProfileClick = { navController.navigate("profile_update") }
                )
            }

            composable("profile_update") {
                ProfileUpdateScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToChangePassword = { navController.navigate("change_password") },
                    onSaveChanges = {
                        Toast.makeText(context, "Profile Saved!", Toast.LENGTH_SHORT).show()
                        navController.navigate("home") {
                            popUpTo("profile_update") { inclusive = true } 
                        }
                    }
                )
            }

            composable("change_password") {
                ChangePasswordScreen(
                    onBackClick = { navController.popBackStack() },
                    onPasswordChanged = {
                        navController.navigate("home") {
                            popUpTo("change_password") { inclusive = true }
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
                        navController.navigate("school_details/${school.id}")
                    }
                )
            }

            composable(
                route = "school_details/{schoolId}",
                arguments = listOf(navArgument("schoolId") { type = NavType.StringType })
            ) { backStackEntry ->
                DrivingSchoolDetailScreen(
                    schoolId = backStackEntry.arguments?.getString("schoolId"),
                    onConfirm = { navController.navigate("payment") },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("renew_license") {
                RenewalsScreen(
                    onBackClick = { navController.popBackStack() },
                    onNavigateToPayment = { navController.navigate("payment") }
                )
            }

            composable("highway_code") {
                HighwayCodeScreen(onBackClick = { navController.popBackStack() })
            }

            composable("report_incident") {
                ReportIncidentScreen(onBackClick = { navController.popBackStack() })
            }

            composable("cof_application") {
                CofApplicationScreen(
                    onBackClick = { navController.popBackStack() },
                    onSubmitClick = { plateNumber, vehicleModel ->
                        val encodedPlate = URLEncoder.encode(plateNumber, StandardCharsets.UTF_8.toString())
                        val encodedModel = URLEncoder.encode(vehicleModel, StandardCharsets.UTF_8.toString())
                        navController.navigate("cof_dashboard/$encodedPlate/$encodedModel")
                    }
                )
            }

            composable(
                route = "cof_dashboard/{plateNumber}/{vehicleModel}",
                arguments = listOf(
                    navArgument("plateNumber") { type = NavType.StringType },
                    navArgument("vehicleModel") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                CofDashboardScreen(
                    plateNumber = backStackEntry.arguments?.getString("plateNumber") ?: "",
                    vehicleModel = backStackEntry.arguments?.getString("vehicleModel") ?: "",
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("notifications") {
                NotificationsScreen(onBackClick = { navController.popBackStack() })
            }

            composable("learner_license") {
                LearnerLicenseScreen(
                    onApplyClick = { navController.navigate("payment") }, // Navigate to a payment screen on successful application
                    onBackClick = { navController.popBackStack() }
                )
            }

            // New Screens
            composable("license_details") {
                LicenseDetailsScreen(onBackClick = { navController.popBackStack() })
            }

            composable("schedule_cof") {
                ScheduleCOFScreen(onBackClick = { navController.popBackStack() })
            }

            composable("insurance_services") {
                InsuranceServicesScreen(onBackClick = { navController.popBackStack() })
            }

            composable("emergency_contacts") {
                EmergencyContactsScreen(onBackClick = { navController.popBackStack() })
            }

            val placeholderRoutes = listOf(
                "vehicle_details", "report_feedback",
                "citizen_reporting", "safety_awareness",
                "fine_payment", "vehicle_registration"
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
