CREATE OR REPLACE VIEW `UsageStock`
AS
  SELECT
    concat(`PRODUCT_CODE`, `DEPOT_ID`) AS `ID`,
    `PRODUCT_CODE`,
    `DEPOT_ID`,
    sum(`AMOUNT`)                      AS `AMOUNT`
  FROM UsageStockInfo
  GROUP BY `PRODUCT_CODE`, `DEPOT_ID`;