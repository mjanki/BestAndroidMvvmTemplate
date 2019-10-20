package org.umbrellahq.baseapp.fragment


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_main.*
import org.umbrellahq.baseapp.R
import org.umbrellahq.baseapp.activity.BaseActivity
import org.umbrellahq.baseapp.activity.SecondaryActivity
import org.umbrellahq.util.foundation.FoundationFragment
import org.umbrellahq.util.inflate
import org.umbrellahq.util.push
import org.umbrellahq.viewmodel.model.TaskViewModelEntity
import org.umbrellahq.viewmodel.viewmodel.TaskViewModel

class MainFragment : FoundationFragment() {
    private lateinit var taskVM: TaskViewModel

    companion object {
        const val REQUEST_CODE_1 = 1
        const val EXTRA_NAME = "extra1"

        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_main)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        taskVM = ViewModelProviders.of(this).get(TaskViewModel::class.java)

        taskVM.getAllTasks().observe(viewLifecycleOwner, Observer<List<TaskViewModelEntity>> {
            println("NOTE NOTE: OBSERVING")
            for (taskVMEntity in it) {
                println("NOTE NOTE: Name: ${taskVMEntity.name}")
            }
        })

        taskVM.getIsLoading().observe(viewLifecycleOwner, Observer {
            tvLoading.visibility = if (it) { View.VISIBLE } else { View.INVISIBLE }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bSecondary.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(SecondaryFragment.EXTRA_NAME, "This is the new text!")
            push<SecondaryActivity>(bundle, REQUEST_CODE_1, sharedViews = arrayListOf(
                    ivAndroid, tvAndroid
            ))
        }

        bAddTask.setOnClickListener {
            taskVM.insertTask("New Task ${taskVM.getAllTasks().value?.size}")
        }

        bPrintTasks.setOnClickListener {
            taskVM.update()
            /*println("NOTE NOTE: SIZE: ${taskVM.getAllTasks().value?.size}")
            taskVM.getAllTasks().value?.let {
                for (task in it) {
                    println("NOTE NOTE: ${task.id}: ${task.name}")
                }
            }*/
        }
    }

    override fun onResume() {
        super.onResume()

        bSecondary.isEnabled = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_1 && resultCode == Activity.RESULT_OK) {
            val string = data?.getStringExtra(EXTRA_NAME)
            if (string != null) tvMain.text = string
        }
    }
}