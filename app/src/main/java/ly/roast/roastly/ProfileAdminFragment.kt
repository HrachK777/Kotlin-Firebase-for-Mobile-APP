//package ly.roast.roastly
//
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.commit
//import ly.roast.roastly.databinding.FragmentProfileAdminBinding
//
//class ProfileAdminFragment : Fragment(), View.OnClickListener {
//
//    private var _binding: FragmentProfileAdminBinding? = null
//    private val binding get() = _binding!!
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentProfileAdminBinding.inflate(inflater, container, false)
//
//        // Configura o OnClickListener para o icon_menu usando View Binding
//        binding.iconMenu.setOnClickListener(this)
//
//        return binding.root
//    }
//
//    override fun onClick(v: View?) {
//        when (v?.id) {
//            R.id.icon_menu_user -> {
//                // Exibe o MenuFragment no container especificado
//                parentFragmentManager.commit {
//                    setReorderingAllowed(true)  // Otimiza a transação
//                    replace(R.id.fragment_menu_container_user, MenuFragment())
//                    addToBackStack(null)
//                }
//            }
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null // Libera o binding quando a view é destruída
//    }
//}
