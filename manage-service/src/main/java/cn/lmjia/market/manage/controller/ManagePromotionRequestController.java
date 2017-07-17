package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.entity.request.PromotionRequest;
import cn.lmjia.market.core.entity.support.PromotionRequestStatus;
import cn.lmjia.market.core.repository.request.PromotionRequestRepository;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.supplier.JQueryDataTableDramatizer;
import cn.lmjia.market.core.service.AgentFinancingService;
import cn.lmjia.market.core.service.ContactWayService;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.dealer.service.AgentService;
import cn.lmjia.market.dealer.service.PromotionService;
import me.jiangcai.lib.resource.service.ResourceService;
import me.jiangcai.lib.seext.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 代理商管理员可进行
 *
 * @author CJ
 */
@Controller
@PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_AllAgent + "','" + Login.ROLE_PROMOTION + "')")
public class ManagePromotionRequestController {

    @Autowired
    private AgentFinancingService agentFinancingService;
    @Autowired
    private AgentService agentService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private ConversionService conversionService;
    @Autowired
    private ReadService readService;
    @Autowired
    private PromotionService promotionService;
    @Autowired
    private PromotionRequestRepository promotionRequestRepository;
    @Autowired
    private ContactWayService contactWayService;

    @GetMapping("/managePromotionRequest")
    public String index() {
        return "_agentUpdate.html";
    }

    /**
     * @param applicationDate quarter,month,all(一年)
     * @return
     */
    @GetMapping("/manage/promotionRequests")
    @RowCustom(distinct = true, dramatizer = JQueryDataTableDramatizer.class)
    public RowDefinition<PromotionRequest> data(String applicationDate, String mobile) {
        return PromotionRequest.Rows(applicationDate, mobile, readService, resourceService, conversionService);
    }


    @PutMapping("/manage/promotionRequests/{id}/rejected")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void rejected(@AuthenticationPrincipal Manager manager, @PathVariable("id") long id) {
        PromotionRequest request = promotionRequestRepository.getOne(id);

        request.setRequestStatus(PromotionRequestStatus.rejected);
        request.setChanger(manager);
        request.setChangeTime(LocalDateTime.now());
    }

    @PutMapping("/manage/promotionRequests/{id}/approved")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void approve(@AuthenticationPrincipal Manager manager, @PathVariable("id") long id
            , @RequestBody(required = false) String title) throws IOException {
        PromotionRequest request = promotionRequestRepository.getOne(id);

        request.setRequestStatus(PromotionRequestStatus.approved);
        request.setChanger(manager);
        request.setChangeTime(LocalDateTime.now());

        // 更新login信息
        Login login = request.getWhose();

        contactWayService.updateName(login, request.getName());
        contactWayService.updateAddress(login, request.getAddress());
        // 另设为临时资源
        String backTmpPath = "tmp/" + UUID.randomUUID().toString() + "." + FileUtils.fileExtensionName(request.getBackImagePath());
        String frontTmpPath = "tmp/" + UUID.randomUUID().toString() + "." + FileUtils.fileExtensionName(request.getFrontImagePath());
        resourceService.uploadResource(backTmpPath, resourceService.getResource(request.getBackImagePath()).getInputStream());
        resourceService.uploadResource(frontTmpPath, resourceService.getResource(request.getFrontImagePath()).getInputStream());
        String bTempPath;
        if (!StringUtils.isEmpty(request.getBusinessLicensePath())) {
            bTempPath = "tmp/" + UUID.randomUUID().toString() + "." + FileUtils.fileExtensionName(request.getBusinessLicensePath());
            resourceService.uploadResource(bTempPath, resourceService.getResource(request.getBusinessLicensePath()).getInputStream());
        } else
            bTempPath = null;
        contactWayService.updateIDCardImages(login, frontTmpPath, backTmpPath, bTempPath);

        // 少了记录代理费
        AgentLevel agentLevel = agentService.highestAgent(login);
        if (request.getType() >= 1) {
            // 成为代理商先
            if (agentLevel == null)
                agentLevel = promotionService.agentLevelUpgrade(login, null);
            agentLevel.setBeginDate(LocalDate.now());
            agentLevel.setEndDate(LocalDate.now().plusYears(1));
            agentLevel.setCreatedBy(manager);
            if (request.getPrice() != null)
                agentFinancingService.recordAgentFee(login, agentLevel, request.getPrice(), null, null);
        }
        if (request.getType() >= 2) {
            // 是否已经是代理商
            if (agentLevel.getLevel() > 3) {
                agentLevel = promotionService.agentLevelUpgrade(login, agentLevel);
            }
            agentLevel.setBeginDate(LocalDate.now());
            agentLevel.setEndDate(LocalDate.now().plusYears(1));
            agentLevel.setCreatedBy(manager);
            if (request.getPrice() != null)
                agentFinancingService.recordAgentFee(login, agentLevel, request.getPrice(), null, null);
        }
        if (request.getType() >= 3) {
            if (StringUtils.isEmpty(title))
                throw new IllegalStateException("请输入自定义代理等级");
            if (agentLevel.getLevel() > 2) {
                agentLevel = promotionService.agentLevelUpgrade(login, agentLevel);
            }
            agentLevel.setBeginDate(LocalDate.now());
            agentLevel.setEndDate(LocalDate.now().plusYears(1));
            agentLevel.setCreatedBy(manager);
            agentLevel.setLevelTitle(title);
            if (request.getPrice() != null)
                agentFinancingService.recordAgentFee(login, agentLevel, request.getPrice(), null, null);
        }
    }


}
