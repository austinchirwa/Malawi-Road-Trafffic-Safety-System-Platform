package ApplicationScreens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class PaymentState {
    object Idle : PaymentState()
    object Processing : PaymentState()
    object Success : PaymentState()
    data class Error(val message: String) : PaymentState()
}

class PaymentViewModel : ViewModel() {

    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState

    fun processPayment(amount: String, phoneNumber: String, provider: String) {
        viewModelScope.launch {
            _paymentState.value = PaymentState.Processing
            try {
                // Simulate a network delay for payment processing
                delay(3000)

                if (phoneNumber.isNotBlank() && amount.toDoubleOrNull() != null) {
                    // In a real app, you would call your payment gateway API here
                    _paymentState.value = PaymentState.Success
                } else {
                    _paymentState.value = PaymentState.Error("Invalid payment details.")
                }
            } catch (e: Exception) {
                _paymentState.value = PaymentState.Error("Payment failed: ${e.message}")
            }
        }
    }

    fun resetState() {
        _paymentState.value = PaymentState.Idle
    }
}
