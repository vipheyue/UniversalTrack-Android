package com.lightworld.childtrack

import android.content.Context
import java.io.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


//var save : LoginSaveBean by Preference<LoginSaveBean>(App.getInstance(),Constant.LOCAL_SAVE_LOGIN_MODEL_KEY, LoginSaveBean("0","0"))
//注意 这条语句我们要放在全局变量来读取，当你改变值或者值被改变时，这里的值会自己提交和同步。
/**
 * author: smart
 * time: 2016/10/26
 * 封装好SharedPreferences 并使用 序列化和反序列化来存储数据
 * 注意要在全局环境使用
 */
class Preference<T>(val context : Context, val name : String, val default : T) : ReadWriteProperty<Any?, T> {

    val prefs by lazy{context.getSharedPreferences("Realnen",Context.MODE_PRIVATE)}


    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return findPreference(name,default)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putPreference(name,value)
    }

    /**
     * 查找数据 返回给调用方法一个具体的对象
     * 如果查找不到类型就采用反序列化方法来返回类型
     * default是默认对象 以防止会返回空对象的异常
     * 即如果name没有查找到value 就返回默认的序列化对象，然后经过反序列化返回
     */
    private fun <A> findPreference(name :String,default: A) : A = with(prefs){
        val res : Any = when(default){
            is Long -> getLong(name,default)
            is String -> getString(name,default)
            is Int -> getInt(name,default)
            is Boolean -> getBoolean(name,default)
            is Float -> getFloat(name,default)
            else -> deSerialization(getString(name,serialize(default)))
        }
        res as A
    }

    private fun <A> putPreference(name :String,value: A) = with(prefs.edit()){
        when(value){
            is Long -> putLong(name,value)
            is String -> putString(name,value)
            is Int -> putInt(name,value)
            is Boolean -> putBoolean(name,value)
            is Float -> putFloat(name,value)
            else -> putString(name,serialize(value))
        }.apply()

    }

    /**
     * 删除全部数据
     */
    fun clearPreference(){
        prefs.edit().clear().commit()
    }

    /**
     * 根据key删除存储数据
     */
    fun clearPreference(key : String){
        prefs.edit().remove(key).commit()
    }

    /**
     * 序列化对象

     * @param person
     * *
     * @return
     * *
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun<A> serialize(obj: A): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(
                byteArrayOutputStream)
        objectOutputStream.writeObject(obj)
        var serStr = byteArrayOutputStream.toString("ISO-8859-1")
        serStr = java.net.URLEncoder.encode(serStr, "UTF-8")
        objectOutputStream.close()
        byteArrayOutputStream.close()
        return serStr
    }

    /**
     * 反序列化对象

     * @param str
     * *
     * @return
     * *
     * @throws IOException
     * *
     * @throws ClassNotFoundException
     */
    @Throws(IOException::class, ClassNotFoundException::class)
    private fun<A> deSerialization(str: String): A {
        val redStr = java.net.URLDecoder.decode(str, "UTF-8")
        val byteArrayInputStream = ByteArrayInputStream(
                redStr.toByteArray(charset("ISO-8859-1")))
        val objectInputStream = ObjectInputStream(
                byteArrayInputStream)
        val obj = objectInputStream.readObject() as A
        objectInputStream.close()
        byteArrayInputStream.close()
        return obj
    }
}