package ApplicationScreens.utils

import android.util.Patterns

/**
 * Utility object for form validation
 */
object ValidationUtils {
    
    /**
     * Validate email format
     */
    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    /**
     * Validate phone number (Malawi format)
     * Accepts formats: +265xxxxxxxxx, 265xxxxxxxxx, 0xxxxxxxxx, xxxxxxxxx
     */
    fun isValidPhoneNumber(phone: String): Boolean {
        val cleanedPhone = phone.replace(Regex("[\\s-]"), "")
        
        return when {
            cleanedPhone.matches(Regex("^\\+265[0-9]{9}$")) -> true // +265xxxxxxxxx
            cleanedPhone.matches(Regex("^265[0-9]{9}$")) -> true    // 265xxxxxxxxx
            cleanedPhone.matches(Regex("^0[0-9]{9}$")) -> true      // 0xxxxxxxxx
            cleanedPhone.matches(Regex("^[0-9]{9}$")) -> true       // xxxxxxxxx
            else -> false
        }
    }
    
    /**
     * Validate National ID (Malawi format)
     * Format: XX-XXXXXX-X-XX (e.g., MN-123456-7-89)
     */
    fun isValidNationalId(nationalId: String): Boolean {
        // Remove spaces and convert to uppercase
        val cleanedId = nationalId.replace(" ", "").uppercase()
        
        // Check format: 2 letters, hyphen, 6 digits, hyphen, 1 digit, hyphen, 2 digits
        return cleanedId.matches(Regex("^[A-Z]{2}-[0-9]{6}-[0-9]{1}-[0-9]{2}$"))
    }
    
    /**
     * Validate date format (DD/MM/YYYY)
     */
    fun isValidDate(date: String): Boolean {
        if (date.isBlank()) return false
        
        val parts = date.split("/")
        if (parts.size != 3) return false
        
        val day = parts[0].toIntOrNull() ?: return false
        val month = parts[1].toIntOrNull() ?: return false
        val year = parts[2].toIntOrNull() ?: return false
        
        return day in 1..31 && month in 1..12 && year in 1900..2100
    }
    
    /**
     * Validate age (must be at least 18 years old)
     */
    fun isValidAge(dateOfBirth: String): Boolean {
        if (!isValidDate(dateOfBirth)) return false
        
        val parts = dateOfBirth.split("/")
        val day = parts[0].toInt()
        val month = parts[1].toInt()
        val year = parts[2].toInt()
        
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        val currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1
        val currentDay = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH)
        
        var age = currentYear - year
        
        if (currentMonth < month || (currentMonth == month && currentDay < day)) {
            age--
        }
        
        return age >= 18
    }
    
    /**
     * Validate name (only letters and spaces)
     */
    fun isValidName(name: String): Boolean {
        return name.isNotBlank() && name.matches(Regex("^[a-zA-Z\\s]+$"))
    }
    
    /**
     * Validate required field
     */
    fun isNotEmpty(value: String): Boolean {
        return value.isNotBlank()
    }
    
    /**
     * Validate password strength
     * Must be at least 8 characters with uppercase, lowercase, and numbers
     */
    fun isValidPassword(password: String): Boolean {
        return password.length >= 8 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { it.isDigit() }
    }
    
    /**
     * Validate vehicle plate number (Malawi format)
     * Format: XX 1234 or XXX 1234
     */
    fun isValidPlateNumber(plateNumber: String): Boolean {
        val cleaned = plateNumber.replace(" ", "").uppercase()
        return cleaned.matches(Regex("^[A-Z]{2,3}[0-9]{4}$"))
    }
    
    /**
     * Get error message for email validation
     */
    fun getEmailError(email: String): String? {
        return when {
            email.isBlank() -> "Email is required"
            !isValidEmail(email) -> "Invalid email format"
            else -> null
        }
    }
    
    /**
     * Get error message for phone validation
     */
    fun getPhoneError(phone: String): String? {
        return when {
            phone.isBlank() -> "Phone number is required"
            !isValidPhoneNumber(phone) -> "Invalid phone number format"
            else -> null
        }
    }
    
    /**
     * Get error message for national ID validation
     */
    fun getNationalIdError(nationalId: String): String? {
        return when {
            nationalId.isBlank() -> "National ID is required"
            !isValidNationalId(nationalId) -> "Invalid National ID format (XX-XXXXXX-X-XX)"
            else -> null
        }
    }
    
    /**
     * Get error message for date validation
     */
    fun getDateError(date: String): String? {
        return when {
            date.isBlank() -> "Date is required"
            !isValidDate(date) -> "Invalid date format (DD/MM/YYYY)"
            else -> null
        }
    }
    
    /**
     * Get error message for age validation
     */
    fun getAgeError(dateOfBirth: String): String? {
        return when {
            dateOfBirth.isBlank() -> "Date of birth is required"
            !isValidDate(dateOfBirth) -> "Invalid date format (DD/MM/YYYY)"
            !isValidAge(dateOfBirth) -> "You must be at least 18 years old"
            else -> null
        }
    }
    
    /**
     * Get error message for name validation
     */
    fun getNameError(name: String): String? {
        return when {
            name.isBlank() -> "Name is required"
            !isValidName(name) -> "Name should only contain letters and spaces"
            else -> null
        }
    }
}
