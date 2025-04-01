package com.example.foodappmvp.ui.home

import android.annotation.SuppressLint
import android.app.ProgressDialog.show
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.foodappmvp.R
import com.example.foodappmvp.data.model.home.ResponseCategoriesList
import com.example.foodappmvp.data.model.home.ResponseFoodList
import com.example.foodappmvp.databinding.FragmentHomeBinding
import com.example.foodappmvp.ui.home.adapters.CategoriesAdapter
import com.example.foodappmvp.utils.isNetworkAvailable
import com.example.foodappmvp.utils.showSnackBar
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding4.widget.textChanges
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(), HomeContracts.View {
    //Binding
    private lateinit var binding: FragmentHomeBinding

    @Inject
    lateinit var presenter: HomePresenter

    @Inject
    lateinit var categoriesAdapter: CategoriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //InitViews
        binding.apply {
            //Call api
            presenter.callFoodRandom()
            presenter.callCategoriesFoodList()
            //Search
            searchEdt.textChanges()
                .skipInitialValue()
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.toString().length > 1) {
                        //Call Api
                    }
                }
            //Filter
            filterFood()
        }
    }

    private fun filterFood() {
        val filters = listOf('A'..'Z').flatten()
        val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, filters)
        adapter.setDropDownViewResource(R.layout.item_spinner_list)
        binding.filterSpinner.adapter = adapter
        binding.filterSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //Call api
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    override fun loadFoodRandom(data: ResponseFoodList) {
        binding.headerImg.load(data.meals?.get(0)?.strMealThumb)
    }

    override fun loadCategoriesFoodList(data: ResponseCategoriesList) {
        categoriesAdapter.setData(data.categories)
        binding.categoryList.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = categoriesAdapter
        }
    }

    override fun showLoading() {
        binding.homeCategoryLoading.visibility = View.VISIBLE
        binding.categoryList.visibility = View.GONE
    }

    override fun hideLoading() {
        binding.homeCategoryLoading.visibility = View.GONE
        binding.categoryList.visibility = View.VISIBLE
    }

    override fun checkInternet(): Boolean {
        return requireContext().isNetworkAvailable()
    }

    override fun internetError(hasInternet: Boolean) {
    }

    override fun serverError(message: String) {
        binding.root.showSnackBar(message)
    }
}