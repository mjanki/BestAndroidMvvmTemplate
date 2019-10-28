package org.umbrellahq.baseapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_task.view.*
import org.umbrellahq.baseapp.R
import org.umbrellahq.baseapp.models.TaskViewEntity

class TasksRecyclerViewAdapter(var tasks: Array<TaskViewEntity>) : RecyclerView.Adapter<TasksRecyclerViewAdapter.TaskViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder =
            TaskViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                            R.layout.row_task,
                            parent,
                            false
                    )
            )

    override fun getItemCount(): Int = tasks.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.tvTaskName.text = tasks[position].name
    }

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTaskName: TextView = view.tvTaskName
    }
}