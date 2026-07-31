package com.towfikedutips.app.ui.screen

import androidx.compose.foundation.background
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: String = "Now"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var inputQuery by remember { mutableStateOf("") }
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
        if (queryText.isBlank()) return
        messages.add(ChatMessage(queryText, isUser = true))
        inputQuery = ""

        coroutineScope.launch {
            delay(1000)
            val replyText = when {
                queryText.contains("History", ignoreCase = true) -> {
                    "**🏆 Madhyamik History 2026 High-Yield Questions:**\n\n1. **ভারতের মহাবিদ্রোহ (১৮৫৭):** মহাবিদ্রোহের প্রকৃতি ও চরিত্র আলোচনা করো। (৮ নম্বর)\n2. **শিক্ষা সংস্কার:** উনিশ শতকে বাংলায় নারী শিক্ষা বিস্তারে ঈশ্বরচন্দ্র বিদ্যাসাগরের অবদান লেখো। (৮ নম্বর)\n3. **মহাবিদ্রোহে হিন্দু-মুসলিম ঐক্য:** বিদ্রোহে দুই সম্প্রদায়ের মেলবন্ধনের গুরুত্ব।"
                }
                queryText.contains("Physical Science", ignoreCase = true) -> {
                    "**⚗️ Physical Science Important Formulas & Key Tips:**\n\n- **বয়েলের সূত্র (Boyle's Law):** ${'$'}P_1V_1 = P_2V_2${'$'} (স্থির তাপমাত্রায় নির্দিষ্ট ভরের গ্যাসের ক্ষেত্রে)\n- **চার্লসের সূত্র (Charles's Law):** ${'$'}V/T = \\text{constant}${'$'} or ${'$'}V_1/T_1 = V_2/T_2${'$'}\n- **আদর্শ গ্যাস সমীকরণ:** ${'$'}PV = nRT${'$'}"
                }
                queryText.contains("Life Science", ignoreCase = true) -> {
                    "**🌿 Life Science Diagram-Based Questions:**\n\n1. একটি আদর্শ নিউরনের (Neuron) পরিচ্ছন্ন চিত্র অঙ্কন করে নিম্নলিখিত অংশগুলি চিহ্নিত করো: অ্যাক্সন, ডেনড্রন, মায়েলিন সিথ, র্যানভিয়ারের পর্ব।\n2. সপুষ্পক উদ্ভিদের দ্বিনিষেক প্রক্রিয়ার চিত্র অঙ্কন।"
                }
                queryText.contains("Mathematics", ignoreCase = true) -> {
                    "**📐 Mathematics Important Geometry Theorems (Class 10 WBBSE):**\n\n1. **উপপাদ্য ৩৮:** বৃত্তস্থ চতুর্ভুজের বিপরীত কোণগুলি পরস্পর সম্পূরক।\n2. **উপপাদ্য ৪১:** বৃত্তের বহিঃস্থ কোনো বিন্দু থেকে যে দুটি স্পর্শক অঙ্কন করা যায়, তাদের দৈর্ঘ্য সমান।"
                }
                else -> {
                    "নমস্কার! I have received your question regarding \"$queryText\". As your expert Class 10 Madhyamik study tutor, I suggest focusing on WBBSE past ten years' solved board questions and textbook chapter summaries. Keep preparing hard!"
                }
            }
            messages.add(ChatMessage(replyText, isUser = false))
            delay(100)
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
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Towfik AI Tutor",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Class 10 Smart Assistant",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Preset Suggestion Chips Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
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
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { msg ->
                    val alignment = if (msg.isUser) Alignment.End else Alignment.Start
                    val containerColor = if (msg.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                    val contentColor = if (msg.isUser) Color.White else MaterialTheme.colorScheme.onSurface

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
            }

            // Chat input bar
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
