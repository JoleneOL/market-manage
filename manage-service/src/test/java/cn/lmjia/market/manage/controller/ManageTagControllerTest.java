package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.Tag;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.repository.MainGoodRepository;
import cn.lmjia.market.core.repository.TagRepository;
import cn.lmjia.market.core.service.MainGoodService;
import cn.lmjia.market.manage.ManageServiceTest;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by helloztt on 2017/9/16.
 */
public class ManageTagControllerTest extends ManageServiceTest {
    private static final Log log = LogFactory.getLog(ManageTagControllerTest.class);
    private static final String TAG_LIST_URL = "/manage/tagList";
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private MainGoodService mainGoodService;
    @Autowired
    private MainGoodRepository mainGoodRepository;

    @Override
    protected Login allRunWith() {
        return newRandomManager(ManageLevel.root);
    }

    @Test
    public void index() throws Exception {
        driver.get("http://localhost/manageTag");
        assertThat(driver.getTitle())
                .isEqualTo("标签管理");
        driver.get("http://localhost/manageTagAdd");
        assertThat(driver.getTitle())
                .isEqualTo("新标签");
    }

    @Test
    public void toAdd() throws Exception {
        addNewTag();
    }

    @Test
    public void data() throws Exception {
        toAdd();
        mockMvc.perform(
                get(TAG_LIST_URL)
        )
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(similarJQueryDataTable("classpath:/manage-view/mock/tagList.json"));
    }

    @Test
    public void delete() throws Exception {
        toAdd();
        //随机找一个标签
        Tag tag = tagRepository.findAll().stream().max(new RandomComparator()).orElse(null);
        if (tag == null) {
            return;
        }
        //随机找一个商品，添加标签
        MainGood mainGood = mainGoodService.forSale().stream().max(new RandomComparator()).orElse(null);
        if (mainGood.getTags() == null)
            mainGood.setTags(new HashSet<>());
        mainGood.getTags().add(tag);
        mainGoodRepository.saveAndFlush(mainGood);
        log.debug("set good tag success");
        //确保现在是有这个标签的
        assertThat(mainGoodRepository.findOne(mainGood.getId()).getTags()).contains(tag);

        mockMvc.perform(delete(TAG_LIST_URL)
                .param("name",tag.getName()))
                .andExpect(status().is2xxSuccessful());

        assertThat(tagRepository.findOne(tag.getName()))
                .as("特定标签" + tag.getName() + "已被成功删除")
                .isNull();
        //商品的标签消失了
        mainGood = mainGoodRepository.findOne(mainGood.getId());
        if (mainGood.getTags() != null) {
            assertThat(mainGood.getTags()).doesNotContain(tag);
        }
    }

    @Test
    public void testDelete() throws Exception {
        int randomNum = random.nextInt(10);
        while (randomNum-- > 0) {
            toAdd();
        }
        //随机找一个标签
        Tag tag = tagRepository.findAll().stream().max(new RandomComparator()).orElse(null);
        if (tag == null) {
            return;
        }
        //随机找一个商品，添加标签
        MainGood mainGood = mainGoodService.forSale().stream().max(new RandomComparator()).orElse(null);
        if (mainGood.getTags() == null)
            mainGood.setTags(new HashSet<>());
        mainGood.getTags().add(tag);
        mainGoodRepository.save(mainGood);
        //确保现在是有这个标签的
        assertThat(mainGoodRepository.findOne(mainGood.getId()).getTags()).contains(tag);
        assertThat(mainGoodService.forSale(null,tag.getName())).contains(mainGood);
        //删除有这个标签的商品的标签
        List<MainGood> tagMainGood = mainGoodService.forSale(null,tag.getName());
        tagMainGood.forEach(good -> good.getTags().remove(tag));
        mainGoodRepository.save(tagMainGood);
        //确保现在没有标签
        if (!CollectionUtils.isEmpty(mainGood.getTags()))
            assertThat(mainGoodRepository.findOne(mainGood.getId()).getTags()).doesNotContain(tag);
        else
            assertThat(mainGoodRepository.findOne(mainGood.getId()).getTags()).isNull();
        assertThat(mainGoodService.forSale(null,tag.getName())).doesNotContain(mainGood);

    }

    @Test
    public void disable() throws Exception {
        changeDisable("/disable", true);
    }

    @Test
    public void enable() throws Exception {
        changeDisable("/enable", false);
    }

    @Test
    public void check() throws Exception{
        toAdd();
        //随机找一个标签
        Tag tag = tagRepository.findAll().stream().max(new RandomComparator()).orElse(null);
        if (tag == null) {
            return;
        }
        MvcResult result = mockMvc.perform(get(TAG_LIST_URL + "/check")
                .param("name",tag.getName()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualToIgnoringCase("false");

        result = mockMvc.perform(get(TAG_LIST_URL+ "/check")
                .param("name", RandomStringUtils.randomAlphabetic(10)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualToIgnoringCase("true");
    }

    private void changeDisable(String path, boolean targetDisable) throws Exception {
        toAdd();
        //随机找一个标签
        Tag tag = tagRepository.findAll().stream().max(new RandomComparator()).orElse(null);
        if (tag == null) {
            return;
        }
        tag.setDisabled(!targetDisable);
        tagRepository.save(tag);

        mockMvc.perform(put(TAG_LIST_URL + "/"  + path)
                .param("name" ,tag.getName()))
                .andExpect(status().is2xxSuccessful());

        assertThat(tagRepository.findOne(tag.getName()).isDisabled())
                .isEqualTo(targetDisable);

    }

}