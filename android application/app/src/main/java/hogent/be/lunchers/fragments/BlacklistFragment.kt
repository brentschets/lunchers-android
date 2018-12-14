package hogent.be.lunchers.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hogent.be.lunchers.R
import hogent.be.lunchers.activities.MainActivity
import hogent.be.lunchers.adapters.LunchAdapter
import hogent.be.lunchers.viewmodels.LunchViewModel
import kotlinx.android.synthetic.main.lunch_list.*
import kotlinx.android.synthetic.main.lunch_list.view.*
import android.text.Editable
import android.text.TextWatcher
import hogent.be.lunchers.adapters.BlacklistAdapter
import hogent.be.lunchers.utils.MessageUtil
import hogent.be.lunchers.viewmodels.AccountViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_blacklist.*
import kotlinx.android.synthetic.main.fragment_blacklist.view.*
import kotlinx.android.synthetic.main.partial_search_filter.view.*


class BlacklistFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private var twoPane: Boolean = false

    /**
     * [AccountViewModel] met de info over aangemede user
     */
    //Globaal ter beschikking gesteld aangezien het mogeiljks later nog in andere functie dan onCreateView wenst te worden
    private lateinit var accountViewModel: AccountViewModel

    private lateinit var blacklistAdapter: BlacklistAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_blacklist, container, false)

        //viewmodel vullen
        accountViewModel = ViewModelProviders.of(requireActivity()).get(AccountViewModel::class.java)

        //blacklisteditems van server ophalen
        accountViewModel.refreshBlacklistedItems()

        //lijst vullen met lunches uit viewmodel.
        //We doen niet direct .value maar behouden het als mutueablelivedata mits we hier op willen op observen
        val blacklistedItems = accountViewModel.getBlacklistedItems()


        //adapter aanmaken
        blacklistAdapter = BlacklistAdapter(this.requireActivity() as MainActivity, blacklistedItems)

        //indien de meetinglijst veranderd moet de adapter opnieuw zijn cards genereren met nieuwe data
        blacklistedItems.observe(this, Observer {
            blacklistAdapter.notifyDataSetChanged()
        })

        rootView.blacklist_list.adapter = blacklistAdapter

        rootView.blacklist_swiperefresh.setOnRefreshListener(this)

        initListeners(rootView)

        return rootView
    }

    override fun onRefresh() {
        retrieveAllBlacklistedItems()
    }

    fun initListeners(rootView: View) {
        rootView.blacklist_add_fab.setOnClickListener { v ->
            MessageUtil.showMakeSuggestionDialog(context!!, "Toevoegen", "Voeg item toe aan zwarte lijst", addBlacklistItem())
        }
    }

    fun addBlacklistItem() = { name : String -> accountViewModel.addBlacklistedItem(name) }

    private fun retrieveAllBlacklistedItems() {
        accountViewModel.refreshBlacklistedItems()
        blacklist_swiperefresh?.isRefreshing = false
    }

    override fun onResume() {
        super.onResume()

        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onPause() {
        super.onPause()

        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

}