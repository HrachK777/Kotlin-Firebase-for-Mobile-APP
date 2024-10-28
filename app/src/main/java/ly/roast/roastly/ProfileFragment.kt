package ly.roast.roastly

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentManager

class ProfileFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Obtém a referência para o icon_menu
        val iconMenu = view.findViewById<ImageView>(R.id.icon_menu)

        // Configura o listener de clique
        iconMenu.setOnClickListener {
            // Adiciona o FragmentMenu abaixo do icon_menu
            val fragmentMenu = MenuFragment()
            childFragmentManager.beginTransaction()
                .replace(R.id.fragment_menu_container, fragmentMenu) // Certifique-se de que este ID corresponde ao seu layout
                .commit()
        }

        return view
    }
}
