package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.viewmodel.LmsViewModel
import com.example.ui.viewmodel.OtpFlowState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginRegisterScreen(
    viewModel: LmsViewModel,
    onAuthSuccess: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Login, 1 = Register
    val authError by viewModel.authError.collectAsState()
    val authSuccess by viewModel.authSuccess.collectAsState()
    val otpState by viewModel.otpState.collectAsState()
    val requires2FA by viewModel.requires2FA.collectAsState()

    // Login Form State
    var loginIdentifier by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginPasswordVisible by remember { mutableStateOf(false) }

    // 2FA Verification Dialog State
    var input2FACode by remember { mutableStateOf("") }

    // Register Form State
    var regName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regPasswordVisible by remember { mutableStateOf(false) }
    var regRole by remember { mutableStateOf("SISWA") } // "SISWA", "INSTRUKTUR", "ADMIN"
    var regProgram by remember { mutableStateOf("Sertifikasi Asesor Kompetensi") }
    var regSpecialReq by remember { mutableStateOf("") } // Admin Secret Code or Instructor Specialty
    var regOtpCodeInput by remember { mutableStateOf("") }
    var isOtpSentLocal by remember { mutableStateOf(false) }
    var generatedOtpLocal by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Header Branding
            Image(
                painter = painterResource(id = R.drawable.img_lms_logo_1785305469960),
                contentDescription = "Logo Asesmen Profesional Indonesia",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Asesmen Profesional Indonesia",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = "LMS Portofolio, Sertifikasi & Uji Kompetensi Terpadu",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Alert messages
            AnimatedVisibility(visible = authError != null) {
                authError?.let { err ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = err, color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 12.sp)
                        }
                    }
                }
            }

            AnimatedVisibility(visible = authSuccess != null) {
                authSuccess?.let { succ ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = succ, color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Tab Selector (Masuk / Daftar)
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0; viewModel.clearAuthMessages() },
                    text = { Text("MASUK (LOGIN)", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_login")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1; viewModel.clearAuthMessages() },
                    text = { Text("DAFTAR (REGISTER)", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_register")
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (selectedTab == 0) {
                // LOGIN FORM
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Masuk ke Akun LMS",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Gunakan Email atau Nomor Telepon yang terdaftar",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = loginIdentifier,
                            onValueChange = { loginIdentifier = it },
                            label = { Text("Email atau No. Telepon") },
                            placeholder = { Text("Contoh: siswa@asesmen.id atau 08123456789") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_identifier_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = loginPassword,
                            onValueChange = { loginPassword = it },
                            label = { Text("Kata Sandi (Password)") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { loginPasswordVisible = !loginPasswordVisible }) {
                                    Icon(
                                        imageVector = if (loginPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null
                                    )
                                }
                            },
                            visualTransformation = if (loginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_password_input")
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                viewModel.login(
                                    identifier = loginIdentifier,
                                    password = loginPassword,
                                    on2FARequired = {},
                                    onSuccess = onAuthSuccess
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("login_submit_button")
                        ) {
                            Icon(imageVector = Icons.Default.Login, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("MASUK LMS", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        HorizontalDivider()

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Akun Demo Cepat:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(onClick = {
                                loginIdentifier = "siswa@asesmen.id"
                                loginPassword = "siswa123"
                            }) {
                                Text("Siswa Demo", fontSize = 11.sp)
                            }
                            TextButton(onClick = {
                                loginIdentifier = "instruktur@asesmen.id"
                                loginPassword = "instruktur123"
                            }) {
                                Text("Instruktur Demo", fontSize = 11.sp)
                            }
                            TextButton(onClick = {
                                loginIdentifier = "admin@asesmen.id"
                                loginPassword = "admin123"
                            }) {
                                Text("Admin Demo", fontSize = 11.sp)
                            }
                        }
                    }
                }
            } else {
                // REGISTER FORM
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Pendaftaran Akun Baru",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Lembaga Asesmen Profesional Indonesia",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Role Selector Cards
                        Text("Pilih Peran Akses LMS:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("SISWA", "INSTRUKTUR", "ADMIN").forEach { roleOption ->
                                val isSelected = regRole == roleOption
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { regRole = roleOption },
                                    label = { Text(roleOption, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    leadingIcon = if (isSelected) {
                                        { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp)) }
                                    } else null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("role_chip_$roleOption")
                                )
                            }
                        }

                        // Syarat khusus info notice based on Role
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = when (regRole) {
                                    "ADMIN" -> "Syarat Khusus Admin: Wajib memasukkan Kode Kunci Akses Admin Lembaga (Gunakan: ADMIN-API-2026)."
                                    "INSTRUKTUR" -> "Syarat Khusus Instruktur: Wajib memasukkan Nomor Sertifikat/Spesialisasi Pengajar."
                                    else -> "Siswa: Mengakses materi, kuis otomatis, dan mengunggah jawaban ujian esai."
                                },
                                fontSize = 11.sp,
                                modifier = Modifier.padding(8.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = regName,
                            onValueChange = { regName = it },
                            label = { Text("Nama Lengkap") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Badge, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_name_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Email OTP Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = regEmail,
                                onValueChange = { regEmail = it },
                                label = { Text("Email Pendaftaran") },
                                leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null) },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("reg_email_input")
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    viewModel.sendOtpEmail(regEmail) { generated ->
                                        generatedOtpLocal = generated
                                        isOtpSentLocal = true
                                    }
                                },
                                modifier = Modifier
                                    .height(56.dp)
                                    .testTag("send_otp_button"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Kirim OTP", fontSize = 11.sp)
                            }
                        }

                        if (isOtpSentLocal) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = regOtpCodeInput,
                                onValueChange = { regOtpCodeInput = it },
                                label = { Text("Kode OTP Email (Simulasi: $generatedOtpLocal)") },
                                leadingIcon = { Icon(imageVector = Icons.Default.MarkEmailRead, contentDescription = null) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_otp_input")
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = regPhone,
                            onValueChange = { regPhone = it },
                            label = { Text("Nomor Telepon / WA") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_phone_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = regProgram,
                            onValueChange = { regProgram = it },
                            label = { Text("Program / Keahlian") },
                            leadingIcon = { Icon(imageVector = Icons.Default.School, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (regRole != "SISWA") {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = regSpecialReq,
                                onValueChange = { regSpecialReq = it },
                                label = {
                                    Text(if (regRole == "ADMIN") "Kode Kunci Akses Admin (Gunakan: ADMIN-API-2026)" else "No. Sertifikat/Spesialisasi Instruktur")
                                },
                                leadingIcon = { Icon(imageVector = Icons.Default.Key, contentDescription = null) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_special_req_input")
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = regPassword,
                            onValueChange = { regPassword = it },
                            label = { Text("Kata Sandi (Password)") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { regPasswordVisible = !regPasswordVisible }) {
                                    Icon(
                                        imageVector = if (regPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null
                                    )
                                }
                            },
                            visualTransformation = if (regPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_password_input")
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                viewModel.verifyAndRegisterUser(
                                    name = regName,
                                    email = regEmail,
                                    phone = regPhone,
                                    password = regPassword,
                                    role = regRole,
                                    program = regProgram,
                                    specialReq = regSpecialReq,
                                    inputOtp = regOtpCodeInput,
                                    expectedOtp = generatedOtpLocal,
                                    onSuccess = onAuthSuccess
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("reg_submit_button")
                        ) {
                            Icon(imageVector = Icons.Default.HowToReg, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("DAFTAR SEKARANG", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 2FA Verification Dialog Modal
        if (requires2FA) {
            val otpCodeSent = (otpState as? OtpFlowState.Sent)?.otpCode ?: ""
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Verifikasi Dua Faktor (2FA)", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Kode keamanan 2FA telah dikirimkan ke email Anda. Masukkan 6 digit kode berikut untuk melanjutkan:")
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Kode Simulasi: $otpCodeSent",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = input2FACode,
                            onValueChange = { input2FACode = it },
                            label = { Text("Kode 2FA (6 Digit)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("2fa_code_input")
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.verify2FA(input2FACode, otpCodeSent, onAuthSuccess)
                        },
                        modifier = Modifier.testTag("2fa_confirm_button")
                    ) {
                        Text("Verifikasi & Masuk")
                    }
                }
            )
        }
    }
}
