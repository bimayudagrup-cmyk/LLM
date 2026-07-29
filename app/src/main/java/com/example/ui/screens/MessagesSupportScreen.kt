package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.LmsViewModel

@Composable
fun MessagesSupportScreen(
    viewModel: LmsViewModel
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val messages by viewModel.supportMessages.collectAsState()

    var subjectInput by remember { mutableStateOf("") }
    var messageInput by remember { mutableStateOf("") }

    // Admin Reply Dialog State
    var selectedMessageIdForReply by remember { mutableStateOf<Int?>(null) }
    var replyInput by remember { mutableStateOf("") }

    val userRole = currentUser?.role ?: "SISWA"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Text(
                text = "Pesan & Bantuan Support LMS",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Layanan Konsultasi & Bantuan Kendala LMS Lembaga Asesmen Profesional Indonesia",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Form Send Message
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Kirim Pesan Bantuan Baru",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = subjectInput,
                        onValueChange = { subjectInput = it },
                        label = { Text("Subjek Bantuan / Judul Kendala") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("support_subject_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = messageInput,
                        onValueChange = { messageInput = it },
                        label = { Text("Isi Pesan Pertanyaan / Kendala") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("support_message_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (subjectInput.isNotBlank() && messageInput.isNotBlank()) {
                                viewModel.sendSupportMessage(subjectInput, messageInput)
                                subjectInput = ""
                                messageInput = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("send_support_message_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("KIRIM PESAN BANTUAN", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Daftar Tiket & Diskusi Bantuan",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(messages) { msg ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "${msg.senderName} (${msg.senderRole})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (msg.isResolved) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                        ) {
                            Text(
                                text = if (msg.isResolved) "SELESAI" else "PENDING",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Subjek: ${msg.subject}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = msg.message, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    if (msg.reply.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.SupportAgent, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Tanggapan Tim Admin API: ${msg.reply}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                        }
                    } else if (userRole == "ADMIN") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                selectedMessageIdForReply = msg.id
                                replyInput = ""
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("reply_message_button_${msg.id}")
                        ) {
                            Text("Tanggapi Pesan (Admin)", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    selectedMessageIdForReply?.let { msgId ->
        AlertDialog(
            onDismissRequest = { selectedMessageIdForReply = null },
            title = { Text("Tanggapi Tiket Bantuan", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = replyInput,
                    onValueChange = { replyInput = it },
                    label = { Text("Isi Balasan Admin") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.replySupportMessage(msgId, replyInput)
                        selectedMessageIdForReply = null
                    }
                ) {
                    Text("Kirim Balasan")
                }
            }
        )
    }
}
