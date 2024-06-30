package com.mec.mec.employee

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mec.mec.R
import com.mec.mec.model.Task

class EmployeeTaskAdapter(
    private var tasks: List<Task>,
    private val onItemClick: (Task) -> Unit // Callback to handle item click with Task parameter
) : RecyclerView.Adapter<EmployeeTaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task)
        holder.itemView.setOnClickListener {
            onItemClick(task) // Pass the clicked Task to the callback
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val subjectTextView: TextView = itemView.findViewById(R.id.subjectTextView)
        private val detailsTextView: TextView = itemView.findViewById(R.id.detailsTextView)
        private  val image: ImageView = itemView.findViewById(R.id.iv_bg)


        fun bind(task: Task) {
            subjectTextView.text = task.subject
            detailsTextView.text = task.details
            image.setImageResource(R.drawable.ep__arrow_right_bold) // Set your image resource here

        }
    }
}
