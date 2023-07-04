package com.nextgenapp.nextgentech.ui.main.bottomnav.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.nextgenapp.nextgentech.R
import com.nextgenapp.nextgentech.data.api.Response
import com.nextgenapp.nextgentech.data.model.EmployeeListResponse
import com.nextgenapp.nextgentech.databinding.FragmentEmployeeBinding
import com.nextgenapp.nextgentech.ui.base.BaseFragment
import com.nextgenapp.nextgentech.ui.main.activity.AddEmployeeActivity
import com.nextgenapp.nextgentech.ui.main.activity.EmployeeProfileActivity
import com.nextgenapp.nextgentech.ui.main.adapter.EmployeeListAdapter
import com.nextgenapp.nextgentech.ui.viewmodel.EmployeeViewModel
import com.nextgenapp.nextgentech.utils.showLog
import com.nextgenapp.nextgentech.utils.showToast
import kotlinx.coroutines.launch


class EmployeeFragment : BaseFragment(), View.OnClickListener {

    private lateinit var fragmentEmployeeBinding: FragmentEmployeeBinding
    private val employeeViewModel : EmployeeViewModel by viewModels()
    private val employeeListResponseData by lazy { ArrayList<EmployeeListResponse.DataItem?>() }
    private val employeeListAdapter by lazy { EmployeeListAdapter(
        employeeData = employeeListResponseData,
        itemClick = ::itemClick,
        mail = ::mail,
        call = ::call
    ) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentEmployeeBinding = FragmentEmployeeBinding.inflate(layoutInflater)
        return fragmentEmployeeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusBar(R.color.status_bar)

        employeeViewModel.employeeListResponse.observe(viewLifecycleOwner, employeeObserver)

        fragmentEmployeeBinding.floatingActionButton.setOnClickListener(this)

        lifecycleScope.launch { employeeViewModel.getEmployeeList() }
    }
    @SuppressLint("SetTextI18n")
    private val employeeObserver = Observer<Response<EmployeeListResponse>>{
        when(it){
            is Response.Success ->{
                val dataItem = it.data
                if (dataItem != null){
                    val employeeData = dataItem.data
                    if (employeeData != null){
                        employeeListResponseData.clear()
                        if (employeeData.isNotEmpty()){
                            employeeListResponseData.addAll(employeeData)
                            fragmentEmployeeBinding.employeeList.adapter = employeeListAdapter
                            fragmentEmployeeBinding.employeeCount.text = "${employeeListResponseData.size} Employees"
                        }
                    }
                }
            }
            is Response.Error ->{
                requireActivity().showLog(it.errorMessage!!)
            }
            is Response.Loading -> {
                if (it.showLoader!!){
                    fragmentEmployeeBinding.progressBar.visibility = View.VISIBLE
                }else{
                    fragmentEmployeeBinding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            fragmentEmployeeBinding.floatingActionButton.id -> launchActivity(javaClass = AddEmployeeActivity::class.java)
        }
    }

    private fun mail(employee: EmployeeListResponse.DataItem) {
        requireActivity().showToast(message = "mail clicked")
    }

    private fun call(employee: EmployeeListResponse.DataItem) {
        requireActivity().showToast(message = "Call clicked")
    }

    private fun itemClick(employee: EmployeeListResponse.DataItem) {
        val bundle = Bundle().apply {
                putString("employeeId", employee.token)
        }
       launchActivity(javaClass = EmployeeProfileActivity::class.java, bundle = bundle)
    }
}