package com.mec.mec.employee

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mec.mec.R
import com.mec.mec.model.Employee

class EmployeeListAdapter(
    private val onItemClick: (Long) -> Unit // Callback to handle item click
) : ListAdapter<Employee, EmployeeListAdapter.EmployeeViewHolder>(EmployeeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_employee, parent, false)
        return EmployeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val employee = getItem(position)
        holder.bind(employee)
        holder.itemView.setOnClickListener {
            onItemClick(employee.userID) // Pass userID on item click
        }
    }

    class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val firstNameTextView: TextView = itemView.findViewById(R.id.subjectTextView)
        private val lastNameTextView: TextView = itemView.findViewById(R.id.detailsTextView)

        fun bind(employee: Employee) {
            firstNameTextView.text = employee.firstName
            lastNameTextView.text = employee.lastName
        }
    }

    private class EmployeeDiffCallback : DiffUtil.ItemCallback<Employee>() {
        override fun areItemsTheSame(oldItem: Employee, newItem: Employee): Boolean {
            return oldItem.userID == newItem.userID
        }

        override fun areContentsTheSame(oldItem: Employee, newItem: Employee): Boolean {
            return oldItem == newItem
        }
    }
}
