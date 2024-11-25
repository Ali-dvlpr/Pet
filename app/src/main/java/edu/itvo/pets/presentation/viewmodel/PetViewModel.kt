package edu.itvo.pets.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.itvo.pets.data.models.PetModel
import edu.itvo.pets.domain.usecases.PetUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PetViewModel @Inject constructor(private val petUseCase: PetUseCase):  ViewModel(){
    private val _uiState = MutableStateFlow(PetState())
    val uiState: StateFlow<PetState> = _uiState.asStateFlow()

    //Nueva lista para almacenar mascotas
    private val _petsList = MutableStateFlow<List<PetModel>>(emptyList())
    val petslist: StateFlow<List<PetModel>> = _petsList.asStateFlow()

    fun onEvent(event: PetEvent) {
        when (event) {
            is PetEvent.NameChanged -> {
              _uiState.value = _uiState.value.copy(
                        name= event.name)
            }
            is PetEvent.DescriptionChanged -> {
                _uiState.value = _uiState.value.copy(
                    description =  event.description)
            }
            is PetEvent.ImageChanged -> {
                _uiState.value = _uiState.value.copy(
                    image =  event.image)
            }
            is PetEvent.TypeChanged -> {
                _uiState.value = _uiState.value.copy(
                    type = event.type)
            }
            is PetEvent.RaceChanged -> {
                _uiState.value = _uiState.value.copy(
                    race= event.race)
            }
            is PetEvent.BirthdateChanged -> {
                _uiState.value = _uiState.value.copy(
                    birthdate =  event.birthdate)
            }
            is PetEvent.AddClicked  -> {
                    val pet = PetModel(
                        id=0,
                        name = event.name, description = event.description,
                        type = event.type, race = event.race, birthdate = event.birthdate,
                        image = event.image,
                    )
                    println(pet)
                    viewModelScope.launch(Dispatchers.IO) {
                        petUseCase.addPet(pet)
                        //Actualiza la lista depues de agregar
                        fetchPets()
                    }
            }
            is PetEvent.UpdateClicked ->{
                val pet = PetModel(
                    id= event.id,
                    name= event.name,
                    description = event.description,
                    type = event.type,
                    race = event.race,
                    birthdate = event.birthdate,
                    image = event.image
                )
                viewModelScope.launch(Dispatchers.IO) {
                    petUseCase.updatePet(pet)
                    fetchPets() //Actualiza la lista despues de actualizar
                }
            }

            is PetEvent.DeleteClicked -> {
                viewModelScope.launch(Dispatchers.IO) {
                    petUseCase.deletePet(event.id)
                    fetchPets() //Actualiza la lista despues de eliminar
                }
            }
            is PetEvent.Reset -> {
                _uiState.value = PetState() // Restablece todos los campos a sus valores predeterminados
            }

        }
    }


    //Metodo para obtener las mascotas
    private fun fetchPets(){
        viewModelScope.launch(Dispatchers.IO){
            petUseCase.getPets().collect { response ->
                response?.data?.let {pets ->
                    _petsList.value = pets.filterNotNull() //Filtra los nulos
                }
            }
        }
    }

    data class PetState(
        val name: String = "",
        val description: String ="",
        val image: String ="",
        val type: String = "",
        val race: String = "",
        val birthdate: String = "",
        val isLoading: Boolean=false,
        val error: String = "",
        val success: Boolean=false,
        val hasError: Boolean = false,
   )
    sealed class PetEvent {
        data class NameChanged(val name: String) : PetEvent()
        data class DescriptionChanged(val description: String) : PetEvent()
        data class ImageChanged(val image: String) : PetEvent()
        data class TypeChanged(val type: String) : PetEvent()
        data class RaceChanged(val race: String) : PetEvent()
        data class BirthdateChanged(val birthdate: String) : PetEvent()
        data class AddClicked(val name: String,
                              val description: String,
                              val type: String,
                              val race: String,
                              val birthdate: String,
                              val image: String
            ) : PetEvent()
        data class UpdateClicked(
            val id: Int,
            val name: String,
            val description: String,
            val type: String,
            val race: String,
            val birthdate: String,
            val image: String
        ) : PetEvent()

        data class DeleteClicked(val id: Int) : PetEvent()

        // Nuevo evento para restablecer los campos
        object Reset : PetEvent() // No necesita parámetros
    }
}