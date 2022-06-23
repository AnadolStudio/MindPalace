package com.anadol.mindpalace.view.screens.main.lessons

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.anadol.mindpalace.R
import com.anadol.mindpalace.databinding.FragmentLessonsBinding
import com.anadol.mindpalace.view.screens.BaseFragment
import com.anadol.mindpalace.view.screens.main.lessons.adapter.InformationAdapter
import com.anadolstudio.core.viewbinding.viewBinding

class InformationFragment : BaseFragment(R.layout.fragment_lessons) {

    companion object {
        fun newInstance(): InformationFragment = InformationFragment()
    }

    private val binding by viewBinding { FragmentLessonsBinding.bind(requireView()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lessons.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = InformationAdapter()
        }
    }
}