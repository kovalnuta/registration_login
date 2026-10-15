package com.example.registration_login

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DBHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    val LOG_TAG: String = "myLogs"

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "salon.db"

        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"

        const val TABLE_STATS = "stats"
        const val COLUMN_USER_NAME = "user_name"
        const val COLUMN_LOGIN_COUNT = "login_count"
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d(LOG_TAG, "--- onCreate database ---")

        db.execSQL(
            "create table $TABLE_USERS (" +
                    "$COLUMN_ID integer primary key autoincrement, " +
                    "$COLUMN_NAME text, " +
                    "$COLUMN_PHONE text, " +
                    "$COLUMN_EMAIL text, " +
                    "$COLUMN_PASSWORD text);"
        )
        db.execSQL(
            "create table $TABLE_STATS (" +
                    "$COLUMN_ID integer primary key autoincrement, " +
                    "$COLUMN_USER_NAME text unique, " +
                    "$COLUMN_LOGIN_COUNT integer);"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("drop table if exists $TABLE_USERS")
        db?.execSQL("drop table if exists $TABLE_STATS")
        onCreate(db!!)
    }

    fun register(name: String, phone: String, email: String, password: String): Boolean {
        return registerUser(name, phone, email, password)
    }

    fun registerUser(name: String, phone: String, email: String, password: String): Boolean {
        val db = writableDatabase
        val c = db.query(
            TABLE_USERS, arrayOf(COLUMN_EMAIL),
            "$COLUMN_EMAIL = ?", arrayOf(email),
            null, null, null
        )
        if (c.moveToFirst()) { c.close(); db.close(); return false }
        c.close()

        val cv = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_PHONE, phone)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
        }
        val rowId = db.insert(TABLE_USERS, null, cv)
        Log.d(LOG_TAG, "row inserted, ID = $rowId")
        db.close()
        return rowId > 0
    }

    fun login(email: String, password: String): String? {
        return loginUser(email, password)
    }

    fun loginUser(email: String, password: String): String? {
        val db = readableDatabase
        val c: Cursor = db.query(
            TABLE_USERS, arrayOf(COLUMN_NAME),
            "$COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(email, password),
            null, null, null
        )
        val userName = if (c.moveToFirst())
            c.getString(c.getColumnIndexOrThrow(COLUMN_NAME)) else null
        c.close()
        db.close()
        return userName
    }
    fun increaseLoginCount(userName: String) {
        val db = writableDatabase
        val c = db.query(
            TABLE_STATS, arrayOf(COLUMN_LOGIN_COUNT),
            "$COLUMN_USER_NAME = ?", arrayOf(userName),
            null, null, null
        )
        if (c.moveToFirst()) {
            val current = c.getInt(c.getColumnIndexOrThrow(COLUMN_LOGIN_COUNT))
            val cv = ContentValues().apply { put(COLUMN_LOGIN_COUNT, current + 1) }
            db.update(TABLE_STATS, cv, "$COLUMN_USER_NAME = ?", arrayOf(userName))
            Log.d(LOG_TAG, "increased login count for $userName → ${current + 1}")
        } else {
            val cv = ContentValues().apply {
                put(COLUMN_USER_NAME, userName)
                put(COLUMN_LOGIN_COUNT, 1)
            }
            db.insert(TABLE_STATS, null, cv)
            Log.d(LOG_TAG, "created stats row for $userName, count = 1")
        }
        c.close()
        db.close()
    }

    fun getAllStats(): List<Pair<String, Int>> {
        val db = readableDatabase
        val result = mutableListOf<Pair<String, Int>>()
        val c = db.query(
            TABLE_STATS,
            arrayOf(COLUMN_USER_NAME, COLUMN_LOGIN_COUNT),
            null, null, null, null,
            "$COLUMN_LOGIN_COUNT DESC"
        )
        while (c.moveToNext()) {
            val name = c.getString(c.getColumnIndexOrThrow(COLUMN_USER_NAME))
            val count = c.getInt(c.getColumnIndexOrThrow(COLUMN_LOGIN_COUNT))
            result.add(name to count)
        }
        c.close()
        db.close()
        Log.d(LOG_TAG, "getAllStats: returned ${result.size} rows")
        return result
    }
}