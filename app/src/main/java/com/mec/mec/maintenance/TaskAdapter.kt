package com.mec.mec.maintenance
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mec.mec.R
import com.mec.mec.model.Task

class TaskAdapter(private var tasks: List<Task>, private val onClick: (Task) -> Unit) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount(): Int = tasks.size

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val detailsTextView: TextView = itemView.findViewById(R.id.detailsTextView)
        private val subjectTextView: TextView = itemView.findViewById(R.id.subjectTextView)
        private  val image: ImageView = itemView.findViewById(R.id.iv_bg)


        fun bind(task: Task) {
            // Bind data to views
            detailsTextView.text = task.details
            subjectTextView.text = task.subject
            image.setImageResource(R.drawable.ep__arrow_right_bold) // Set your image resource here


            // Handle item click
            itemView.setOnClickListener {
                onClick(task)
            }
        }
    }
}
