@file:OptIn(ExperimentalMaterial3Api::class)

package edu.itvo.pets.presentation.composables

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CrueltyFree
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HdrAuto
import androidx.compose.material.icons.filled.Panorama
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.itvo.pets.data.models.PetModel
import edu.itvo.pets.presentation.viewmodel.PetViewModel

@Composable
fun Pet(viewModel: PetViewModel = hiltViewModel(), modifier: Modifier = Modifier) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val petsList by viewModel.petslist.collectAsStateWithLifecycle()
    var isListVisible by remember { mutableStateOf(false) } // Controla la visibilidad de la lista
    var hasSaved by remember { mutableStateOf(false) }

    Column(modifier = modifier.padding(16.dp)) {


        // Mostrar lista de mascotas si es visible
        if (isListVisible) {
            if (petsList.isEmpty()) {
                Text(text = "No hay mascotas disponibles.")
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    items(petsList) { pet ->
                        PetListItem(pet = pet, onDeleteClick = {
                            viewModel.onEvent(PetViewModel.PetEvent.DeleteClicked(pet.id))
                        })
                    }
                }
            }
        }

        // Campos para agregar o actualizar mascota
        OutlinedTextField(
            value = state.name,
            onValueChange = { viewModel.onEvent(PetViewModel.PetEvent.NameChanged(it)) },
            label = { Text("Nombre") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {}),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.HdrAuto,
                    contentDescription = null,)
            }
        )

        OutlinedTextField(
            value = state.description,
            onValueChange = { viewModel.onEvent(PetViewModel.PetEvent.DescriptionChanged(it)) },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {}),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Description,
                    contentDescription = null,)
            }
        )

        OutlinedTextField(
            value = state.type,
            onValueChange = { viewModel.onEvent(PetViewModel.PetEvent.TypeChanged(it)) },
            label = { Text("Tipo") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {}),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.CrueltyFree,
                    contentDescription = null,)
            }
        )

        OutlinedTextField(
            value = state.race,
            onValueChange = { viewModel.onEvent(PetViewModel.PetEvent.RaceChanged(it)) },
            label = { Text("Raza") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {}),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Category,
                    contentDescription = null,)
            }
        )

        OutlinedTextField(
            value = state.birthdate,
            onValueChange = { viewModel.onEvent(PetViewModel.PetEvent.BirthdateChanged(it)) },
            label = { Text("Fecha nacimiento") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {}),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = null,)
            }
        )

        OutlinedTextField(
            value = state.image,
            onValueChange = { viewModel.onEvent(PetViewModel.PetEvent.ImageChanged(it)) },
            label = { Text("Foto") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {}),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Panorama,
                    contentDescription = null,)
            }
        )

        Button(onClick = {
            viewModel.onEvent(PetViewModel.PetEvent.AddClicked(
                name= state.name,
                description= state.description,
                type= state.type,
                race= state.race,
                birthdate= state.birthdate,
                image= state.image))

            // Limpiar campos después de guardar
            viewModel.onEvent(PetViewModel.PetEvent.Reset) // Asumiendo que tienes un evento Reset para limpiar el estado


            hasSaved = true // Indica que se ha guardado con éxito
        }) {
            Text("Guardar")
        }
       // Mostrar diálogo de éxito al guardar
        if (hasSaved) {
            AlertDialog(
                onDismissRequest = { hasSaved = false },
                confirmButton = {
                    Button(onClick = { hasSaved = false }) {
                        Text("Aceptar")
                    }
                },
                title = { Text("Éxito") },
                text = { Text("Guardado satisfactoriamente") }
            )
        }

        // Botón para mostrar/ocultar la lista de mascotas
        Button(onClick = { isListVisible = !isListVisible }) {
            Text(if (isListVisible) "Ocultar Listado" else "Mostrar Listado")
        }
    }
}

@Composable
fun PetListItem(pet: PetModel, onDeleteClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "${pet.name} - ${pet.type}", style = MaterialTheme.typography.titleMedium)

        Button(onClick = onDeleteClick) {
            Text("Eliminar")
        }
    }
}