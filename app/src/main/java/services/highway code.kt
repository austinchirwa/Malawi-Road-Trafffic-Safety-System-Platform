package services

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.malawiroadtraffficsafetysystem.ui.theme.MalawiRoadTraffficSafetySystemTheme
import java.io.File
import java.io.FileOutputStream

/**
 * Data class for a single safety rule.
 */
data class SafetyRule(
    val title: String,
    val description: String
)

/**
 * A screen displaying essential safety rules from the Malawi Highway Code.
 * It also provides an option to download the full document.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HighwayCodeScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val safetyRules = remember {
        listOf(
            SafetyRule("Speed Limits", "Observe prescribed speed limits. In built-up areas, the general limit is 60 km/h unless otherwise signposted. On highways, it is 100 km/h for light vehicles."),
            SafetyRule("Overtaking", "Only overtake when it is safe to do so. Do not overtake at junctions, pedestrian crossings, or where you see a solid white line on your side of the road."),
            SafetyRule("Seatbelts", "All occupants of a vehicle must wear a seatbelt where one is fitted."),
            SafetyRule("Use of Mobile Phones", "It is illegal to hold and use a mobile phone while driving."),
            SafetyRule("Driving Under the Influence", "Do not drive if you have consumed alcohol or drugs. Driving under the influence is a serious offense.")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Highway Code") },
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SectionHeader("Essential Safety Rules")
                // Rules are now compacted into a single card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        safetyRules.forEachIndexed { index, rule ->
                            RuleItem(rule)
                            if (index < safetyRules.lastIndex) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader("Full Document")
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { openPdfFromAssets(context, "highway_code.pdf") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Download, contentDescription = "Download")
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text("View Highway Code (PDF)")
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

/**
 * Copies a PDF from the assets folder to the cache and opens it with a PDF viewer.
 */
private fun openPdfFromAssets(context: Context, assetName: String) {
    val file = File(context.cacheDir, assetName)

    // --- IMPORTANT SETUP --- //
    // To make this work, you need to configure a FileProvider.
    // 1. Create a file `app/src/main/res/xml/file_paths.xml` with this content:
    //    <paths>
    //        <cache-path name="cached_files" path="." />
    //    </paths>
    //
    // 2. Add this provider to your `AndroidManifest.xml` inside the <application> tag:
    //    <provider
    //        android:name="androidx.core.content.FileProvider"
    //        android:authorities="${applicationId}.provider"
    //        android:exported="false"
    //        android:grantUriPermissions="true">
    //        <meta-data
    //            android:name="android.support.FILE_PROVIDER_PATHS"
    //            android:resource="@xml/file_paths" />
    //    </provider>

    try {
        // Copy file from assets to a temporary cache file
        context.assets.open(assetName).use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        // Get a content URI for the file using the FileProvider
        val uri = FileProvider.getUriForFile(
            context,
            context.packageName + ".provider", // Matches the authority in the manifest
            file
        )

        // Create an Intent to view the PDF
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(intent)

    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No PDF viewer app found.", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        // Handles other exceptions, like if the PDF is not in the assets folder
        Toast.makeText(context, "Error opening PDF: ${e.message}", Toast.LENGTH_LONG).show()
    }
}


@Composable
private fun RuleItem(rule: SafetyRule) {
    Column {
        Text(
            text = rule.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = rule.description,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HighwayCodeScreenPreview() {
    MalawiRoadTraffficSafetySystemTheme {
        HighwayCodeScreen(onBackClick = {})
    }
}
