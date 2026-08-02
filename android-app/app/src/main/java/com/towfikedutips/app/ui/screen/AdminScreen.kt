package com.towfikedutips.app.ui.screen

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.navigation.NavController
import com.towfikedutips.app.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(navController: NavController) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Authorization", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Towfik Edutips",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "System Portal Management",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (username == "admin" && password == "towfik2026") {
                        Toast.makeText(context, "Welcome Admin!", Toast.LENGTH_SHORT).show()
                        navController.navigate(Screen.AdminDashboard.route) {
                            popUpTo(Screen.Admin.route) { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, "Invalid username or password!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Login to Dashboard", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(navController: NavController) {
    val context = LocalContext.current
    var activeSubTab by remember { mutableStateOf("alerts") }

    // State for Notifications
    var pushTitle by remember { mutableStateOf("WBBSE Madhyamik 2026 Update") }
    var pushMessage by remember { mutableStateOf("New Physics formulas and math model papers are now uploaded!") }

    // State for Config Settings
    var appName by remember { mutableStateOf("Towfik Edutips") }
    var noticeBanner by remember { mutableStateOf("WBBSE Madhyamik 2026 Suggestions are fully active.") }
    var contactPhone by remember { mutableStateOf("9609881733") }
    var contactEmail by remember { mutableStateOf("support@towfikedutips.com") }

    // State for Banner Management
    val promoBanners = remember {
        mutableStateListOf(
            "Madhyamik 2026 Final Suggestion Set",
            "Complete Chapter PDFs & Solved Model Papers",
            "Subjectwise MCQ Test & Rapid Revision"
        )
    }
    var newBannerTitle by remember { mutableStateOf("") }

    // State for Subjects/Chapters configuration
    val classSubjects = remember {
        mutableStateListOf(
            "Bengali (বাংলা)",
            "English",
            "Mathematics (গণিত)",
            "Physical Science (ভৌতবিজ্ঞান)",
            "Life Science (জীবনবিজ্ঞান)",
            "History (ইতিহাস)",
            "Geography (ভূগোল)"
        )
    }
    var newSubjectName by remember { mutableStateOf("") }

    fun sendLocalPushNotification(title: String, body: String) {
        val channelId = "towfik_push_channel"
        val notificationId = 101

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Study Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Class 10 Revision suggestions and notices."
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Management Console", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Horizontal Admin tabs navigation selection
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = activeSubTab == "alerts",
                        onClick = { activeSubTab = "alerts" },
                        label = { Text("Push Alerts", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }
                item {
                    FilterChip(
                        selected = activeSubTab == "settings",
                        onClick = { activeSubTab = "settings" },
                        label = { Text("Settings", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }
                item {
                    FilterChip(
                        selected = activeSubTab == "banners",
                        onClick = { activeSubTab = "banners" },
                        label = { Text("Banners Slider", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }
                item {
                    FilterChip(
                        selected = activeSubTab == "syllabus",
                        onClick = { activeSubTab = "syllabus" },
                        label = { Text("Syllabus Manager", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (activeSubTab) {
                    "alerts" -> {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Broadcasting Push Alerts", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = pushTitle,
                                        onValueChange = { pushTitle = it },
                                        label = { Text("Alert Title", fontSize = 12.sp) },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = pushMessage,
                                        onValueChange = { pushMessage = it },
                                        label = { Text("Alert Message Content", fontSize = 12.sp) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = {
                                            sendLocalPushNotification(pushTitle, pushMessage)
                                            Toast.makeText(context, "Push Alert broadcasted successfully!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Broadcast Alert to Students", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }

                    "settings" -> {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Edit Settings Configuration", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = appName,
                                        onValueChange = { appName = it },
                                        label = { Text("Portal Title", fontSize = 12.sp) },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = noticeBanner,
                                        onValueChange = { noticeBanner = it },
                                        label = { Text("Global Notice Bar", fontSize = 12.sp) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = contactPhone,
                                        onValueChange = { contactPhone = it },
                                        label = { Text("Contact WhatsApp / Mobile", fontSize = 12.sp) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = contactEmail,
                                        onValueChange = { contactEmail = it },
                                        label = { Text("Support Email Address", fontSize = 12.sp) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = {
                                            Toast.makeText(context, "Settings updated successfully!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Save Changes", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }

                    "banners" -> {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Manage Promotional Banners", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = newBannerTitle,
                                        onValueChange = { newBannerTitle = it },
                                        label = { Text("New Banner Title", fontSize = 12.sp) },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            if (newBannerTitle.isNotBlank()) {
                                                promoBanners.add(0, newBannerTitle)
                                                newBannerTitle = ""
                                                Toast.makeText(context, "Banner Added!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Add Promotional Banner", fontSize = 12.sp)
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text("Current Banners List:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    promoBanners.forEach { banner ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = banner, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                            IconButton(onClick = { promoBanners.remove(banner) }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                            }
                                        }
                                        Divider()
                                    }
                                }
                            }
                        }
                    }

                    "syllabus" -> {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Book, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Syllabus & Subject Manager", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = newSubjectName,
                                        onValueChange = { newSubjectName = it },
                                        label = { Text("New Subject Title", fontSize = 12.sp) },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            if (newSubjectName.isNotBlank()) {
                                                classSubjects.add(newSubjectName)
                                                newSubjectName = ""
                                                Toast.makeText(context, "Subject Added!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Add New Subject", fontSize = 12.sp)
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text("Current Subjects List:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    classSubjects.forEach { sub ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = sub, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                            IconButton(onClick = { classSubjects.remove(sub) }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                            }
                                        }
                                        Divider()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
