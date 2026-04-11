package com.pavsod.pavsodbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pavsod.pavsodbackend.dto.UserLoginDTO;
import com.pavsod.pavsodbackend.entity.Task;
import com.pavsod.pavsodbackend.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    User selectByPhoneAndPassword(UserLoginDTO dto);

    Integer countCurrentMonthDetectionCount(@Param("userId") Long userId);

    Integer sumTotalDetectionDuration(@Param("userId") Long userId);

    User selectByUserId(Long id);

    /*
        这里IDEA可能会有误报，忽略即可
        IDE 插件误将统计查询的 List<Map<String,Object>>（每行数据一个 Map）当成了对象映射的 Map<Key, Entity>（需要 @MapKey 指定主键作为 Key），
        因为两者都包含 Map 关键字，插件静态分析无法区分泛型参数的具体结构，机械地触发"返回 Map 必须加注解"的规则。
     */
    List<Map<String, Object>> selectLast7DaysTaskCount(@Param("userId") Long userId);

    List<Task> select5LastRecords(@Param("userId") Long userId);
}
