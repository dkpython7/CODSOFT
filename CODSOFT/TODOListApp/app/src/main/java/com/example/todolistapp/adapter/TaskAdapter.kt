package com.example.todolistapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistapp.R
import com.example.todolistapp.model.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskAdapter(
    private val onItemClick: (Task) -> Unit,
    private val onCheckboxChecked: (Task) -> Unit // Callback to handle checkbox state changes
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var tasks: List<Task> = emptyList()

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.task_title)
        val dueDate: TextView = itemView.findViewById(R.id.task_due_date)
        val isCompleted: CheckBox = itemView.findViewById(R.id.checkbox_completed)
        val priorityIndicator: View = itemView.findViewById(R.id.priority_indicator)
        val subtitle: TextView = itemView.findViewById(R.id.task_subtitle)

        init {
            // Set a click listener for the checkbox to avoid immediate state change on clicking the item
            isCompleted.setOnCheckedChangeListener { _, isChecked ->
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val task = tasks[adapterPosition]
                    task.isCompleted = isChecked
                    onCheckboxChecked(task) // Call the callback to update task in RoomDB
                }
            }

            // Click listener for editing tasks
            itemView.setOnClickListener { onItemClick(tasks[adapterPosition]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        // Bind task data to the views
        holder.title.text = task.title
        holder.dueDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(task.dueDate ?: 0))
        holder.isCompleted.isChecked = task.isCompleted
        holder.subtitle.text = task.subtitle ?: "" // Display subtitle if available

        // Set the background color of the priority indicator based on task priority
        holder.priorityIndicator.setBackgroundColor(getPriorityColor(task.priority))
    }

    override fun getItemCount(): Int = tasks.size

    // Method to update the list of tasks and sort by priority
    fun setTasks(tasks: List<Task>) {
        this.tasks = tasks.sortedBy { it.priority } // Sort tasks by priority (1 -> High, 2 -> Medium, 3 -> Low)
        notifyDataSetChanged()
    }

    // Method to get a task at a specific position (used for swipe-to-delete)
    fun getTaskAt(position: Int): Task {
        return tasks[position]
    }

    // Helper function to get color based on task priority
    private fun getPriorityColor(priority: Int): Int {
        return when (priority) {
            1 -> Color.RED    // High priority
            2 -> Color.YELLOW // Medium priority
            3 -> Color.GREEN  // Low priority
            else -> Color.GRAY // Default or undefined priority
        }
    }
}
