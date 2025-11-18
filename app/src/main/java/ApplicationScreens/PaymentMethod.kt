package ApplicationScreens

import ApplicationScreens.viewmodels.PaymentState
import ApplicationScreens.viewmodels.PaymentViewModel
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
    viewModel: PaymentViewModel = viewModel(),
    onConfirmPayment: () -> Unit = {},
    onCancelPayment: () -> Unit = {},
    onViewPaymentHistory: () -> Unit = {}
) {
    val context = LocalContext.current
    val paymentState by viewModel.paymentState.collectAsState()
    var phoneNumber by remember { mutableStateOf("") }

    val paymentMethods = remember {
        listOf(
            PaymentMethod("Airtel Money", R.drawable.airtelmoney),
            PaymentMethod("TNM Mpamba", R.drawable.tnm),
            PaymentMethod("Bank Transfer", Icons.Default.AccountBalance),
            PaymentMethod("Visa / MasterCard", R.drawable.imagesvisa)
        )
    }

    var selectedMethod by remember { mutableStateOf<PaymentMethod?>(null) }

    LaunchedEffect(paymentState) {
        when (val state = paymentState) {
            is PaymentState.Success -> {
                Toast.makeText(context, "Payment Successful!", Toast.LENGTH_LONG).show()
                viewModel.resetState()
                onConfirmPayment()
            }
            is PaymentState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> Unit
        }
    }

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
                    TextButton(onClick = onViewPaymentHistory) {
                        Text("History")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select a Payment Method",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(paymentMethods) { method ->
                        PaymentMethodItem(
                            method = method,
                            isSelected = selectedMethod == method,
                            onSelected = { selectedMethod = method }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (selectedMethod != null) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Enter Payment Details", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = phoneNumber,
                                onValueChange = { phoneNumber = it },
                                label = { Text(if (selectedMethod?.name?.contains("Visa") == true) "Card Number" else "Phone Number") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        selectedMethod?.let { method ->
                            if (phoneNumber.isNotBlank()) {
                                // Simulating an amount of 15,000 MWK for now
                                viewModel.processPayment("15000", phoneNumber, method.name)
                            } else {
                                Toast.makeText(context, "Please enter your details", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    enabled = selectedMethod != null && paymentState != PaymentState.Processing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .height(48.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(if (paymentState == PaymentState.Processing) "PROCESSING..." else "CONFIRM PAYMENT")
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

            if (paymentState == PaymentState.Processing) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

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
        PaymentScreen(onConfirmPayment = {})
    }
}
