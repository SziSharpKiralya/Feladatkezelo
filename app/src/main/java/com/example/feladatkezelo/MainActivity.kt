package com.example.feladatkezelo

import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.Int

data class Task(
    val id: Int,
    val name: String,
    val category: String,
    val priority: Int,
    val isCompleted: Boolean
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                TaskManagerScreen()
            }
        }
    }
}

@Composable
fun TaskManagerScreen() {
    val context = LocalContext.current

    var name by remember {
        mutableStateOf("")
    }
    var category by remember {
        mutableStateOf("")
    }
    var priority by remember {
        mutableStateOf("")
    }

    var nextId: Int by remember {
        mutableStateOf(1)
    }

    val tasks = remember {
        mutableStateListOf<Task>()
    }

    var nameError by remember {
        mutableStateOf(false)
    }
    var categoryError by remember {
        mutableStateOf(false)
    }
    var priorityError by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Feladatkezelő",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Szervezd meg a napi teendőidet!",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                nameError = false
            },
            label = { Text("Feladat neve") },
            isError = nameError,
            supportingText = {
                if (nameError) {
                    Text("A feladat neve kötelező!")
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = category,
            onValueChange = {
                category = it
                categoryError = false
            },
            label = { Text("Kategória") },
            isError = categoryError,
            supportingText = {
                if (categoryError) {
                    Text("A kategória megadása kötelező!")
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = priority,
            onValueChange = {
                priority = it
                priorityError = false
            },
            label = { Text("Prioritás (1-3)") },
            isError = priorityError,
            supportingText = {
                if (priorityError) {
                    Text("A prioritás csak 1 és 3 között lehet")
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = {
                nameError = name.isBlank()
                categoryError = category.isBlank()

                val priorityNumber = priority.toIntOrNull()

                priorityError = priorityNumber == null ||
                        priorityNumber !in 1..3

                if (nameError || categoryError || priorityError) {
                    Toast.makeText(
                        context,
                        "Ellenőrizd a megadott adatokat!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val newTask = Task(
                        id = nextId,
                        name = name.trim(),
                        category = category.trim(),
                        priority = priorityNumber!!,
                        isCompleted = false
                    )

                    tasks.add(newTask)
                    nextId++

                    Toast.makeText(
                        context,
                        "Feladat sikeresen hozzáadva!",
                        Toast.LENGTH_SHORT
                    ).show()

                    name = ""
                    category = ""
                    priority = ""

                    nameError = false
                    categoryError = false
                    priorityError = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue,
                contentColor = Color.White
            )
        ) {
            Text("Feladat hozzáadása")
        }

        HorizontalDivider()

        if (tasks.isEmpty()) {
            Text(
                "Még nincs feladatod.",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = tasks,
                    key = { task -> task.id }
                ) { task ->
                    TaskCard(
                        task = task,
                        onToggle = { updatedTask ->
                            val index = tasks.indexOfFirst {
                                it.id == updatedTask.id
                            }

                            if (index != -1) {
                                tasks[index] = updatedTask
                            }
                        },
                        onDelete = {
                            tasks.removeAll { it.id == task.id }
                        },
                        onEdit = { updatedTask ->
                            val index = tasks.indexOfFirst {
                                it.id == updatedTask.id
                            }

                            if (index != -1) {
                                tasks[index] = updatedTask
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskCard(task: Task, onToggle: (Task) -> Unit, onDelete: () -> Unit, onEdit: (Task) -> Unit)
{

}
