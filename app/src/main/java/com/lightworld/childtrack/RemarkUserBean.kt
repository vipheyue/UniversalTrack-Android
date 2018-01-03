package com.lightworld.childtrack

import com.orhanobut.logger.Logger


/**
 * Created by heyue on 2018/1/3.
 */
data class RemarkUserBean(var entityName: String, var remarkName: String = ""){

    override fun hashCode(): Int {
        return entityName.hashCode() + remarkName .hashCode()
    }

    override fun equals(obj: Any?): Boolean {

        if (obj !is RemarkUserBean)
            return false

        val p = obj as RemarkUserBean?
        Logger.d(this.entityName + "...equals.." + p!!.entityName)

        return this.entityName.equals(p!!.entityName) && this.remarkName === p!!.remarkName
    }
}
