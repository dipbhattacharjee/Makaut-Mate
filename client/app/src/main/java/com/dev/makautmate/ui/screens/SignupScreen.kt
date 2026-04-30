package com.dev.makautmate.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.EventNote
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.makautmate.ui.theme.BluePrimary
import com.dev.makautmate.ui.viewmodel.AuthViewModel

@Composable
fun SignupScreen(
    onSignupSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var college by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var semester by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(authState) {
        if (authState is AuthViewModel.AuthState.Success) {
            onSignupSuccess()
        } else if (authState is AuthViewModel.AuthState.Error) {
            Toast.makeText(context, (authState as AuthViewModel.AuthState.Error).message, Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0F1A30), Color(0xFF05080C))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, tint = Color.White)
                }
                Text(
                    text = "Create Account",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            SignupTextField(value = fullName, onValueChange = { fullName = it }, label = "Full Name", icon = Icons.Rounded.Person)
            Spacer(modifier = Modifier.height(16.dp))
            SignupTextField(value = email, onValueChange = { email = it }, label = "Email Address", icon = Icons.Rounded.Email)
            Spacer(modifier = Modifier.height(16.dp))
            SignupTextField(value = password, onValueChange = { password = it }, label = "Password", icon = Icons.Rounded.Lock, isPassword = true)
            Spacer(modifier = Modifier.height(16.dp))
            SignupTextField(value = phone, onValueChange = { phone = it }, label = "Phone Number", icon = Icons.Rounded.Phone)
            Spacer(modifier = Modifier.height(16.dp))
            SignupTextField(value = college, onValueChange = { college = it }, label = "College Name", icon = Icons.Rounded.School)
            Spacer(modifier = Modifier.height(16.dp))
            SignupTextField(value = department, onValueChange = { department = it }, label = "Department", icon = Icons.Rounded.AccountTree)
            Spacer(modifier = Modifier.height(16.dp))
            SignupTextField(value = semester, onValueChange = { semester = it }, label = "Current Semester", icon = Icons.AutoMirrored.Rounded.EventNote)

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = { 
                    viewModel.signup(fullName, email, password, phone, college, department, semester) 
                },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                enabled = authState !is AuthViewModel.AuthState.Loading,
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                shape = RoundedCornerShape(30.dp)
            ) {
                if (authState is AuthViewModel.AuthState.Loading) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                } else {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Sign Up", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, tint = Color.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Already have an account? Login",
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.clickable { onBack() }
            )
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun SignupTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label, color = Color.White.copy(alpha = 0.6f)) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = BluePrimary) },
        shape = RoundedCornerShape(16.dp),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
            focusedContainerColor = Color.White.copy(alpha = 0.05f),
            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
            focusedBorderColor = BluePrimary,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        )
    )
}
