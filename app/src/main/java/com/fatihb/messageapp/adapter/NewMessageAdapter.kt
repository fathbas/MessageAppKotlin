package com.fatihb.messageapp.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.fatihb.messageapp.R
import com.fatihb.messageapp.model.UserModel
import com.fatihb.messageapp.view.ChatLog
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cate_row.view.*

class NewMessageAdapter(private val list: ArrayList<UserModel>):
    RecyclerView.Adapter<NewMessageAdapter.RowHolder>() {
    class RowHolder(view: View): RecyclerView.ViewHolder(view) {
        @SuppressLint("SetTextI18n")
        fun bind(list: UserModel){
            itemView.nameAndSurname.text = list.name+" "+list.surname
            Picasso.get().load(list.profilePicture).into(itemView.profilePic)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RowHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cate_row,parent,false)
        return RowHolder(view)
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        holder.bind(list[position])
        holder.itemView.setOnClickListener {
            val intent =
                Intent(it.context, ChatLog::class.java)
            intent.putExtra("action",list[position])
            startActivity(it.context,intent,null)
        }
    }

    override fun getItemCount(): Int {
    return list.size
    }
}