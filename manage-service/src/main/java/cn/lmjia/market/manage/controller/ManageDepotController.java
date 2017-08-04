package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.repository.DepotRepository;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.supplier.JQueryDataTableDramatizer;
import cn.lmjia.market.core.rows.DepotRows;
import me.jiangcai.jpa.entity.support.Address;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.haier.entity.HaierDepot;
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
 * @author CJ
 */
@Controller
@PreAuthorize("hasRole('ROOT')")
public class ManageDepotController {

    @Autowired
    private DepotRepository depotRepository;
    @Autowired
    private ConversionService conversionService;

    @GetMapping("/manageDepot")
    public String index() {
        return "_depotManage.html";
    }

    @GetMapping("/manageDepotAdd")
    public String toAdd() {
        return "_depotAdd.html";
    }

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
