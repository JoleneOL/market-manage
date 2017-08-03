package cn.lmjia.market.core.service;

import cn.lmjia.market.core.CoreServiceTest;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.channel.Channel;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class MainGoodServiceTest extends CoreServiceTest {

    @Autowired
    private MainGoodService mainGoodService;
    @Autowired
    private ChannelService channelService;

    @Test
    public void forSale() throws Exception {
        List<MainGood> originGoodList = mainGoodService.forSale();

        Channel channel = channelService.findByName("投融家");
        // 随便找一个设置为该渠道的
        channelService.setupChannel(originGoodList.stream().max(new RandomComparator()).orElse(null), channel);

        // 因为投融家为额外渠道 所以总量应该减少
        assertThat(mainGoodService.forSale().size())
                .isEqualTo(originGoodList.size() - 1);

    }

}