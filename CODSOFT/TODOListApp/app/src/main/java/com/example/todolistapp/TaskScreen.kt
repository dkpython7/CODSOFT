package com.example.todolistapp

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistapp.adapter.TaskAdapter
import com.example.todolistapp.databinding.ActivityTaskScreenBinding
import com.example.todolistapp.model.Task
import com.example.todolistapp.view_model.TaskViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class TaskScreen : AppCompatActivity() {
    private lateinit var binding: ActivityTaskScreenBinding
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTaskScreenBinding.inflate(layoutInflater)
        setStatusBarColor()
        setContentView(binding.root)

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        taskAdapter = TaskAdapter(
            onItemClick = { task -> showEditTaskDialog(task) },
            onCheckboxChecked = { task -> taskViewModel.update(task) }
        )

        val recyclerView: RecyclerView = binding.taskListView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter

        taskViewModel.allTasks.observe(this) { tasks ->
            taskAdapter.setTasks(tasks)
        }

        val addTaskButton: FloatingActionButton = binding.addTaskBtn
        addTaskButton.setOnClickListener { showAddTaskDialog() }

        setupSwipeToDelete(recyclerView)
        setupWindowInsets()
    }

    private fun setupSwipeToDelete(recyclerView: RecyclerView) {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val task = taskAdapter.getTaskAt(position)

                AlertDialog.Builder(this@TaskScreen)
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Yes") { _: DialogInterface, _: Int -> taskViewModel.delete(task) }
                    .setNegativeButton("No") { _: DialogInterface, _: Int -> taskAdapter.notifyItemChanged(position) }
                    .create()
                    .show()
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                val itemView = viewHolder.itemView
                val background = ColorDrawable(Color.RED)
                val deleteIcon: Drawable? = ContextCompat.getDrawable(this@TaskScreen, R.drawable.baseline_delete_24)

                val iconMargin = (itemView.height - (deleteIcon?.intrinsicHeight ?: 0)) / 2
                val iconTop = itemView.top + iconMargin
                val iconBottom = iconTop + (deleteIcon?.intrinsicHeight ?: 0)

                if (dX < 0) {
                    val iconLeft = itemView.right - iconMargin - (deleteIcon?.intrinsicWidth ?: 0)
                    val iconRight = itemView.right - iconMargin
                    deleteIcon?.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                    background.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                } else {
                    background.setBounds(0, 0, 0, 0)
                }

                background.draw(c)
                deleteIcon?.draw(c)

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showAddTaskDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_task, null)
        val titleInput = dialogView.findViewById<EditText>(R.id.task_title_input)
        val descriptionInput = dialogView.findViewById<EditText>(R.id.task_description_input)
        val prioritySpinner = dialogView.findViewById<Spinner>(R.id.task_priority_spinner)
        val dueDateInput = dialogView.findViewById<EditText>(R.id.task_due_date_input)

        // Set up the spinner with priority options
        val priorityOptions = arrayOf("High", "Medium", "Low")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorityOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        prioritySpinner.adapter = adapter

        // DatePicker setup
        dueDateInput.setOnClickListener {
            showDatePickerDialog(dueDateInput)
        }

        AlertDialog.Builder(this)
            .setTitle("Add New Task")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title = titleInput.text.toString()
                val description = descriptionInput.text.toString()
                val priority = prioritySpinner.selectedItemPosition + 1 // Convert to integer (1 for High, 2 for Medium, 3 for Low)
                val dueDate = DateUtils.parseDate(dueDateInput.text.toString()) // Use the utility method

                val task = Task(title = title, description = description, subtitle = description, priority = priority, dueDate = dueDate)
                taskViewModel.insert(task)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditTaskDialog(task: Task) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_task, null)
        val titleInput = dialogView.findViewById<EditText>(R.id.task_title_input)
        val descriptionInput = dialogView.findViewById<EditText>(R.id.task_description_input)
        val prioritySpinner = dialogView.findViewById<Spinner>(R.id.task_priority_spinner)
        val dueDateInput = dialogView.findViewById<EditText>(R.id.task_due_date_input)

        // Populate the dialog with the current task details
        titleInput.setText(task.title)
        descriptionInput.setText(task.description)
        prioritySpinner.setSelection(task.priority - 1) // Set the current priority in the spinner
        dueDateInput.setText(DateUtils.formatDate(task.dueDate)) // Use the utility method

        // Set up the spinner with priority options
        val priorityOptions = arrayOf("High", "Medium", "Low")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorityOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        prioritySpinner.adapter = adapter

        // Set up a DatePicker for the due date input
        dueDateInput.setOnClickListener {
            showDatePickerDialog(dueDateInput, task)
        }

        AlertDialog.Builder(this)
            .setTitle("Edit Task")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                task.title = titleInput.text.toString()
                task.description = descriptionInput.text.toString()
                task.subtitle = descriptionInput.text.toString() // Update subtitle here
                task.priority = prioritySpinner.selectedItemPosition + 1
                task.dueDate = DateUtils.parseDate(dueDateInput.text.toString()) // Convert date string to timestamp

                taskViewModel.update(task)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDatePickerDialog(dueDateInput: EditText, task: Task? = null) {
        val calendar = Calendar.getInstance()
        if (task != null) {
            calendar.timeInMillis = task.dueDate ?: 0
        }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance().apply {
                set(Calendar.YEAR, selectedYear)
                set(Calendar.MONTH, selectedMonth)
                set(Calendar.DAY_OF_MONTH, selectedDay)
            }.time

            dueDateInput.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate))
        }, year, month, day).show()
    }
    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.appBarColor)
        }
    }
}
