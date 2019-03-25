package com.testcountriesapp.ui.main.fragment.borderList

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.testcountriesapp.R
import com.testcountriesapp.architecture.BaseMVVMFragment
import com.testcountriesapp.general.adapter.BaseRecyclerAdapter
import com.testcountriesapp.general.behavior.ActionBarBehaviorWrapper
import com.testcountriesapp.repository.model.Country
import com.testcountriesapp.ui.main.fragment.mainList.MainFragmentVHFactory
import kotlinx.android.synthetic.main.fragment_borders.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class BordersFragment : BaseMVVMFragment(R.layout.fragment_borders) {

    val viewModel: BordersFragmentViewModel by viewModel()
    private lateinit var adapter: BaseRecyclerAdapter<Country>
    private var countryId: Long? = null
    private var countryName: String? = null

    companion object {
        private const val COUNTRY_ID = "COUNTRY_ID"
        private const val COUNTRY_NAME = "COUNTRY_NAME"

        fun newInstance(countryId: Long, countryName: String?): BordersFragment {
            val fragment = BordersFragment()
            val args = Bundle()
            args.putLong(COUNTRY_ID, countryId)
            args.putString(COUNTRY_NAME, countryName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun initToolbar(behavior: ActionBarBehaviorWrapper) {
        countryId = this.arguments?.getLong(COUNTRY_ID, 0L)
        countryName = this.arguments?.getString(COUNTRY_NAME)
        countryName?.let {
            behavior.applyToolbarParams(
                it,
                ActionBarBehaviorWrapper.Button.BACK
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        countryId?.let { id ->
            viewModel.loadBorderCountries(id)
        }
    }

    private fun setupAdapter() {
        val factory = MainFragmentVHFactory()
        adapter = BaseRecyclerAdapter(context, listOf(), factory, R.layout.item_list)
        borders_list.adapter = adapter
        borders_list.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        borders_list.layoutManager = layoutManager
    }

    override fun onBindLiveData() {
        super.onBindLiveData()
        observe(viewModel.progressLoadingEvent, ::setInProgress)
        observe(viewModel.errorDialogEvent, ::showErrorDialog)
        observe(viewModel.countriesLiveData, ::showCountries)
    }

    private fun showCountries(countries: ArrayList<Country>?) {
        adapter.replace(countries)
    }


    override fun attachLifecycleObserver() {
        lifecycle.addObserver(viewModel)
    }

}