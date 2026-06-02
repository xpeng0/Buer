package com.cscyxp.bookkeeping.`data`.dao

import androidx.collection.LongSparseArray
import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndex
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.room.util.recursiveFetchLongSparseArray
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteStatement
import com.cscyxp.bookkeeping.`data`.entity.CategoryEntity
import com.cscyxp.bookkeeping.`data`.entity.TransactionEntity
import com.cscyxp.bookkeeping.`data`.entity.TransactionEntityWithCategoryEntity
import javax.`annotation`.processing.Generated
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlin.text.StringBuilder
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class TransactionDao_Impl(
  __db: RoomDatabase,
) : TransactionDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfTransactionEntity: EntityInsertAdapter<TransactionEntity>

  private val __updateAdapterOfTransactionEntity: EntityDeleteOrUpdateAdapter<TransactionEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfTransactionEntity = object : EntityInsertAdapter<TransactionEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `transactions` (`id`,`categoryId`,`title`,`type`,`amount`,`date`) VALUES (nullif(?, 0),?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: TransactionEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.categoryId)
        statement.bindText(3, entity.title)
        statement.bindLong(4, entity.type.toLong())
        statement.bindDouble(5, entity.amount)
        statement.bindLong(6, entity.date)
      }
    }
    this.__updateAdapterOfTransactionEntity = object : EntityDeleteOrUpdateAdapter<TransactionEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `transactions` SET `id` = ?,`categoryId` = ?,`title` = ?,`type` = ?,`amount` = ?,`date` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: TransactionEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.categoryId)
        statement.bindText(3, entity.title)
        statement.bindLong(4, entity.type.toLong())
        statement.bindDouble(5, entity.amount)
        statement.bindLong(6, entity.date)
        statement.bindLong(7, entity.id)
      }
    }
  }

  public override suspend fun insert(transaction: TransactionEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfTransactionEntity.insert(_connection, transaction)
  }

  public override suspend fun insertList(transactions: List<TransactionEntity>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfTransactionEntity.insert(_connection, transactions)
  }

  public override suspend fun updateTransaction(transaction: TransactionEntity): Int = performSuspending(__db, false, true) { _connection ->
    var _result: Int = 0
    _result += __updateAdapterOfTransactionEntity.handle(_connection, transaction)
    _result
  }

  public override fun getAllTransactions(): Flow<List<TransactionEntity>> {
    val _sql: String = "SELECT * FROM transactions ORDER BY date DESC"
    return createFlow(__db, false, arrayOf("transactions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCategoryId: Int = getColumnIndexOrThrow(_stmt, "categoryId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _result: MutableList<TransactionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransactionEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpCategoryId: Long
          _tmpCategoryId = _stmt.getLong(_columnIndexOfCategoryId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpType: Int
          _tmpType = _stmt.getLong(_columnIndexOfType).toInt()
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_columnIndexOfDate)
          _item = TransactionEntity(_tmpId,_tmpCategoryId,_tmpTitle,_tmpType,_tmpAmount,_tmpDate)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllTransactionsWithCategory(): Flow<List<TransactionEntityWithCategoryEntity>> {
    val _sql: String = "SELECT * FROM transactions ORDER BY date DESC"
    return createFlow(__db, false, arrayOf("categories", "transactions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCategoryId: Int = getColumnIndexOrThrow(_stmt, "categoryId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _collectionCategoryEntity: LongSparseArray<CategoryEntity?> = LongSparseArray<CategoryEntity?>()
        while (_stmt.step()) {
          val _tmpKey: Long
          _tmpKey = _stmt.getLong(_columnIndexOfCategoryId)
          _collectionCategoryEntity.put(_tmpKey, null)
        }
        _stmt.reset()
        __fetchRelationshipcategoriesAscomCscyxpBookkeepingDataEntityCategoryEntity(_connection, _collectionCategoryEntity)
        val _result: MutableList<TransactionEntityWithCategoryEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransactionEntityWithCategoryEntity
          val _tmpTransactionEntity: TransactionEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpCategoryId: Long
          _tmpCategoryId = _stmt.getLong(_columnIndexOfCategoryId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpType: Int
          _tmpType = _stmt.getLong(_columnIndexOfType).toInt()
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_columnIndexOfDate)
          _tmpTransactionEntity = TransactionEntity(_tmpId,_tmpCategoryId,_tmpTitle,_tmpType,_tmpAmount,_tmpDate)
          val _tmpCategoryEntity: CategoryEntity?
          val _tmpKey_1: Long
          _tmpKey_1 = _stmt.getLong(_columnIndexOfCategoryId)
          _tmpCategoryEntity = _collectionCategoryEntity.get(_tmpKey_1)
          _item = TransactionEntityWithCategoryEntity(_tmpTransactionEntity,_tmpCategoryEntity)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getTransactions(
    startMonthTs: Long,
    endMonthTs: Long,
    categoryId: Long?,
  ): Flow<List<TransactionEntityWithCategoryEntity>> {
    val _sql: String = "SELECT * FROM transactions WHERE (? IS NULL OR ? IS NULL OR date BETWEEN ? AND ?) AND (? IS NULL OR categoryId = ?) ORDER BY date DESC"
    return createFlow(__db, false, arrayOf("categories", "transactions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, startMonthTs)
        _argIndex = 2
        _stmt.bindLong(_argIndex, endMonthTs)
        _argIndex = 3
        _stmt.bindLong(_argIndex, startMonthTs)
        _argIndex = 4
        _stmt.bindLong(_argIndex, endMonthTs)
        _argIndex = 5
        if (categoryId == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, categoryId)
        }
        _argIndex = 6
        if (categoryId == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, categoryId)
        }
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCategoryId: Int = getColumnIndexOrThrow(_stmt, "categoryId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _collectionCategoryEntity: LongSparseArray<CategoryEntity?> = LongSparseArray<CategoryEntity?>()
        while (_stmt.step()) {
          val _tmpKey: Long
          _tmpKey = _stmt.getLong(_columnIndexOfCategoryId)
          _collectionCategoryEntity.put(_tmpKey, null)
        }
        _stmt.reset()
        __fetchRelationshipcategoriesAscomCscyxpBookkeepingDataEntityCategoryEntity(_connection, _collectionCategoryEntity)
        val _result: MutableList<TransactionEntityWithCategoryEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransactionEntityWithCategoryEntity
          val _tmpTransactionEntity: TransactionEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpCategoryId: Long
          _tmpCategoryId = _stmt.getLong(_columnIndexOfCategoryId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpType: Int
          _tmpType = _stmt.getLong(_columnIndexOfType).toInt()
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_columnIndexOfDate)
          _tmpTransactionEntity = TransactionEntity(_tmpId,_tmpCategoryId,_tmpTitle,_tmpType,_tmpAmount,_tmpDate)
          val _tmpCategoryEntity: CategoryEntity?
          val _tmpKey_1: Long
          _tmpKey_1 = _stmt.getLong(_columnIndexOfCategoryId)
          _tmpCategoryEntity = _collectionCategoryEntity.get(_tmpKey_1)
          _item = TransactionEntityWithCategoryEntity(_tmpTransactionEntity,_tmpCategoryEntity)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  private fun __fetchRelationshipcategoriesAscomCscyxpBookkeepingDataEntityCategoryEntity(_connection: SQLiteConnection, _map: LongSparseArray<CategoryEntity?>) {
    if (_map.isEmpty()) {
      return
    }
    if (_map.size() > 999) {
      recursiveFetchLongSparseArray(_map, false) { _tmpMap ->
        __fetchRelationshipcategoriesAscomCscyxpBookkeepingDataEntityCategoryEntity(_connection, _tmpMap)
      }
      return
    }
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT `id`,`parent_id`,`name`,`type`,`icon` FROM `categories` WHERE `id` IN (")
    val _inputSize: Int = _map.size()
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    val _stmt: SQLiteStatement = _connection.prepare(_sql)
    var _argIndex: Int = 1
    for (i in 0 until _map.size()) {
      val _item: Long = _map.keyAt(i)
      _stmt.bindLong(_argIndex, _item)
      _argIndex++
    }
    try {
      val _itemKeyIndex: Int = getColumnIndex(_stmt, "id")
      if (_itemKeyIndex == -1) {
        return
      }
      val _columnIndexOfId: Int = 0
      val _columnIndexOfParentId: Int = 1
      val _columnIndexOfName: Int = 2
      val _columnIndexOfType: Int = 3
      val _columnIndexOfIcon: Int = 4
      while (_stmt.step()) {
        val _tmpKey: Long
        _tmpKey = _stmt.getLong(_itemKeyIndex)
        if (_map.containsKey(_tmpKey)) {
          val _item_1: CategoryEntity?
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpParentId: Long?
          if (_stmt.isNull(_columnIndexOfParentId)) {
            _tmpParentId = null
          } else {
            _tmpParentId = _stmt.getLong(_columnIndexOfParentId)
          }
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpType: Int
          _tmpType = _stmt.getLong(_columnIndexOfType).toInt()
          val _tmpIcon: String
          _tmpIcon = _stmt.getText(_columnIndexOfIcon)
          _item_1 = CategoryEntity(_tmpId,_tmpParentId,_tmpName,_tmpType,_tmpIcon)
          _map.put(_tmpKey, _item_1)
        }
      }
    } finally {
      _stmt.close()
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
