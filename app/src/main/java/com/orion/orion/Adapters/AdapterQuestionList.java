package com.orion.orion.Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.orion.orion.R;
import com.orion.orion.dialogs.AddQuestionDialog;
import com.orion.orion.models.QuizQuestion;
import com.orion.orion.profile.Account.Contest;

import java.util.ArrayList;

public class AdapterQuestionList extends RecyclerView.Adapter<AdapterQuestionList.ViewHolder> implements AddQuestionDialog.OnAddButtonClickListener {

    private static final String TAG = "AdapterQuestionList";
    private ArrayList<QuizQuestion> mList;
    private LayoutInflater mInflater;
    private Context mContext;

    @Override
    public void onClick(QuizQuestion quizQuestion, boolean isEdit, int idx) {
        Log.d(TAG, "onClick: " + isEdit);
        Log.d(TAG, "onClick: " + idx);
        if (!quizQuestion.isEmpty()) {
            if (isEdit) {
                mList.set(idx, quizQuestion);
                notifyItemChanged(idx);
            }
        }
    }

    public AdapterQuestionList(Context context, ArrayList<QuizQuestion> list) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mList = list;
    }

    public ArrayList<QuizQuestion> getQuestionList() {
        return this.mList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_question_list, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        QuizQuestion question = mList.get(position);
        if (!question.isEmpty()) {
            holder.question.setText("Q" + (position + 1) + "--> " + question.getQuestion());
            holder.option1value.setText(question.getOption1());
            holder.option2value.setText(question.getOption2());
            holder.option3value.setText(question.getOption3());
            holder.option4value.setText(question.getOption4());
            if (question.option1Correct())
                holder.option1value.setChecked(true);
            else if (question.option2Correct())
                holder.option2value.setChecked(true);
            else if (question.option3Correct())
                holder.option3value.setChecked(true);
            else
                holder.option4value.setChecked(true);
            holder.edit.setOnClickListener(v -> {
                AddQuestionDialog dialog = new AddQuestionDialog();
                FragmentManager fm = ((FragmentActivity) mContext).getSupportFragmentManager();
                dialog.setQuizQuestion(question);
                dialog.setPosition(position);
                Log.d(TAG, "onClick: " + position);
                dialog.show(fm, "Edit");
                mList.remove(position);
                notifyDataSetChanged();
            });
            holder.delete.setOnClickListener(v -> new AlertDialog.Builder(mContext)
                    .setTitle("Delete question")
                    .setMessage("Are you sure you want to delete this question?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        mList.remove(position);
                        notifyDataSetChanged();
                    })
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel())
                    .show());
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mList.size();
    }

    public QuizQuestion getItem(int id) {
        return mList.get(id);
    }

    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView question;
        private final RadioButton option1value;
        private final RadioButton option2value;
        private final RadioButton option3value;
        private final RadioButton option4value;
        private final ImageView edit;
        private final ImageView delete;

        public ViewHolder(View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.question);
            option1value = itemView.findViewById(R.id.option1Value);
            option2value = itemView.findViewById(R.id.option2Value);
            option3value = itemView.findViewById(R.id.option3Value);
            option4value = itemView.findViewById(R.id.option4Value);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}