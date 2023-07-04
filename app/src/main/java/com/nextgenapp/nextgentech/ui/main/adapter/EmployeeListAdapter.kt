package com.nextgenapp.nextgentech.ui.main.adapter;

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nextgenapp.nextgentech.R
import com.nextgenapp.nextgentech.data.model.EmployeeListResponse
import com.nextgenapp.nextgentech.databinding.EmployeeItemBinding

class EmployeeListAdapter(
    var employeeData: ArrayList<EmployeeListResponse.DataItem?>,
    var itemClick: (EmployeeListResponse.DataItem) -> Unit,
    var mail: (EmployeeListResponse.DataItem) -> Unit,
    var call: (EmployeeListResponse.DataItem) -> Unit
) :
    RecyclerView.Adapter<EmployeeListAdapter.EmployeeViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val employeeItemBinding =
            EmployeeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmployeeViewHolder(employeeItemBinding)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int = employeeData.size

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) =
        holder.bindItems(employeeData[position])

    //the class is holding the list view
    inner class EmployeeViewHolder(private val employeeItemBinding: EmployeeItemBinding) :
        RecyclerView.ViewHolder(employeeItemBinding.root) {
        private val context = employeeItemBinding.root.context

        fun bindItems(task: EmployeeListResponse.DataItem?) {
            //bind the item here
            if (task != null) {
                employeeItemBinding.apply {
                    profileName.text = task.name
                    team.text = task.departmentName
                    Glide.with(itemView.context).load(task.photo).circleCrop().placeholder(R.drawable.upload_image).into(profileImage)
                }
                employeeItemBinding.mail.setOnClickListener { mail(task) }
                employeeItemBinding.call.setOnClickListener { call(task) }
                employeeItemBinding.clickArea.setOnClickListener { itemClick(task) }
            }
        }

    }

}