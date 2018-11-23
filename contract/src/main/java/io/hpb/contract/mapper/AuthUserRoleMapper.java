package io.hpb.contract.mapper;

import io.hpb.contract.entity.AuthUserRole;
import io.hpb.contract.example.AuthUserRoleExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AuthUserRoleMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_user_role
     *
     * @mbg.generated
     */
    long countByExample(AuthUserRoleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_user_role
     *
     * @mbg.generated
     */
    int deleteByExample(AuthUserRoleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_user_role
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_user_role
     *
     * @mbg.generated
     */
    int insert(AuthUserRole record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_user_role
     *
     * @mbg.generated
     */
    int insertSelective(AuthUserRole record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_user_role
     *
     * @mbg.generated
     */
    List<AuthUserRole> selectByExample(AuthUserRoleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_user_role
     *
     * @mbg.generated
     */
    AuthUserRole selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_user_role
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") AuthUserRole record, @Param("example") AuthUserRoleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_user_role
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") AuthUserRole record, @Param("example") AuthUserRoleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_user_role
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(AuthUserRole record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_user_role
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(AuthUserRole record);

    AuthUserRole selectByUserId(Integer userId);
}