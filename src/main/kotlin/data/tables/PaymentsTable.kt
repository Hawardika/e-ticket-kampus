//package app.data.tables
//import org.jetbrains.exposed.dao.id.LongIdTable
//import org.jetbrains.exposed.sql.javatime.timestamp
//object PaymentsTable : LongIdTable("payments") {
//    val order_id = Int()
//    val amount = float("amount")
//    val method
//    val email = varchar("email", 150).uniqueIndex()
//    val phone = varchar("phone", 50).nullable()
//    val createdAt = timestamp("created_at").clientDefault { java.time.Instant.now() }
//}