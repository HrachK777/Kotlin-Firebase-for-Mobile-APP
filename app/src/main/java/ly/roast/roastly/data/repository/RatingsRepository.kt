package ly.roast.roastly.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RatingsRepository {

    private fun fetchUserReviews() {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: return
        FirebaseFirestore.getInstance().collection("reviews")
            .whereEqualTo("userEmail", userEmail)
            .get()
            .addOnSuccessListener { documents ->
                // Variáveis para acumular os valores
                var totalColaboracao = 0.0
                var countColaboracao = 0
                var totalConhecimento = 0.0
                var countConhecimento = 0
                var totalIniciativa = 0.0
                var countIniciativa = 0
                var totalResponsabilidade = 0.0
                var countResponsabilidade = 0

                // Loop para somar todos os valores das avaliações
                for (document in documents) {
                    document.getDouble("colaboracao")?.let {
                        totalColaboracao += it
                        countColaboracao++
                    }
                    document.getDouble("conhecimento")?.let {
                        totalConhecimento += it
                        countConhecimento++
                    }
                    document.getDouble("iniciativa")?.let {
                        totalIniciativa += it
                        countIniciativa++
                    }
                    document.getDouble("responsabilidade")?.let {
                        totalResponsabilidade += it
                        countResponsabilidade++
                    }
                }

                // Calcular médias
                val avgColaboracao = if (countColaboracao > 0) totalColaboracao / countColaboracao else 0.0
                val avgConhecimento = if (countConhecimento > 0) totalConhecimento / countConhecimento else 0.0
                val avgIniciativa = if (countIniciativa > 0) totalIniciativa / countIniciativa else 0.0
                val avgResponsabilidade = if (countResponsabilidade > 0) totalResponsabilidade / countResponsabilidade else 0.0

                // Atualizar o UI com as médias calculadas
                updateUIWithAverages(avgColaboracao, avgConhecimento, avgIniciativa, avgResponsabilidade)
            }
            .addOnFailureListener { exception ->
                // Tratar falha na leitura dos dados
                Log.w("Firestore", "Erro ao buscar reviews: ", exception)
            }
    }

    private fun updateUIWithAverages(colaboracao: Double, conhecimento: Double, iniciativa: Double, responsabilidade: Double) {
        //colaboracaoStars.rating = colaboracao.toFloat()
        //conhecimentoStars.rating = conhecimento.toFloat()
        //iniciativaStars.rating = iniciativa.toFloat()
        //responsabilidadeStars.rating = responsabilidade.toFloat()
    }


}