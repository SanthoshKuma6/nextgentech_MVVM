package com.nextgenapp.nextgentech.ui.main.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.nextgenapp.nextgentech.R
import com.nextgenapp.nextgentech.data.api.Response
import com.nextgenapp.nextgentech.data.model.EmployeeResponse
import com.nextgenapp.nextgentech.databinding.ActivityEmployeeProfileBinding
import com.nextgenapp.nextgentech.ui.base.BaseActivity
import com.nextgenapp.nextgentech.ui.viewmodel.EmployeeViewModel
import com.nextgenapp.nextgentech.utils.showToast
import kotlinx.coroutines.launch

class EmployeeProfileActivity : BaseActivity(), View.OnClickListener {

    private lateinit var activityEmployeeProfileBinding: ActivityEmployeeProfileBinding
    private val employeeViewModel: EmployeeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityEmployeeProfileBinding = ActivityEmployeeProfileBinding.inflate(layoutInflater)
        setContentView(activityEmployeeProfileBinding.root)

        activityEmployeeProfileBinding.backButton.setOnClickListener(this)

        employeeViewModel.employeeResponse.observe(this, employeeResponse)

        val employeeId = intent.extras?.getString("employeeId")
        lifecycleScope.launch { employeeId?.let { employeeViewModel.getEmployee(employeeId = it) } }

    }

    override fun onClick(view: View?) {
        when (view?.id) {
            activityEmployeeProfileBinding.backButton.id -> finish()
        }
    }

    private val employeeResponse = Observer<Response<EmployeeResponse>> {
        when (it) {
            is Response.Success -> {
                val dataItem = it.data
                if (dataItem != null) {
                    val employeeData = dataItem.data
                    if (employeeData != null) {
                        activityEmployeeProfileBinding.apply {
                            profileName.text = employeeData.name
                            team.text = employeeData.departmentName
                            mailAddress.text = employeeData.email
                            phoneNumber.text = employeeData.mobileNumber
                            dateOfBirth.text = employeeData.dateOfBirth
                            bloodGroup.text = employeeData.bloodGroup
                            address.text = employeeData.address
                            Glide.with(this@EmployeeProfileActivity).load(employeeData.photo)
                                .placeholder(R.drawable.upload_image).circleCrop()
                                .into(profileImage)
                        }
                    }
                }
            }
            is Response.Error -> {
                showToast("FAILED : " + it.errorMessage)
            }
            is Response.Loading -> {
                if (it.showLoader!!){
                    activityEmployeeProfileBinding.progressBar.visibility = View.VISIBLE
                }else{
                    activityEmployeeProfileBinding.progressBar.visibility = View.GONE
                }
            }
        }
    }
}