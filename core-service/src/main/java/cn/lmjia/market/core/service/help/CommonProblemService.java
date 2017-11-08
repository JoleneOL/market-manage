package cn.lmjia.market.core.service.help;

import cn.lmjia.market.core.entity.help.CommonProblem;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 常见问题服务
 */
public interface CommonProblemService {

    /**
     * @return 返回常见问题对象
     */
    @Transactional(readOnly = true)
    CommonProblem getOne(long id);

    /**
     * 添加与修改常见问题
     * 默认问题即被加入热门展示中。
     * @param id 常见问题id,添加时可以为null.
     * @param title 标题
     * @param weight 权重
     * @param content 内容
     */
    @Transactional
    CommonProblem addAndEditCommonProblem(Long id, String title, int weight, String content);

    /**
     * @return 所有直接展示在微信端的常见问题.
     */
    List<CommonProblem> findByWeight();

    /**
     * @param keyword 关键字
     * @return 返回所有包含关键字的对象.
     */
    List<CommonProblem> findByTitle(String keyword);
}
