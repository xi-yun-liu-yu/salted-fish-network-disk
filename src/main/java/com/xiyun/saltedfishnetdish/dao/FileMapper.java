package com.xiyun.saltedfishnetdish.dao;

import com.xiyun.saltedfishnetdish.pojo.File;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface FileMapper {

    // 插入文件记录
    @Insert("INSERT INTO file (file_name, file_uuid, file_format, file_size, download_count, file_url, view_permission, creator_id, creator_nickname, created_at, updated_at) " +
            "VALUES (#{fileName}, #{fileUuid}, #{fileFormat}, #{fileSize}, #{downloadCount}, #{fileUrl}, #{viewPermission}, #{creatorId}, #{creatorNickname}, #{createdAt}, #{updatedAt})")
    int insertFile(File file);

    // 根据 ID 删除文件记录
    @Delete("DELETE FROM file WHERE file_uuid = #{id}")
    int deleteFileById(Long id);

    // 更新文件记录
    @Update("UPDATE file SET file_name = #{fileName}, file_uuid = #{fileUuid}, file_format = #{fileFormat}, file_size = #{fileSize}, " +
            "download_count = #{downloadCount}, file_url = #{fileUrl}, view_permission = #{viewPermission}, creator_id = #{creatorId}, " +
            "creator_nickname = #{creatorNickname}, updated_at = #{updatedAt} WHERE file_uuid = #{id}")
    int updateFile(File file);

    // 根据 ID 查询文件记录
    @Select("SELECT * FROM file WHERE file_uuid = #{id}")
    File selectFileById(Long id);

    // 查询所有文件记录
    @Select("SELECT * FROM file")
    List<File> selectAllFiles();
}