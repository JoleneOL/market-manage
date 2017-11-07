package cn.lmjia.market.core.service.help;

import cn.lmjia.market.core.entity.help.CommonProblem;

import java.util.List;

/**
 * 常见问题服务
 */
public interface CommonProblemService {

    /**
     * @return 返回常见问题对象
     */
    CommonProblem getOne(long id);

    /**
     * 添加与修改常见问题
     * @param id 常见问题id,添加时可以为null.
     * @param title 标题
     * @param weight
     * @param content 内容
     */
    CommonProblem addAndEditCommonProblem(Long id, String title, Integer weight, String content);

    /**
     * @return 所有直接展示在微信端的常见问题.
     */
    List<CommonProblem> findByIsWeigth();

    /**
     * @param keyword 关键字
     * @return 返回所有包含关键字的对象.
     */
    List<CommonProblem> findByTitle(String keyword);
}
