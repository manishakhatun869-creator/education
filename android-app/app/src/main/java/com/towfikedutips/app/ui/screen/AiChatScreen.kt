package com.towfikedutips.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: String = "Now"
)

// Call official Gemini API securely through the server-side /api/chat proxy to keep API Key secure and prevent PERMISSION_DENIED blocks
suspend fun callGeminiApi(prompt: String, baseUrl: String): String = withContext(Dispatchers.IO) {
    var connection: HttpURLConnection? = null
    try {
        val cleanUrl = baseUrl.removeSuffix("/")
        val url = URL("$cleanUrl/api/chat")
        connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.doOutput = true

        val requestBody = JSONObject()
        requestBody.put("message", prompt)
        requestBody.put("history", JSONArray())

        val writer = OutputStreamWriter(connection.outputStream)
        writer.write(requestBody.toString())
        writer.flush()
        writer.close()

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val responseString = connection.inputStream.bufferedReader().use { it.readText() }
            val jsonResponse = JSONObject(responseString)
            return@withContext jsonResponse.optString("reply", "Sorry, I could not generate a response. Please try again.")
        } else {
            val errorResponse = connection.errorStream?.bufferedReader()?.use { it.readText() }
            if (!errorResponse.isNullOrBlank()) {
                val errJson = JSONObject(errorResponse)
                return@withContext errJson.optString("error", "Error Code $responseCode from AI Tutor backend.")
            }
        }
        return@withContext "Sorry, I am facing a connection issue with port 3000. Please make sure the local dev server is running or configure the server URL."
    } catch (e: Exception) {
        e.printStackTrace()
        return@withContext "Apologies! As your WBBSE Madhyamik tutor, I am temporarily having trouble reaching the knowledgebase. Please try again soon."
    } finally {
        connection?.disconnect()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var inputQuery by remember { mutableStateOf("") }
    var isThinking by remember { mutableStateOf(false) }

    val firestore = remember { com.towfikedutips.app.data.FirestoreProvider.getFirestore(context) }
    var backendApiUrl by remember { mutableStateOf("http://10.0.2.2:3000") }

    LaunchedEffect(Unit) {
        firestore.collection("settings").get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val doc = querySnapshot.documents[0]
                backendApiUrl = doc.getString("backendUrl") ?: "http://10.0.2.2:3000"
            }
        }
    }

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                text = "নমস্কার! I am Towfik Edutips AI Tutor. How can I help you with your WBBSE Madhyamik Class 10 preparation today? Ask me any subject question, formula explanation, or suggestion guidelines!",
                isUser = false
            )
        )
    }

    val presetQueries = listOf(
        "🏆 History 8-mark suggestion questions for 2026",
        "⚗️ Physical Science important formulas",
        "🌿 Life Science diagram based questions",
        "📐 Mathematics geometry theorems list for WBBSE"
    )

    fun onSendMessage(queryText: String) {
        if (queryText.isBlank() || isThinking) return
        messages.add(ChatMessage(queryText, isUser = true))
        inputQuery = ""
        isThinking = true

        coroutineScope.launch {
            // Scroll to the user message
            listState.animateScrollToItem(messages.size - 1)

            // Securely call official Gemini API through server-side proxy
            val replyText = callGeminiApi(queryText, backendApiUrl)

            messages.add(ChatMessage(replyText, isUser = false))
            isThinking = false

            // Scroll to the bot's response
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Towfik AI Tutor",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Class 10 Smart Assistant",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White) // crisp white background
                .padding(paddingValues)
        ) {
            // Preset Suggestion Chips Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(presetQueries) { query ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .clickable { onSendMessage(query) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = query,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Message Area
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC)) // Textbook off-white tint
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { msg ->
                    val alignment = if (msg.isUser) Alignment.End else Alignment.Start
                    val containerColor = if (msg.isUser) MaterialTheme.colorScheme.primary else Color.White
                    val contentColor = if (msg.isUser) Color.White else Color.Black
                    val bubbleBorderModifier = if (msg.isUser) Modifier else Modifier.border(0.5.dp, Color.LightGray, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomEnd = 12.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = alignment
                    ) {
                        Box(
                            modifier = Modifier
                                .widthIn(max = 280.dp)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 12.dp,
                                        topEnd = 12.dp,
                                        bottomStart = if (msg.isUser) 12.dp else 0.dp,
                                        bottomEnd = if (msg.isUser) 0.dp else 12.dp
                                    )
                                )
                                .background(containerColor)
                                .then(bubbleBorderModifier)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = msg.text,
                                color = contentColor,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                if (isThinking) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Towfik AI is typing...",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Chat input bar
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = inputQuery,
                        onValueChange = { inputQuery = it },
                        placeholder = { Text("Ask anything (e.g. ইতিহাস সাজেশন)...", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    IconButton(
                        onClick = { onSendMessage(inputQuery) },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.size(44.dp),
                        enabled = inputQuery.isNotBlank() && !isThinking
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
