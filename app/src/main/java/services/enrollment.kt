package services

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.malawiroadtraffficsafetysystem.ui.theme.MalawiRoadTraffficSafetySystemTheme

/**
 * Data class to hold information about a driving school.
 */
data class DrivingSchool(
    val name: String,
    val location: String,
    val price: String
)

/**
 * A screen that displays a list of driving schools with options to enroll.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnrollmentScreen(onBackClick: () -> Unit, onEnrollClick: (DrivingSchool) -> Unit) {
    val schools = remember {
        listOf(
            DrivingSchool("Prestige Driving School", "Blantyre, Limbe", "MWK 150,000"),
            DrivingSchool("Central Driving School", "Lilongwe, Area 47", "MWK 180,000"),
            DrivingSchool("Mzuzu Moto Trainers", "Mzuzu, Katoto", "MWK 135,000"),
            DrivingSchool("Zomba Safe Drivers", "Zomba, Matawale", "MWK 160,000"),
            DrivingSchool("Capital City Drivers", "Lilongwe, Old Town", "MWK 175,000"),
            DrivingSchool("Lakeshore Driving Academy", "Mangochi, Town", "MWK 190,000")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Driving School Enrollment", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Choose a driving school that suits your needs. Prices are for a full beginner course.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(schools) { school ->
                DrivingSchoolCard(school = school, onEnrollClick = { onEnrollClick(school) })
            }
        }
    }
}

/**
 * An enhanced card that displays information about a single driving school.
 */
@Composable
fun DrivingSchoolCard(school: DrivingSchool, onEnrollClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.School,
                    contentDescription = "Driving School Icon",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = school.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Location: ${school.location}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Price: ${school.price}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onEnrollClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("ENROLL NOW")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EnrollmentScreenPreview() {
    MalawiRoadTraffficSafetySystemTheme {
        EnrollmentScreen(onBackClick = {}, onEnrollClick = {})
    }
}
