package com.cyhee.android.rabit.activity.goal

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.cyhee.android.rabit.R
import com.cyhee.android.rabit.activity.comment.CommentViewAdapter
import com.cyhee.android.rabit.activity.goallog.GoalLogViewAdapter
import com.cyhee.android.rabit.listener.IntentListener
import com.cyhee.android.rabit.model.*
import kotlinx.android.synthetic.main.activity_goal.*
import kotlinx.android.synthetic.main.item_complete_fullgoal.*
import kotlinx.android.synthetic.main.item_complete_list.*
import kotlinx.android.synthetic.main.item_complete_prevtopbar.*
import kotlinx.android.synthetic.main.item_part_goalwriter.*
import kotlinx.android.synthetic.main.item_part_reaction.*


class GoalActivity: AppCompatActivity(), GoalContract.View {
    override var presenter : GoalContract.Presenter = GoalPresenter(this)
    private var commentAdapter: CommentViewAdapter? = null
    private var goalLogAdapter: GoalLogViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal)

        if (intent.hasExtra("goalId")) {
            val goalId = intent.getLongExtra("goalId", -1)
            presenter.goalInfo(goalId)
            presenter.goalStoreGoalLogs(goalId)

            likeNumberText.setOnClickListener(IntentListener.toGoalLikeListListener(goalId))

        } else {
            Toast.makeText(this, "전달된 goal 아이디가 없습니다", Toast.LENGTH_SHORT).show()
        }

        prevBtn.setOnClickListener {
            Log.d("preBtn","clicked")
            finish()
        }
    }

    override fun showGoalInfo(goalInfo : GoalInfo) {
        nameText.text = goalInfo.author.username
        titleText.text = goalInfo.content

        var companion = when {
            goalInfo.parent != null -> "${goalInfo.parent!!.author.username} 님 외 ${goalInfo.companionNum}명이 함께하는 중"
            goalInfo.companionNum != 0 -> "${goalInfo.companionNum}명이 함께하는 중"
            else -> "함께 해보세요!"
        }

        companionText.text = companion
        likeNumberText.text = goalInfo.likeNum.toString()
        commentNumberText.text = goalInfo.commentNum.toString()

        when {
            goalInfo.parent != null -> companionText.setOnClickListener(IntentListener.toGoalListener(goalInfo.parent!!.id))
            else -> companionText.setOnClickListener(IntentListener.toGoalListener(goalInfo.id))
        }

        likeButton.setOnClickListener {
            presenter.postLikeForGoal(goalInfo.id)
        }

        commentWriteLayout.findViewById<Button>(R.id.postBtn).setOnClickListener {
            val content = commentWriteLayout.findViewById<EditText>(R.id.commentText).text.toString()
            // TODO: 내용이 없을 경우 포스트 안되도록
            val postedComment = CommentFactory.Post(content)

            presenter.postCommentForGoal(goalInfo.id, postedComment)
        }
    }

    override fun showComments(comments: MutableList<Comment>) {
        if (commentAdapter == null) {
            commentAdapter = CommentViewAdapter(comments)
            commentListLayout.findViewById<RecyclerView>(R.id.listView).addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
            commentListLayout.findViewById<RecyclerView>(R.id.listView).adapter = commentAdapter
        } else {
            commentAdapter!!.appendComments(comments)
        }
    }

    override fun showGoalLogInfos(goalLogInfos: MutableList<GoalLogInfo>) {
        if (goalLogAdapter === null) {
            goalLogAdapter = GoalLogViewAdapter(goalLogInfos,
                    { id -> presenter.postLikeForGoalLog(id)},
                    { id, comment -> presenter.postCommentForGoalLog(id, comment)})
            goalLogListLayout.findViewById<RecyclerView>(R.id.listView).addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
            goalLogListLayout.findViewById<RecyclerView>(R.id.listView).adapter = goalLogAdapter
        } else {
            goalLogAdapter!!.appendGoalLogInfos(goalLogInfos)
        }
    }
}