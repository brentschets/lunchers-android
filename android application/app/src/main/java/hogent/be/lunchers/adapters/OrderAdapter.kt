package hogent.be.lunchers.adapters

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import hogent.be.lunchers.fragments.LunchDetailFragment
import hogent.be.lunchers.R
import hogent.be.lunchers.activities.MainActivity
import hogent.be.lunchers.constants.BASE_URL_BACKEND
import hogent.be.lunchers.models.Reservatie
import hogent.be.lunchers.viewmodels.OrderViewModel
import kotlinx.android.synthetic.main.order_list_content.view.*

class OrderAdapter(
    private val parentActivity: MainActivity,
    private val reservaties: MutableLiveData<List<Reservatie>>)
    : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    private lateinit var orderViewModel: OrderViewModel

    private val onClickListener: View.OnClickListener

    init {
        orderViewModel = ViewModelProviders.of(parentActivity).get(OrderViewModel::class.java)
        onClickListener = View.OnClickListener { v ->
            val selectedOrder = v.tag as Reservatie
            orderViewModel.setSelectedOrder(selectedOrder.reservatieId)

            parentActivity.supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, LunchDetailFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lunch_list_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = reservaties.value!![position]
        Glide.with(parentActivity).load(BASE_URL_BACKEND + item.lunch.afbeeldingen[0].pad).into(holder.imageView)
        holder.lunchMerchantView.text = item.lunch.handelaar.handelsNaam
        holder.lunchNameView.text = item.lunch.naam
        holder.statusView.text = item.status.toString()
        holder.aantalView.text = String.format("Aantal: %d", item.aantal)
        holder.dateView.text = item.datum.toString()

        with(holder.itemView) {
            tag = item
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount() = reservaties.value!!.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.iv_order_list_content_lunchafbeelding
        val lunchMerchantView: TextView = view.tv_order_list_content_lunch_merchant
        val lunchNameView: TextView = view.tv_order_list_content_lunch_name
        val statusView: TextView = view.tv_order_list_content_status
        val aantalView: TextView = view.tv_order_list_content_aantal
        val dateView: TextView = view.tv_order_list_content_date
    }
}