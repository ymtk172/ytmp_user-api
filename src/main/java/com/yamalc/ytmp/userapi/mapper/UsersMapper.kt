package com.yamalc.ytmp.userapi.mapper

import com.yamalc.ytmp.userapi.domain.UserProperties
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

@Mapper
interface UsersMapper {
    @Select("SELECT user_id, display_name " +
            "FROM user_properties " +
            "where user_id = #{userId}")
    fun select(userId: String): UserProperties

    @Insert("INSERT INTO user_properties " +
            "(user_id, display_name) " +
            "values " +
            "(#{userId}, #{displayName})")
    fun insert(userId: String, displayName: String): Int
}
