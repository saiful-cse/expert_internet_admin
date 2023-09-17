package com.creativesaif.expert_internet_admin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.creativesaif.expert_internet_admin.R;
import com.creativesaif.expert_internet_admin.TaskList.Task;
import com.creativesaif.expert_internet_admin.TaskList.Task_Edit_Delete;

import java.util.List;
import java.util.Objects;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.EachTaskView> {
    Context context;
    List<Task> taskList;

    public TaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public EachTaskView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.card_each_task, null);
        return new TaskAdapter.EachTaskView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EachTaskView eachTaskView, int i) {
            Task task = taskList.get(i);

            eachTaskView.tvIssueDate.setText("Issued at: "+task.getCreated_at());
            eachTaskView.tvTaskId.setText("#ID: "+task.getId());
            eachTaskView.tvDescription.setText(task.getDescription());
            eachTaskView.tvAssignByOn.setText("Assign by "+task.getAssign_by()+" to "+task.getAssign_on());

            eachTaskView.ivNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (Objects.equals(task.getCompleted(), "0")){
                        Intent i;
                        i = new Intent(context, Task_Edit_Delete.class);
                        i.putExtra("taskId", task.getId());
                        i.putExtra("description", task.getDescription());
                        i.putExtra("assignBy", task.getAssign_by());
                        i.putExtra("assignOn", task.getAssign_on());
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }else{
                        Toast.makeText(view.getContext(), "Cannot task edit or delete. Task already completed.", Toast.LENGTH_SHORT).show();
                    }

                }
            });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    class EachTaskView extends RecyclerView.ViewHolder{

        TextView tvIssueDate, tvTaskId, tvDescription, tvAssignByOn;
        ImageView ivNext;
        public EachTaskView(@NonNull View itemView) {
            super(itemView);

            tvIssueDate = itemView.findViewById(R.id.tvissuedate);
            tvTaskId = itemView.findViewById(R.id.tvtaskid);
            tvDescription = itemView.findViewById(R.id.tvtaskdesc);
            tvAssignByOn = itemView.findViewById(R.id.tvassignbyon);
            ivNext = itemView.findViewById(R.id.imgNext);
        }
    }

}
