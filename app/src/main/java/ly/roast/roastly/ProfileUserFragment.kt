package ly.roast.roastly

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

class ProfileUserFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the XML layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile_user, container, false)

        // Find the ImageView with ID 'icon_menu' in the layout
        val iconMenu = view.findViewById<ImageView>(R.id.icon_menu)

        // Set a click listener on 'iconMenu'
        iconMenu.setOnClickListener {

            // Create a new instance of MenuFragment to display
            val fragmentMenu = MenuFragment()

            // Replace the FrameLayout with ID 'fragment_menu_container' with the MenuFragment instance
            childFragmentManager.beginTransaction()
                .replace(R.id.fragment_menu_container, fragmentMenu)
                .commit()
        }

        return view
    }
}

