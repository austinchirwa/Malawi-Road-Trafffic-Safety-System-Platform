package ApplicationScreens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.malawiroadtraffficsafetysystem.R
import com.example.malawiroadtraffficsafetysystem.ui.theme.MalawiRoadTraffficSafetySystemTheme

/**
 * Data class representing a single payment method. The icon can be a drawable resource Int or an ImageVector.
 */
data class PaymentMethod(
    val name: String,
    val icon: Any // Can be @DrawableRes Int or ImageVector
)

/**
 * The main payment screen composable with an improved layout and custom icons.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    onConfirmPayment: (selectedMethod: PaymentMethod) -> Unit = {},
    onCancelPayment: () -> Unit = {},
    onViewPaymentHistory: () -> Unit = {}
) {
    val paymentMethods = remember {
        listOf(
            // Using the correct drawable resources for the custom icons
            PaymentMethod("Airtel Money", R.drawable.airtelmoney),
            PaymentMethod("TNM Mpamba", R.drawable.tnm),
            PaymentMethod("Bank Transfer", Icons.Default.AccountBalance),
            PaymentMethod("Visa / MasterCard", R.drawable.imagesvisa)
        )
    }

    var selectedMethod by remember { mutableStateOf<PaymentMethod?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Complete Your Payment", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onCancelPayment) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // "View Payment History" is now a TextButton in the app bar
                    TextButton(onClick = onViewPaymentHistory) {
                        Text("History")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select a Payment Method",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // List of Payment Methods
            LazyColumn {
                items(paymentMethods) { method ->
                    PaymentMethodItem(
                        method = method,
                        isSelected = selectedMethod == method,
                        onSelected = { selectedMethod = method }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // Spacer pushes the buttons to the bottom of the screen
            Spacer(modifier = Modifier.weight(1f))

            // Action Buttons in a Column for better hierarchy
            Button(
                onClick = { selectedMethod?.let { onConfirmPayment(it) } },
                enabled = selectedMethod != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(48.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text("CONFIRM PAYMENT", style = MaterialTheme.typography.titleMedium)
            }
            OutlinedButton(
                onClick = onCancelPayment,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text("CANCEL")
            }
        }
    }
}

/**
 * A composable that displays a single selectable payment method item,
 * capable of rendering both vector and drawable icons.
 */
@Composable
fun PaymentMethodItem(
    method: PaymentMethod,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = isSelected, onClick = onSelected),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Use a when block to handle either a drawable resource or an ImageVector
            when (val icon = method.icon) {
                is Int -> Image(
                    painter = painterResource(id = icon),
                    contentDescription = "${method.name} logo",
                    modifier = Modifier.size(40.dp)
                )
                is ImageVector -> Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = method.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(1f)
            )
            RadioButton(
                selected = isSelected,
                onClick = onSelected
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PaymentScreenPreview() {
    MalawiRoadTraffficSafetySystemTheme {
        PaymentScreen()
    }
}
