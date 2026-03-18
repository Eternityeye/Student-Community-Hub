package com.ruoyi.system.mapper;

import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.SocialLike;

/**
 * 社交点赞 数据层
 *
 * @author ruoyi
 */
public interface SocialLikeMapper
{
    /**
     * 查询是否点赞
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 点赞记录
     */
    public SocialLike selectLikeByPostAndUser(@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * 新增点赞
     *
     * @param like 点赞信息
     * @return 结果
     */
    public int insertLike(SocialLike like);

    /**
     * 删除点赞
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 结果
     */
    public int deleteLikeByPostAndUser(@Param("postId") Long postId, @Param("userId") Long userId);
}
