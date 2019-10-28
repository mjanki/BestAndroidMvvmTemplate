package org.umbrellahq.baseapp.activities

import android.os.Bundle
import android.transition.Fade
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.jakewharton.threetenabp.AndroidThreeTen
import org.umbrellahq.baseapp.R
import org.umbrellahq.util.NavigationUtil
import org.umbrellahq.util.foundation.FoundationActivity
import org.umbrellahq.viewmodel.models.ErrorNetworkViewModelEntity
import org.umbrellahq.viewmodel.viewmodels.ErrorNetworkViewModel

open class BaseActivity : FoundationActivity() {

    private lateinit var errorNetworkVM: ErrorNetworkViewModel
    private var currentErrorNetwork: ErrorNetworkViewModelEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize AndroidThreeTen
        AndroidThreeTen.init(application)

        // Setup main fragment id and main constraint layout id in NavigationUtil
        NavigationUtil.setup(R.id.clContainer)

        val fade = Fade()
        fade.excludeTarget(android.R.id.statusBarBackground, true)
        fade.excludeTarget(android.R.id.navigationBarBackground, true)

        window.enterTransition = fade
        window.exitTransition = fade

        // Initialize ErrorsNetwork observable
        errorNetworkVM = ViewModelProviders.of(this).get(ErrorNetworkViewModel::class.java)
        errorNetworkVM.init()

        errorNetworkVM.getErrorsNetwork().observe(
                this,
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
    open fun handleErrorNetwork(errorNetworkViewModelEntity: ErrorNetworkViewModelEntity) { }

    protected fun resolveErrorNetwork() {
        currentErrorNetwork?.let {
            errorNetworkVM.deleteErrorNetwork(it)
        }
    }
}