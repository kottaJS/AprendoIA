package cl.duoc.aprendoia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.duoc.aprendoia.ui.components.animacionLatidoInfinito
import cl.duoc.aprendoia.ui.theme.AprendoIATheme
import cl.duoc.aprendoia.ui.theme.AzulPrimario
import cl.duoc.aprendoia.ui.theme.MoradoIA
import cl.duoc.aprendoia.viewmodel.AprendoIAViewModel

@Composable
fun LoginScreen(
    viewModel: AprendoIAViewModel? = null,
    onLoginExitoso: ((email: String, password: String) -> Unit)? = null
) {
    var email by remember { mutableStateOf("test@duocuc.cl") }
    var password by remember { mutableStateOf("123456") }
    var esModoRegistro by remember { mutableStateOf(false) }
    var nombreRegistro by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(MoradoIA.copy(alpha = 0.12f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Usuario",
                    tint = MoradoIA,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Aprendo IA",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = AzulPrimario
            )

            Text(
                text = "Plataforma Móvil Gamificada • Duoc UC",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = if (esModoRegistro) "Crear Cuenta" else "Iniciar Sesión",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (esModoRegistro) {
                        OutlinedTextField(
                            value = nombreRegistro,
                            onValueChange = { nombreRegistro = it },
                            label = { Text("Nombre Completo") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            viewModel?.loginEmailError = null
                        },
                        label = { Text("Correo Electrónico") },
                        isError = viewModel?.loginEmailError != null,
                        supportingText = {
                            viewModel?.loginEmailError?.let { err ->
                                Text(text = err, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = AzulPrimario)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            viewModel?.loginPasswordError = null
                        },
                        label = { Text("Contraseña") },
                        isError = viewModel?.loginPasswordError != null,
                        supportingText = {
                            viewModel?.loginPasswordError?.let { err ->
                                Text(text = err, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = AzulPrimario)
                        },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (viewModel != null) {
                                viewModel.validarLogin(email, password)
                            } else {
                                onLoginExitoso?.invoke(email, password)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .animacionLatidoInfinito(activo = email.isNotEmpty() && password.isNotEmpty()),
                        colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (esModoRegistro) "Registrarse" else "Ingresar",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = { esModoRegistro = !esModoRegistro }
                    ) {
                        Text(
                            text = if (esModoRegistro) "¿Ya tienes cuenta? Inicia sesión" else "¿No tienes cuenta? Regístrate",
                            color = MoradoIA
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    AprendoIATheme {
        LoginScreen()
    }
}
