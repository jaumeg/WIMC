package es.utopik.wimc

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery

//@Entity(tableName = "todo_items")
@Entity()
data class TodoEntity(
//    constructor ( val title : String, content:String="") : this(title=title, content=content)

    @PrimaryKey(autoGenerate = true)
    var id: Long=0,

    //@ColumnInfo(name = "title")
    var title: String="",

    //@ColumnInfo(name = "content")
    var content: String=""



)



@Dao
interface TodoDao {
    @Query("SELECT * FROM TodoEntity order by id desc")
    fun getAll(): List<TodoEntity>


    @Query("SELECT * FROM TodoEntity where id=:id")
    fun findByID(id : Long): TodoEntity


    @Query("SELECT * FROM TodoEntity where id>=:id")
    fun findByMinID(id : Long): List<TodoEntity>

    @Query("SELECT * FROM TodoEntity WHERE title LIKE :title")
    fun findByTitle(title: String): List<TodoEntity>

    //returns affectedRows
    @Query("delete FROM TodoEntity ")
    fun deleteALL() : Int

    //04.01.2020
    @Query("SELECT * FROM TodoEntity WHERE :where")
    fun findByWhere(where: String): List<TodoEntity>

    //04.01.2020 --> OK!!
    @RawQuery()
    fun execSQL(query: SupportSQLiteQuery) : Long


/*
    //04.01.2020
    @Query("SELECT * FROM TodoEntity WHERE id>=:minID")
    fun findByMinID(minID: Int): List<TodoEntity>
*/



    @Insert
    fun insertAll(vararg todo: TodoEntity)

    @Insert
    fun insert( todo: TodoEntity) : Long

    @Delete
    fun delete(todo: TodoEntity)

    @Update
    fun updateTodo(vararg todos: TodoEntity)
}


@Database(entities = arrayOf(TodoEntity::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}


/*
@Database(
    entities = [TodoEntity::class, TaskEntry::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase(){
    abstract fun TodoDao(): TodoDao
    abstract fun TaskDao(): TaskDao

    companion object {
        @Volatile private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context)= instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also { instance = it}
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
            AppDatabase::class.java, "todo-list.db")
            .build()
    }
}*/
