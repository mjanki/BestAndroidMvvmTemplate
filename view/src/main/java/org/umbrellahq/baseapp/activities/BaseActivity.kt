package org.umbrellahq.baseapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.jakewharton.threetenabp.AndroidThreeTen
import org.umbrellahq.baseapp.R
import org.umbrellahq.baseapp.mappers.ErrorNetworkViewViewModelMapper
import org.umbrellahq.baseapp.models.ErrorNetworkViewEntity
import org.umbrellahq.util.NavigationUtil
import org.umbrellahq.util.helper.removeOverlay
import org.umbrellahq.viewmodel.models.ErrorNetworkViewModelEntity
import org.umbrellahq.viewmodel.viewmodels.ErrorNetworkViewModel

open class BaseActivity : AppCompatActivity() {
    private lateinit var errorNetworkVM: ErrorNetworkViewModel
    private var currentErrorNetwork: ErrorNetworkViewModelEntity? = null
    private val errorNetworkViewViewModelMapper = ErrorNetworkViewViewModelMapper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize AndroidThreeTen
        AndroidThreeTen.init(application)

        // Setup main fragment id and main constraint layout id in NavigationUtil
        NavigationUtil.setup(R.id.clContainer)

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
                        handleErrorNetwork(errorNetworkViewViewModelMapper.upstream(currentError))
                    }
                }
        )
    }

    // To be handled in subclasses
    open fun handleErrorNetwork(errorNetworkViewEntity: ErrorNetworkViewEntity) { }

    protected fun resolveErrorNetwork() {
        currentErrorNetwork?.let {
            errorNetworkVM.deleteErrorNetwork(it)
        }
    }

    override fun onResume() {
        super.onResume()

        removeOverlay(this)
    }
}