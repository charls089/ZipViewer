package com.kobbi.project.zipviewer.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.kobbi.project.zipviewer.BR
import com.kobbi.project.zipviewer.R
import com.kobbi.project.zipviewer.databinding.FragmentViewBinding

class ViewFragment: Fragment() {
    companion object {
        private const val POSITION_INDEX_CODE = "index"

        fun newInstance(index: Int): ViewFragment {
            val fragment = ViewFragment()
            val args = Bundle().apply {
                putInt(POSITION_INDEX_CODE, index)
            }
            fragment.arguments = args
            return fragment
        }
    }

    private var mPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPosition = arguments?.getInt(POSITION_INDEX_CODE) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentViewBinding>(
            inflater, R.layout.fragment_view, container, false
        ).apply{
        context?.applicationContext?.let {
                activity?.run {
                    this@apply.setVariable(BR.file, file)
                    lifecycleOwner = this@ViewFragment
                }
            }
        }
        return binding.root
    }
}