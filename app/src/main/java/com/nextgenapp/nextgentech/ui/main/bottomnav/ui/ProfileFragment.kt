package com.nextgenapp.nextgentech.ui.main.bottomnav.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nextgenapp.nextgentech.R
import com.nextgenapp.nextgentech.databinding.FragmentProfileBinding
import com.nextgenapp.nextgentech.ui.base.BaseFragment
import com.nextgenapp.nextgentech.utils.Session

class ProfileFragment : BaseFragment() {

    private lateinit var fragmentProfileBinding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentProfileBinding = FragmentProfileBinding.inflate(layoutInflater)
        return fragmentProfileBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusBar(R.color.white)

        fragmentProfileBinding.profileName.text = Session.userDetail?.name
    }
}