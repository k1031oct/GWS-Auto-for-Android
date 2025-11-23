package com.gws.auto.mobile.android.ui.settings.app

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.gws.auto.mobile.android.R
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun AppSettingsScreen(
    settingsRepository: com.gws.auto.mobile.android.data.repository.SettingsRepository,
    historyRepository: com.gws.auto.mobile.android.data.repository.HistoryRepository,
    onNavigateToTags: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Section 1: Google Account
        GoogleAccountSection()
        
        Divider()
        
        // Section 2: Settings
        SettingsSection(settingsRepository = settingsRepository)
        
        Divider()
        
        // Section 3: Data Management
        DataManagementSection(
            onNavigateToTags = onNavigateToTags,
            historyRepository = historyRepository
        )
    }
}

@Composable
private fun GoogleAccountSection() {
    val context = LocalContext.current
    var isSignedIn by remember { mutableStateOf(false) }
    var accountName by remember { mutableStateOf<String?>(null) }
    var accountEmail by remember { mutableStateOf<String?>(null) }
    var accountPhotoUrl by remember { mutableStateOf<String?>(null) }
    
    // GoogleSignInClient
    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()
        GoogleSignIn.getClient(context, gso)
    }
    
    // Check sign-in status on composition
    LaunchedEffect(Unit) {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        isSignedIn = account != null
        accountName = account?.displayName
        accountEmail = account?.email
        accountPhotoUrl = account?.photoUrl?.toString()
    }
    
    // Sign-in launcher
    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                isSignedIn = true
                accountName = account.displayName
                accountEmail = account.email
                accountPhotoUrl = account.photoUrl?.toString()
                Toast.makeText(context, "Sign in successful", Toast.LENGTH_SHORT).show()
            } catch (e: ApiException) {
                Toast.makeText(context, "Sign in failed: ${e.statusCode}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Google Account
        Text(
            text = "Google Account",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        
        if (isSignedIn) {
            // Signed-in UI
            AccountCard(
                name = accountName ?: "Unknown",
                email = accountEmail ?: "",
                initial = accountName?.firstOrNull()?.uppercase() ?: "?"
            )
            
            Button(
                onClick = {
                    googleSignInClient.signOut().addOnCompleteListener {
                        isSignedIn = false
                        accountName = null
                        accountEmail = null
                        accountPhotoUrl = null
                        Toast.makeText(context, "Signed out", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign Out")
            }
        } else {
            // Not signed-in UI
            Text(
                text = "Sign in with your Google account to sync your data",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Button(
                onClick = {
                    val signInIntent = googleSignInClient.signInIntent
                    signInLauncher.launch(signInIntent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign In with Google")
            }
        }
    }
}

@Composable
private fun AccountCard(name: String, email: String, initial: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = initial,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Composable
private fun SettingsSection(
    settingsRepository: com.gws.auto.mobile.android.data.repository.SettingsRepository
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // State for each setting
    var firstDayOfWeek by remember { mutableStateOf("") }
    var holidayCountry by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("") }
    var theme by remember { mutableStateOf("") }
    var highlightColor by remember { mutableStateOf("") }
    
    // Load settings
    LaunchedEffect(Unit) {
        scope.launch {
            firstDayOfWeek = settingsRepository.firstDayOfWeek.first()
            holidayCountry = settingsRepository.holidayCountry.first()
            language = settingsRepository.language.first()
            theme = settingsRepository.theme.first()
            highlightColor = settingsRepository.highlightColor.first()
        }
    }

    
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        
        // First Day of Week
        SettingDropdown(
            label = "First Day of Week",
            options = context.resources.getStringArray(R.array.first_day_of_week_entries).toList(),
            selectedValue = firstDayOfWeek,
            onSelectionChanged = { value ->
                firstDayOfWeek = value
                scope.launch { settingsRepository.saveFirstDayOfWeek(value) }
            }
        )
        
        // Holiday Country
        val countryEntries = context.resources.getStringArray(R.array.country_entries).toList()
        val countryValues = context.resources.getStringArray(R.array.country_values).toList()
        SettingDropdown(
            label = "Holiday Country",
            options = countryEntries,
            selectedValue = holidayCountry,
            valueMapper = countryValues,
            onSelectionChanged = { value ->
                holidayCountry = value
                scope.launch { settingsRepository.saveHolidayCountry(value) }
            }
        )
        
        // Language  
        val languageValues = context.resources.getStringArray(R.array.language_values_from_countries).toList()
        SettingDropdown(
            label = "Language",
            options = countryEntries, // Reuse country entries for language
            selectedValue = language,
            valueMapper = languageValues,
            onSelectionChanged = { value ->
                language = value
                scope.launch { settingsRepository.saveLanguage(value) }
            }
        )
        
        // Theme
        val themeEntries = context.resources.getStringArray(R.array.theme_entries).toList()
        val themeValues = context.resources.getStringArray(R.array.theme_values).toList()
        SettingDropdown(
            label = "Theme",
            options = themeEntries,
            selectedValue = theme,
            valueMapper = themeValues,
            onSelectionChanged = { value ->
                theme = value
                scope.launch { settingsRepository.saveTheme(value) }
            }
        )
        
        // Highlight Color with indicator
        val highlightColorEntries = context.resources.getStringArray(R.array.highlight_color_entries).toList()
        val highlightColorValues = context.resources.getStringArray(R.array.highlight_color_values).toList()
        Column {
            SettingDropdown(
                label = "Highlight Color",
                options = highlightColorEntries,
                selectedValue = highlightColor,
                valueMapper = highlightColorValues,
                onSelectionChanged = { value ->
                    highlightColor = value
                    scope.launch { settingsRepository.saveHighlightColor(value) }
                }
            )
            
            // Color indicator
            val indicatorColor = when(highlightColor) {
                "forest" -> Color(0xFF386A1F)
                "ocean" -> Color(0xFF00696F)
                "sakura" -> Color(0xFFB14E69)
                "neon" -> Color(0xFFDFFF00)
                else -> Color(0xFF9E9E9E) // default (Gray)
            }
            Surface(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(width = 100.dp, height = 30.dp),
                color = indicatorColor,
                shape = RoundedCornerShape(4.dp)
            ) {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingDropdown(
    label: String,
    options: List<String>,
    selectedValue: String,
    valueMapper: List<String>? = null,
    onSelectionChanged: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    // Find display value
    val displayValue = if (valueMapper != null) {
        val index = valueMapper.indexOf(selectedValue)
        if (index >= 0 && index < options.size) options[index] else options.firstOrNull() ?: ""
    } else {
        selectedValue
    }
    
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = displayValue,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )
            
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                options.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            val actualValue = valueMapper?.getOrNull(index) ?: option
                            onSelectionChanged(actualValue)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


@Composable
private fun DataManagementSection(
    onNavigateToTags: () -> Unit,
    historyRepository: com.gws.auto.mobile.android.data.repository.HistoryRepository
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showClearHistoryDialog by remember { mutableStateOf(false) }
    
    // CSV Export launcher
    val csvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let {
            scope.launch {
                try {
                    val histories = historyRepository.getAllHistory().first()
                    val csvContent = buildCsvContent(histories)
                    
                    context.contentResolver.openOutputStream(it)?.use { outputStream ->
                        outputStream.write(csvContent.toByteArray())
                    }
                    Toast.makeText(context, "History exported successfully", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Data Management",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        
        // CSV Export Button
        Button(
            onClick = {
                val timestamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault())
                    .format(java.util.Date())
                csvLauncher.launch("history_export_$timestamp.csv")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Export History to CSV")
        }
        
        // Clear History Button
        OutlinedButton(
            onClick = { showClearHistoryDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Clear All History")
        }
        
        // Tag Management Button
        OutlinedButton(
            onClick = onNavigateToTags,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Manage Tags")
        }
    }
    
    // Clear History Confirmation Dialog
    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("Clear All History") },
            text = { Text("Are you sure you want to delete all execution history? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            historyRepository.deleteAllHistory()
                            Toast.makeText(context, "All history has been cleared", Toast.LENGTH_SHORT).show()
                        }
                        showClearHistoryDialog = false
                    }
                ) {
                    Text("Clear All", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun buildCsvContent(histories: List<com.gws.auto.mobile.android.domain.model.History>): String {
    val header = "ID,Workflow ID,Workflow Name,Executed At,Status,Duration (ms),Logs\n"
    val rows = histories.joinToString("\n") { history ->
        "$ {history.id},\"${history.workflowId}\",\"${history.workflowName}\",${history.executedAt.time},${history.status},${history.durationMs},\"${history.logs.replace("\"", "\"\"")}\""
    }
    return header + rows
}


