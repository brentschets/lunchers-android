package hogent.be.lunchers.fragments


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hogent.be.lunchers.R
import hogent.be.lunchers.activities.MainActivity
import hogent.be.lunchers.adapters.OrderAdapter
import hogent.be.lunchers.viewmodels.OrderViewModel
import kotlinx.android.synthetic.main.fragment_order_list.view.*

class OrderListFragment : Fragment() {

    private lateinit var orderViewModel: OrderViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_order_list, container, false)

        orderViewModel = ViewModelProviders.of(activity!!).get(OrderViewModel::class.java)

        val reservations = orderViewModel.reservations

        val adapter = OrderAdapter(activity!! as MainActivity, reservations)

        rootView.rv_order_list_orders.adapter = adapter

        reservations.observe(this, Observer {
            Log.d("LOL", "TIS AANGEPAST")
            adapter.notifyDataSetChanged()
        })

        return rootView
    }

}
