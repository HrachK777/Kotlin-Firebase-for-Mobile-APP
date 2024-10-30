//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.fragment.app.FragmentTransaction
//import ly.roast.roastly.ProfileUserFragment
//import ly.roast.roastly.R
//import ly.roast.roastly.ui.profile.ProfileFragment
//
//class ProfilesActivity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_profiles)
//
//        // Inicia a transação para adicionar o Fragment ao FrameLayout
//        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
//
//        // Cria uma nova instância do Fragment que deseja exibir
//        val profileFragment = ProfileUserFragment() // Supondo que o Fragment se chame ProfileFragment
//
//        // Insere o Fragment no FrameLayout com o id "fragment_menu_container_user"
//        fragmentTransaction.replace(R.id.fragment_menu_container_user, profileFragment)
//
//        // Confirma a transação
//        fragmentTransaction.commit()
//    }
//}
