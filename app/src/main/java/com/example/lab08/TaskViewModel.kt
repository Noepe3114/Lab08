package com.example.lab08
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
enum class TaskFilter(val label: String) {
    ALL("Todas"),
    PENDING("Pendientes"),
    COMPLETED("Completadas")
}
class TaskViewModel(private val dao: TaskDao) : ViewModel() {
    private val _filter = MutableStateFlow(TaskFilter.ALL)
    val filter: StateFlow<TaskFilter> = _filter.asStateFlow()
    val tasks: StateFlow<List<Task>> = combine(dao.observeAllTasks(), _filter) { list, filter ->
        when (filter) {
            TaskFilter.ALL -> list
            TaskFilter.PENDING -> list.filter { !it.isCompleted }
            TaskFilter.COMPLETED -> list.filter { it.isCompleted }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )
    fun setFilter(filter: TaskFilter) {
        _filter.value = filter
    }
    fun addTask(description: String) {
        viewModelScope.launch {
            dao.insertTask(Task(description = description.trim()))
        }
    }
    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            dao.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            dao.deleteTask(task)
        }
    }

    fun deleteAllTasks() {
        viewModelScope.launch {
            dao.deleteAllTasks()
        }
    }
}