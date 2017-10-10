package me.jiangcai.logistics;

import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.entity.support.ShiftStatus;
import me.jiangcai.logistics.model.DeliverableOrderId;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 可以运递货品的订单
 *
 * @author CJ
 */
public interface DeliverableOrder {

    Log log = LogFactory.getLog(DeliverableOrder.class);

    /**
     * 新增用以安装的库存转移信息
     *
     * @param unit 库存转移信息
     */
    void addInstallStockShiftUnit(StockShiftUnit unit);

    /**
     * @param unit 新增库存转移信息
     */
    void addStockShiftUnit(StockShiftUnit unit);

    /**
     * @return 用以安装的库存转移信息
     */
    List<? extends StockShiftUnit> getInstallStockShiftUnit();

    /**
     * @return 总共所需物流的货品
     */
    Map<? extends Product, Integer> getTotalShipProduct();

    /**
     * @return 物流相关的库存转移订单, 可能为null
     */
    List<StockShiftUnit> getShipStockShiftUnit();

    /**
     * 即排除掉已经物流出去的货品
     *
     * @return 需要物流的信息
     */
    default Map<? extends Product, Integer> getWantShipProduct() {
        //noinspection unchecked
        final Map<Product, Integer> require = (Map<Product, Integer>) getTotalShipProduct();

        getShipStockShiftUnit().stream().filter(stockShiftUnit -> stockShiftUnit.getCurrentStatus() != ShiftStatus.reject)
                .forEach(stockShiftUnit
                        -> stockShiftUnit.getAmounts().forEach(((product, productBatch) -> {
                    // 减去 require
                    require.computeIfPresent(product, ((product1, integer) -> {
                        int now = integer - productBatch.getAmount();
                        if (now > 0)
                            return now;
                        if (now < 0)
                            log.error(this + "诡异了，已物流的总量大于总的需物流量:" + product.getCode());
                        return null;
                    }));
                })));

        // 移除因为一些复杂的运算导致的0需求量
        return require.entrySet().stream().filter(productIntegerEntry -> productIntegerEntry.getValue() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * 更新物流冗余信息并且切换当前状态
     * 可能切换为forInstall,forDeliver
     *
     * @return 是否都已完成物流（不包括安装）
     */
    default boolean updateLogisticsStatus() {
        // 是否所有订单都已失败
        if (getShipStockShiftUnit().stream().allMatch(stockShiftUnit -> stockShiftUnit.getCurrentStatus() == ShiftStatus.reject)) {
            log.debug("所有物流订单都已被拒绝接单，重新进入待发货状态:" + this);
            setAbleShip(true);
            switchToForDeliverStatus();
            return false;
        } else if (getWantShipProduct().isEmpty()) {
            log.debug("已物流所有所需货品:" + this);
            setAbleShip(false);
            // 已经无货需发了；如果还有货可发状态就无需关注了。
            // 现在确定是否都已经发完了
            if (getShipStockShiftUnit().stream()
                    .filter(stockShiftUnit -> stockShiftUnit.getCurrentStatus() != ShiftStatus.reject)
                    .allMatch(stockShiftUnit -> stockShiftUnit.getCurrentStatus() == ShiftStatus.success)) {
                switchToForInstallStatus();
                log.debug("同时所有物流已抵达");
                return true;
            } else
                log.debug("但是并非所有物流已抵达");
        } else {
            log.debug("还有部分物流未发:" + this);
            setAbleShip(true);
        }
        return false;
    }

    /**
     * 增加已安装的物流信息
     * 可能切换为afterSale
     *
     * @param unit 已完成安装的物流;可能为null
     * @return 是否都已完成物流（包括安装）
     */
    default boolean updateInstallationStatus(StockShiftUnit unit) {
        addInstallStockShiftUnit(unit);
        if (getWantShipProduct().isEmpty()) {
            log.debug("已物流所有所需货品:" + this);
            // 要么无需安装 要么已安装
            if (getShipStockShiftUnit().stream()
                    .filter(stockShiftUnit -> stockShiftUnit.getCurrentStatus() != ShiftStatus.reject)
                    .allMatch(stockShiftUnit ->
                            !stockShiftUnit.isInstallation() || getInstallStockShiftUnit().contains(stockShiftUnit))) {
                switchToLogisticsFinishStatus();

                log.debug("并且所有物流都已完成安装或者无需安装");
                return true;
            } else if (log.isDebugEnabled())
                log.debug("但是并非所有物流订单都已完成安装或者无需安装");
        }
        return false;
    }


    /**
     * 切换到需发起安装的状态
     */
    void switchToForInstallStatus();

    /**
     * 切换到需发起物流状态
     */
    void switchToForDeliverStatus();

    /**
     * 切换到开始物流之后的状态；即接受了物流指令
     */
    void switchToStartDeliverStatus();

    /**
     * 切换到物流以及安装完成状态
     */
    void switchToLogisticsFinishStatus();

    /**
     * @param b 是否允许继续发起物流
     */
    void setAbleShip(boolean b);

    /**
     * 可选实现
     *
     * @return 订单发货的目的地, 默认为null
     */
    LogisticsDestination getLogisticsDestination();

    default DeliverableOrderId getDeliverableOrderId() {
        return new DeliverableOrderId(getClass(), getRepresentationalId());
    }

    /**
     * @return 实体主键的输出描述
     */
    Serializable getRepresentationalId();

}
