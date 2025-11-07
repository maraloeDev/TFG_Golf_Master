import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.runBlocking

fun repararFirestoreIds() = runBlocking {
    val db = FirebaseFirestore.getInstance()

    // 🔧 Reparar reservas sin id
    val reservasSnap = db.collection("reservas").get().await()
    for (doc in reservasSnap.documents) {
        val data = doc.data ?: continue
        val currentId = data["id"] as? String
        if (currentId.isNullOrBlank()) {
            db.collection("reservas").document(doc.id).update("id", doc.id).await()
            println("✅ Reserva ${doc.id} corregida")
        }
    }

    // 🔧 Reparar invitaciones sin id
    val invitacionesSnap = db.collection("invitaciones").get().await()
    for (doc in invitacionesSnap.documents) {
        val data = doc.data ?: continue
        val currentId = data["id"] as? String
        if (currentId.isNullOrBlank()) {
            db.collection("invitaciones").document(doc.id).update("id", doc.id).await()
            println("✅ Invitación ${doc.id} corregida")
        }
    }

    println("🎉 Reparación completada")
}
