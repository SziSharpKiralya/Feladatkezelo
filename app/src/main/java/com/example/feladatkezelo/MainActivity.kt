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
import androidx.compose.material3.TextButton
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
    val context = LocalContext.current
    var showDeleteDialog: Boolean by remember {
        mutableStateOf(false)
    }

    var showEditDialog: Boolean by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PriorityAvatar(task.priority)
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = task.category,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = when (task.priority) {
                        1 -> "Alacsony prioritás"
                        2 -> "Közepes prioritás"
                        else -> "Magas prioritás"
                    },
                    color = when (task.priority) {
                        1 -> Color(0xFF81C784)
                        2 -> Color(0xFFFFB74D)
                        else -> Color(0xFFE57373)
                    },
                    style = MaterialTheme.typography.bodyMedium
                )

            }

            IconButton(
                onClick = {
                    showDeleteDialog = true
                }
            ) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.ic_delete
                    ), contentDescription = ""
                )
            }

            IconButton(
                onClick = {
                    showEditDialog = true
                }
            ) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.ic_edit
                    ), contentDescription = ""
                )
            }
        }

        Button(
            onClick = {
                onToggle(
                    task.copy(
                        isCompleted = !task.isCompleted
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue,
                contentColor = Color.White
            )
        ) {
            Text(
                if (task.isCompleted) {
                    "Újra aktív"
                } else {
                    "Elkészült"
                }
            )
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                },
                title = { Text("Feladat törlése") },
                text = { Text("Biztosan törölni szeretnéd a(z) ${task.name} feladatot?") },
                shape = RoundedCornerShape(12.dp),
                confirmButton = {
                    TextButton(
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        ),
                        onClick = {
                        showDeleteDialog = false
                        onDelete()
                    }) { Text("Törlés") }
                },
                dismissButton = {
                    TextButton(
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.Black
                        ),
                        onClick = {
                        showDeleteDialog = false
                    }) { Text("Mégsem") }
                }
            )
        }

        if (showEditDialog) {
            var editName by remember {
                mutableStateOf("")
            }
            var editCategory by remember {
                mutableStateOf("")
            }
            var editPriority by remember {
                mutableStateOf("")
            }

            var editNameError by remember {
                mutableStateOf(false)
            }
            var editCategoryError by remember {
                mutableStateOf(false)
            }
            var editPriorityError by remember {
                mutableStateOf(false)
            }

            AlertDialog(
                onDismissRequest = {
                    showEditDialog = false
                },
                title = { Text("Diák szerkesztése") },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { it ->
                                editName = it
                                editNameError = false
                            },
                            label = { Text("Név") },
                            supportingText = {
                                if (editNameError)
                                    Text(
                                        "Név megadása kötelező!",
                                        color = MaterialTheme.colorScheme.error
                                    )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = editCategory,
                            onValueChange = { it ->
                                editCategory = it
                                editCategoryError = false
                            },
                            label = { Text("Kategória") },
                            supportingText = {
                                if (editCategoryError)
                                    Text(
                                        "Kategória megadása kötelező!",
                                        color = MaterialTheme.colorScheme.error
                                    )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = editPriority,
                            onValueChange = { it ->
                                editPriority = it
                                editPriorityError = false
                            },
                            label = { Text("Prioritás (1-3)") },
                            supportingText = {
                                if (editPriorityError)
                                    Text(
                                        "A prioritás csak 1 és 3 között lehet!",
                                        color = MaterialTheme.colorScheme.error
                                    )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        editNameError = editName.isBlank()
                        editCategoryError = editCategory.isBlank()
                        editPriorityError = editPriority == null ||
                                editPriority.toInt() !in 1..3
                        if (editNameError || editCategoryError || editPriorityError) {
                            Toast.makeText(
                                context, "Minden mezőt kell kitölteni",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val editedStudent = Task(
                                id = task.id,
                                name = editName.trim(),
                                category = editCategory.trim(),
                                priority = editPriority.toInt(),
                                isCompleted = task.isCompleted,
                            )
                            onEdit(editedStudent)
                            showEditDialog = false

                        }
                    }) { Text("Mentés") }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showEditDialog = false
                    }) { Text("Mégsem") }
                }
            )
        }
    }
}

@Composable
fun PriorityAvatar(priority: Int) {

    val backgroundColor = when (priority) {
        1 -> Color(0xFF81C784)
        2 -> Color(0xFFFFB74D)
        else -> Color(0xFFE57373)
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = priority.toString(),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )
    }
}
