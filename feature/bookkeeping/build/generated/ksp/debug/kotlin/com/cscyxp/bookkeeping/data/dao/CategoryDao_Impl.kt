package com.cscyxp.bookkeeping.`data`.dao

import androidx.collection.LongSparseArray
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
import com.cscyxp.bookkeeping.`data`.entity.CategoryEntityWithChildren
import javax.`annotation`.processing.Generated
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
public class CategoryDao_Impl(
  __db: RoomDatabase,
) : CategoryDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfCategoryEntity: EntityInsertAdapter<CategoryEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfCategoryEntity = object : EntityInsertAdapter<CategoryEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `categories` (`id`,`parent_id`,`name`,`type`,`icon`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: CategoryEntity) {
        statement.bindLong(1, entity.id)
        val _tmpParentId: Long? = entity.parentId
        if (_tmpParentId == null) {
          statement.bindNull(2)
        } else {
          statement.bindLong(2, _tmpParentId)
        }
        statement.bindText(3, entity.name)
        statement.bindLong(4, entity.type.toLong())
        statement.bindText(5, entity.icon)
      }
    }
  }

  public override suspend fun insert(categoryEntity: CategoryEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfCategoryEntity.insert(_connection, categoryEntity)
  }

  public override suspend fun insertList(categoryEntities: List<CategoryEntity>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfCategoryEntity.insert(_connection, categoryEntities)
  }

  public override fun getAllCategoriesFlow(): Flow<List<CategoryEntity>> {
    val _sql: String = "SELECT * FROM categories ORDER BY id"
    return createFlow(__db, false, arrayOf("categories")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfParentId: Int = getColumnIndexOrThrow(_stmt, "parent_id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfIcon: Int = getColumnIndexOrThrow(_stmt, "icon")
        val _result: MutableList<CategoryEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: CategoryEntity
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
          _item = CategoryEntity(_tmpId,_tmpParentId,_tmpName,_tmpType,_tmpIcon)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllCategories(): List<CategoryEntity> {
    val _sql: String = "SELECT * FROM categories ORDER BY id"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfParentId: Int = getColumnIndexOrThrow(_stmt, "parent_id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfIcon: Int = getColumnIndexOrThrow(_stmt, "icon")
        val _result: MutableList<CategoryEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: CategoryEntity
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
          _item = CategoryEntity(_tmpId,_tmpParentId,_tmpName,_tmpType,_tmpIcon)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTopCategories(): List<CategoryEntityWithChildren> {
    val _sql: String = "SELECT * FROM categories WHERE parent_id IS NULL ORDER BY id"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfParentId: Int = getColumnIndexOrThrow(_stmt, "parent_id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfIcon: Int = getColumnIndexOrThrow(_stmt, "icon")
        val _collectionChildren: LongSparseArray<MutableList<CategoryEntity>> = LongSparseArray<MutableList<CategoryEntity>>()
        while (_stmt.step()) {
          val _tmpKey: Long
          _tmpKey = _stmt.getLong(_columnIndexOfId)
          if (!_collectionChildren.containsKey(_tmpKey)) {
            _collectionChildren.put(_tmpKey, mutableListOf())
          }
        }
        _stmt.reset()
        __fetchRelationshipcategoriesAscomCscyxpBookkeepingDataEntityCategoryEntity(_connection, _collectionChildren)
        val _result: MutableList<CategoryEntityWithChildren> = mutableListOf()
        while (_stmt.step()) {
          val _item: CategoryEntityWithChildren
          val _tmpCategoryEntity: CategoryEntity
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
          _tmpCategoryEntity = CategoryEntity(_tmpId,_tmpParentId,_tmpName,_tmpType,_tmpIcon)
          val _tmpChildrenCollection: MutableList<CategoryEntity>
          val _tmpKey_1: Long
          _tmpKey_1 = _stmt.getLong(_columnIndexOfId)
          _tmpChildrenCollection = checkNotNull(_collectionChildren.get(_tmpKey_1))
          _item = CategoryEntityWithChildren(_tmpCategoryEntity,_tmpChildrenCollection)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getTopCategories(type: Int?): Flow<List<CategoryEntityWithChildren>> {
    val _sql: String = "SELECT * FROM categories WHERE parent_id IS NULL AND (? IS NULL OR type = ?)ORDER BY id"
    return createFlow(__db, false, arrayOf("categories")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        if (type == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, type.toLong())
        }
        _argIndex = 2
        if (type == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, type.toLong())
        }
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfParentId: Int = getColumnIndexOrThrow(_stmt, "parent_id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfIcon: Int = getColumnIndexOrThrow(_stmt, "icon")
        val _collectionChildren: LongSparseArray<MutableList<CategoryEntity>> = LongSparseArray<MutableList<CategoryEntity>>()
        while (_stmt.step()) {
          val _tmpKey: Long
          _tmpKey = _stmt.getLong(_columnIndexOfId)
          if (!_collectionChildren.containsKey(_tmpKey)) {
            _collectionChildren.put(_tmpKey, mutableListOf())
          }
        }
        _stmt.reset()
        __fetchRelationshipcategoriesAscomCscyxpBookkeepingDataEntityCategoryEntity(_connection, _collectionChildren)
        val _result: MutableList<CategoryEntityWithChildren> = mutableListOf()
        while (_stmt.step()) {
          val _item: CategoryEntityWithChildren
          val _tmpCategoryEntity: CategoryEntity
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
          _tmpCategoryEntity = CategoryEntity(_tmpId,_tmpParentId,_tmpName,_tmpType,_tmpIcon)
          val _tmpChildrenCollection: MutableList<CategoryEntity>
          val _tmpKey_1: Long
          _tmpKey_1 = _stmt.getLong(_columnIndexOfId)
          _tmpChildrenCollection = checkNotNull(_collectionChildren.get(_tmpKey_1))
          _item = CategoryEntityWithChildren(_tmpCategoryEntity,_tmpChildrenCollection)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getSonCategories(parentId: Long): List<CategoryEntity> {
    val _sql: String = "SELECT * FROM categories WHERE parent_id = ? ORDER BY id"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, parentId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfParentId: Int = getColumnIndexOrThrow(_stmt, "parent_id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfIcon: Int = getColumnIndexOrThrow(_stmt, "icon")
        val _result: MutableList<CategoryEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: CategoryEntity
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
          _item = CategoryEntity(_tmpId,_tmpParentId,_tmpName,_tmpType,_tmpIcon)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  private fun __fetchRelationshipcategoriesAscomCscyxpBookkeepingDataEntityCategoryEntity(_connection: SQLiteConnection, _map: LongSparseArray<MutableList<CategoryEntity>>) {
    if (_map.isEmpty()) {
      return
    }
    if (_map.size() > 999) {
      recursiveFetchLongSparseArray(_map, true) { _tmpMap ->
        __fetchRelationshipcategoriesAscomCscyxpBookkeepingDataEntityCategoryEntity(_connection, _tmpMap)
      }
      return
    }
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT `id`,`parent_id`,`name`,`type`,`icon` FROM `categories` WHERE `parent_id` IN (")
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
      val _itemKeyIndex: Int = getColumnIndex(_stmt, "parent_id")
      if (_itemKeyIndex == -1) {
        return
      }
      val _columnIndexOfId: Int = 0
      val _columnIndexOfParentId: Int = 1
      val _columnIndexOfName: Int = 2
      val _columnIndexOfType: Int = 3
      val _columnIndexOfIcon: Int = 4
      while (_stmt.step()) {
        val _tmpKey: Long?
        if (_stmt.isNull(_itemKeyIndex)) {
          _tmpKey = null
        } else {
          _tmpKey = _stmt.getLong(_itemKeyIndex)
        }
        if (_tmpKey != null) {
          val _tmpRelation: MutableList<CategoryEntity>? = _map.get(_tmpKey)
          if (_tmpRelation != null) {
            val _item_1: CategoryEntity
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
            _tmpRelation.add(_item_1)
          }
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
