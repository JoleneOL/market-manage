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