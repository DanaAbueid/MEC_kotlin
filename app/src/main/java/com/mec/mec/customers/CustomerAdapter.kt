package com.mec.mec.customers
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mec.mec.R
import com.mec.mec.model.Customer

class CustomerAdapter(
    private var customers: List<Customer>,
    private val onItemClick: (Customer) -> Unit
) : RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder>() {

    inner class CustomerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.tv_name)
        private val locationTextView: TextView = itemView.findViewById(R.id.tv_location)
        private  val image: ImageView = itemView.findViewById(R.id.iv_bg)


        fun bind(customer: Customer) {
            nameTextView.text = customer.customerName
            locationTextView.text = customer.customerLocation
            image.setImageResource(R.drawable.ep__arrow_right_bold) // Set your image resource here
            itemView.setOnClickListener { onItemClick(customer) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_cell_item, parent, false)
        return CustomerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        holder.bind(customers[position])
    }

    override fun getItemCount(): Int = customers.size

    fun updateCustomers(newCustomers: List<Customer>) {
        customers = newCustomers
        notifyDataSetChanged()
    }
}
