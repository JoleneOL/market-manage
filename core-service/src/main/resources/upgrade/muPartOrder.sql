INSERT INTO MainOrder_AMOUNTS (`MainOrder_ID`, `amounts_KEY`, `AMOUNTS`) SELECT
                                                                           `ID`,
                                                                           `GOOD_ID`,
                                                                           `AMOUNT`
                                                                         FROM MainOrder
                                                                         WHERE `GOOD_ID` IS NOT NULL;
UPDATE MainOrder
SET `GOODTOTALPRICEAMOUNTINDEPENDENT`         = `GOODTOTALPRICE` * `AMOUNT`
  , `GOODCOMMISSIONINGPRICEAMOUNTINDEPENDENT` = `GOODCOMMISSIONINGPRICE` * `AMOUNT`
WHERE `GOODTOTALPRICEAMOUNTINDEPENDENT` IS NULL OR `GOODCOMMISSIONINGPRICEAMOUNTINDEPENDENT` IS NULL;
UPDATE MainOrder AS O
  JOIN MainGood AS G ON O.GOOD_ID = G.`ID`
  JOIN Product AS P ON P.`CODE` = G.`PRODUCT_CODE`
SET `ORDERBODY` = concat(O.`AMOUNT`, concat(IFNULL(P.`UNIT`, '个'), P.`NAME`))
WHERE O.ORDERBODY IS NULL;
INSERT INTO MainOrderRecord_AMOUNTRECORD (`MainOrderRecord_ID`, `PRODUCTNAME`, `PRODUCTTYPE`, `AMOUNT`, `PRICE`, `COMMISSIONINGPRICE`) SELECT
                                                                                                                                         `R`.`ID`,
                                                                                                                                         `R`.`PRODUCTNAME`,
                                                                                                                                         `R`.`PRODUCTTYPE`,
                                                                                                                                         `R`.`AMOUNT`,
                                                                                                                                         `O`.`GOODTOTALPRICE`,
                                                                                                                                         `O`.`GOODCOMMISSIONINGPRICE`
                                                                                                                                       FROM
                                                                                                                                         MainOrderRecord AS R
                                                                                                                                         JOIN
                                                                                                                                         MainOrder AS O
                                                                                                                                           ON
                                                                                                                                             O.`RECORD_ID`
                                                                                                                                             =
                                                                                                                                             R.`ID`
                                                                                                                                       WHERE
                                                                                                                                         R.PRODUCTNAME
                                                                                                                                         IS
                                                                                                                                         NOT
                                                                                                                                         NULL;