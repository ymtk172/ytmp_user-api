package com.yamalc.ytmp.userapi.mapper

import com.yamalc.ytmp.userapi.domain.Users
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

@Mapper
interface UsersMapper {
    @Select("SELECT * FROM public.users where user_id = #{userId}")
    fun select(userId: String): Users
}