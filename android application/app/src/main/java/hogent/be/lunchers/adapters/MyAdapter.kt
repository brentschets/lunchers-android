package hogent.be.lunchers.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import hogent.be.lunchers.R
import hogent.be.lunchers.utils.MyHolder

class MyAdapter(private val place: IntArray, private val name: Array<String>, private val mContext: Context) : RecyclerView.Adapter<MyHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.list_item, p0, false)
        return MyHolder(v, mContext)
    }

    override fun getItemCount(): Int {
        return place.size
    }

    override fun onBindViewHolder(p0: MyHolder, p1: Int) {
        p0.index(place[p1], name[p1])
    }

}