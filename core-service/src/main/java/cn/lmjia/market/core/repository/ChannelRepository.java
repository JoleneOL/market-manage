package cn.lmjia.market.core.repository;

import cn.lmjia.market.core.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author CJ
 */
public interface ChannelRepository extends JpaRepository<Channel, Long>, JpaSpecificationExecutor<Channel> {
}
