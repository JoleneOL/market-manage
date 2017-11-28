package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.rows.DepotRows;
import me.jiangcai.crud.row.RowCustom;
import me.jiangcai.crud.row.RowDefinition;
import me.jiangcai.crud.row.supplier.JQueryDataTableDramatizer;
import me.jiangcai.jpa.entity.support.Address;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.haier.entity.HaierDepot;
import me.jiangcai.logistics.repository.DepotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

/**
 * 仓库管理, 具有Root权限和供应链权限可以操作
 * @author CJ
 */
@Controller
@PreAuthorize("hasAnyRole('ROOT','"+ Login.ROLE_SUPPLY_CHAIN+"')")
public class ManageDepotController {

    @Autowired
    private DepotRepository depotRepository;
    @Autowired
    private ConversionService conversionService;

    @PreAuthorize("hasAnyRole('ROOT','"+ Login.ROLE_SUPPLY_CHAIN+"','"+Login.ROLE_LOOK+"')")
    @GetMapping("/manageDepot")
    public String index() {
        return "_depotManage.html";
    }

    @GetMapping("/manageDepotAdd")
    public String toAdd() {
        return "_depotAdd.html";
    }

    @PreAuthorize("hasAnyRole('ROOT','"+ Login.ROLE_SUPPLY_CHAIN+"','"+Login.ROLE_LOOK+"')")
    @GetMapping("/manage/depotList")
    @RowCustom(distinct = true, dramatizer = JQueryDataTableDramatizer.class)
    public RowDefinition data() {
        return new DepotRows(time -> conversionService.convert(time, String.class)) {

            @Override
            public Specification<Depot> specification() {
                return null;
            }
        };
    }

    @PostMapping("/manage/depotList")
    public String add(String name, Address address, String haierCode, String type, String chargePeopleName
            , String chargePeopleMobile) {
        Depot depot;
        if ("HaierDepot".equalsIgnoreCase(type)) {
            HaierDepot haierDepot = new HaierDepot();
            haierDepot.setHaierCode(haierCode);
            depot = haierDepot;
        } else {
            depot = new Depot();
        }
        depot.setEnable(true);
        depot.setCreateTime(LocalDateTime.now());
        depot.setName(name);
        depot.setAddress(address);
        depot.setChargePeopleName(chargePeopleName);
        depot.setChargePeopleMobile(chargePeopleMobile);
        depotRepository.save(depot);
        return "redirect:/manageDepot";
    }

    @PutMapping("/manage/depotList/{id}/disable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void disable(@PathVariable("id") long id) {
        depotRepository.getOne(id).setEnable(false);
    }

    @PutMapping("/manage/depotList/{id}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void enable(@PathVariable("id") long id) {
        depotRepository.getOne(id).setEnable(true);
    }


}
