package hogent.be.lunchers.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hogent.be.lunchers.R
import hogent.be.lunchers.activities.MainActivity
import hogent.be.lunchers.utils.GuiUtil
import hogent.be.lunchers.utils.MessageUtil
import hogent.be.lunchers.viewmodels.AccountViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_register.*

/**
 * Een [Fragment] waarmee een gebruiker hem kan registreren.
 *
 * Gebruiker kan doorklikken naar login.
 */
class RegisterFragment : Fragment() {

    /**
     * [AccountViewModel] met de data over account
     */
    //Globaal ter beschikking gesteld aangezien het mogeiljks later nog in andere functie dan onCreateView wenst te worden
    private lateinit var accountViewModel : AccountViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_register, container, false)

        //viewmodel vullen
        accountViewModel = ViewModelProviders.of(requireActivity()).get(AccountViewModel::class.java)

        return rootView
    }

    /**
     * Instantieer de listeners
     */
    private fun initListeners() {
        //indien aangemeld naar lijst gaan
        accountViewModel.isLoggedIn.observe(this, Observer {
            if (accountViewModel.isLoggedIn.value == true) {
                //simuleert een button click op lijst om er voor te zorgen dat juiste
                //item actief is + zet fragment etc automatisch juist
                (requireActivity() as MainActivity).bottom_navigation_mainactivity.selectedItemId = R.id.action_list
            }
        })

        btn_register_login.setOnClickListener {
            login()
        }

        btn_register_confirm.setOnClickListener {
            register()
        }
    }

    /**
     * Stop de listeners
     */
    @Suppress("UNUSED_EXPRESSION")
    private fun stopListeners() {
        btn_register_login.setOnClickListener { null }

        btn_register_confirm.setOnClickListener { null }
    }

    /**
     * Kijkt of velden ingevuld zijn en wachtwoorden overeenkomen en probeert vervolgens te registreren
     */
    private fun register() {
        //er is een veld leeg
        if (text_register_phone_number.text.toString() == ""  ||
            text_register_first_name.text.toString() == ""  ||
            text_register_last_name.text.toString() == ""  ||
            text_register_email.text.toString() == ""  ||
            text_register_username.text.toString() == ""  ||
            text_register_password.text.toString() == ""  ||
            text_register_confirm_password.text.toString() == "" ) {
            MessageUtil.showToast("Gelieve alle velden in te vullen")
        }

        //wws niet gelijk
        else if (text_register_password.text.toString() != text_register_confirm_password.text.toString()) {
            MessageUtil.showToast("Wachtwoorden komen niet overeen")
        }

        //registreerKlant
        else {
            accountViewModel.registreerKlant(text_register_phone_number.text.toString(),
                    text_register_first_name.text.toString(),
                    text_register_last_name.text.toString(),
                    text_register_email.text.toString(),
                    text_register_username.text.toString(),
                    text_register_password.text.toString())
        }
    }

    /**
     * Kijkt of velden ingevuld zijn en probeert aan te melden
     */
    private fun login() {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container_mainactivity, LoginFragment())
            .addToBackStack(null)
            .commit()
    }

    /**
     * Stel de actionbar zijn titel in en enable back knop
     */
    override fun onResume() {
        super.onResume()
        GuiUtil.setActionBarTitle(requireActivity() as MainActivity, getString(R.string.text_register))
        GuiUtil.setCanPop(requireActivity() as MainActivity)
    }

    /**
     * Disable backnop
     */
    override fun onPause() {
        super.onPause()
        GuiUtil.removeCanPop(requireActivity() as MainActivity)
    }

    override fun onStart() {
        super.onStart()

        initListeners()
    }

    override fun onStop() {
        stopListeners()
        super.onStop()
    }

}