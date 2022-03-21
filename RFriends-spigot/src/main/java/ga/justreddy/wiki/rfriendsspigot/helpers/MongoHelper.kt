package ga.justreddy.wiki.rfriendsbungee.helpers

import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document


class MongoHelper(private val uri: String) {

    private lateinit var database: MongoDatabase
    fun connect() {
        val connectionString = MongoClientURI(uri)
        val mongoClient = MongoClient(connectionString)
        database = mongoClient.getDatabase("rfriends")
    }
    
    fun getDatabase(collectionName: String): MongoCollection<Document> {
        return database.getCollection(collectionName)
    }

}