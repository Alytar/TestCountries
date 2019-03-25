package com.testcountriesapp.ui.main.fragment.mainList

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.testcountriesapp.R
import com.testcountriesapp.architecture.BaseMVVMFragment
import com.testcountriesapp.general.adapter.BaseRecyclerAdapter
import com.testcountriesapp.general.behavior.ActionBarBehaviorWrapper
import com.testcountriesapp.general.enums.FragmentTransition
import com.testcountriesapp.repository.model.Country
import com.testcountriesapp.ui.main.fragment.borderList.BordersFragment
import kotlinx.android.synthetic.main.fragment_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : BaseMVVMFragment(R.layout.fragment_main) {

    val viewModel: MainFragmentViewModel by viewModel()

    private lateinit var adapter: BaseRecyclerAdapter<Country>
    private var page = 1
    private var stopPagin = false

    companion object {
        fun getInstance(): MainFragment =
            MainFragment()
    }

    override fun initToolbar(behavior: ActionBarBehaviorWrapper) {
        behavior.applyToolbarParams(
            getString(R.string.app_name),
            ActionBarBehaviorWrapper.Button.BACK
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        viewModel.loadCountries()
    }

    private fun setupAdapter() {
        val factory = MainFragmentVHFactory()
        adapter = BaseRecyclerAdapter(context, listOf(), factory, R.layout.item_list)
        adapter.setOnItemClickListener {view, item, position, _ ->
            item.id?.let {
                navigator?.replaceFragment(
                    BordersFragment.newInstance(it, item.countryName),
                    addToBackStack = true,
                    transition = FragmentTransition.FADE
                )
                viewModel.scrollToItem(position, view.top)
            }
        }
        countries_list.adapter = adapter
        countries_list.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        countries_list.layoutManager = layoutManager
    }

    override fun onBindLiveData() {
        super.onBindLiveData()
        observe(viewModel.progressLoadingEvent, ::setInProgress)
        observe(viewModel.errorDialogEvent, ::showErrorDialog)
        observe(viewModel.countriesLiveData, ::showCountries)
        observe(viewModel.scrollLiveData, ::scrollToPosition)

    }

    private fun scrollToPosition(pair: Pair<Int, Int>) {
        pair.let {
            val manager = countries_list.layoutManager as LinearLayoutManager
            manager.scrollToPositionWithOffset(pair.first, pair.second)
        }
    }

    private fun showCountries(countries: ArrayList<Country>?) {
        adapter.replace(countries)
    }


    override fun attachLifecycleObserver() {
        lifecycle.addObserver(viewModel)
    }


}