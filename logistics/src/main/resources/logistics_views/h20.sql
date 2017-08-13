CREATE OR REPLACE VIEW `UnSettlementUsageStock`
AS
  SELECT
    amounts.amounts_KEY AS `PRODUCT_CODE`,
    destDepot.ID        AS `DESTINATION_ID`,
    originDepot.ID      AS `ORIGIN_ID`,
    SUM(amounts.AMOUNT) AS `AMOUNT`
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