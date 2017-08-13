-- 定制一个视图
-- 获取当前 未结算状态的 可用库存
-- 大致返回结果会是
-- Product_Code,Depot_Id,Amount
CREATE OR REPLACE VIEW `UnSettlementUsageStock`
AS
  SELECT
    amounts.amounts_KEY AS `PRODUCT_CODE`,
    destDepot.ID        AS `DESTINATION_ID`,
    originDepot.ID      AS `ORIGIN_ID`,
    SUM(amounts.AMOUNT) AS 'AMOUNT'
  FROM STOCKSHIFTUNIT shift LEFT OUTER JOIN DEPOT destDepot
      ON ((destDepot.ID = shift.DESTINATION_ID) AND (shift.CURRENTSTATUS = 5))
    LEFT OUTER JOIN DEPOT originDepot
      ON ((originDepot.ID = shift.ORIGIN_ID) AND (shift.CURRENTSTATUS <> 2))
    , StockShiftUnit_AMOUNTS amounts
  WHERE (((
    ((shift.LOCKEDTIME IS NULL) OR (shift.LOCKEDTIME > (SELECT MAX(`TIME`)
                                                        FROM stocksettlement))))) AND
         ((amounts.StockShiftUnit_ID = shift.ID))
  )
  GROUP BY amounts.amounts_KEY, destDepot.ID, originDepot.ID;

CREATE OR REPLACE VIEW `UsageStock`
AS
  SELECT
    concat(`PRODUCT_CODE`, `DEPOT_ID`) AS `ID`,
    `PRODUCT_CODE`,
    `DEPOT_ID`,
    sum(`AMOUNT`)                      AS `AMOUNT`
  FROM (SELECT
          `PRODUCT_CODE` AS `PRODUCT_CODE`,
          `DEPOT_ID`     AS `DEPOT_ID`,
          `AMOUNT`       AS `AMOUNT`
        FROM stocksettlement_usablestock
        WHERE StockSettlement_ID = (SELECT `ID`
                                    FROM stocksettlement
                                    WHERE `TIME` = (SELECT max(`TIME`)
                                                    FROM stocksettlement))
        UNION SELECT
                `PRODUCT_CODE`   AS `PRODUCT_CODE`,
                `DESTINATION_ID` AS `DEPOT_ID`,
                `AMOUNT`         AS `AMOUNT`
              FROM unsettlementusagestock
              WHERE DESTINATION_ID IS NOT NULL
        UNION SELECT
                `PRODUCT_CODE` AS `PRODUCT_CODE`,
                `ORIGIN_ID`    AS `DEPOT_ID`,
                -`AMOUNT`      AS `AMOUNT`
              FROM unsettlementusagestock
              WHERE ORIGIN_ID IS NOT NULL) AS A
  GROUP BY `PRODUCT_CODE`, `DEPOT_ID`;