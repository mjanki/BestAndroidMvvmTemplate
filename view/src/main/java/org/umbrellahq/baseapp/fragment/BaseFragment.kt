package org.umbrellahq.baseapp.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import org.umbrellahq.util.foundation.FoundationFragment
import org.umbrellahq.viewmodel.model.ErrorNetworkViewModelEntity
import org.umbrellahq.viewmodel.viewmodel.ErrorNetworkViewModel

abstract class BaseFragment : FoundationFragment() {

    lateinit var errorNetworkVM: ErrorNetworkViewModel
    var currentErrorNetwork: ErrorNetworkViewModelEntity? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        errorNetworkVM = ViewModelProviders.of(this).get(ErrorNetworkViewModel::class.java)

        errorNetworkVM.getAllErrorsNetwork().observe(
                viewLifecycleOwner,
                Observer { errorNetworkVMEntityList ->

                    // If all errors were handled
                    if (errorNetworkVMEntityList.isEmpty()) {
                        currentErrorNetwork = null
                        return@Observer
                    }

                    val firstID = errorNetworkVMEntityList.first().id
                    val currentID = currentErrorNetwork?.id

                    // We haven't handled current error
                    if (firstID == currentID) {
                        return@Observer
                    }

                    currentErrorNetwork = errorNetworkVMEntityList.first()
                    currentErrorNetwork?.let { currentError ->
                        handleErrorNetwork(currentError)
                    }
                }
        )
    }

    // To be handled in subclasses
    abstract fun handleErrorNetwork(errorNetworkViewModelEntity: ErrorNetworkViewModelEntity)

    protected fun handlePostErrorNetwork() {
        currentErrorNetwork?.let {
            errorNetworkVM.deleteErrorNetwork(it)
        }
    }
}