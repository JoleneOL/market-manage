CREATE OR REPLACE VIEW `LoginCommissionJournal`
AS
  SELECT
    concat('OC-', c.SOURCE_ID, '-', c.WHO_ID) AS `ID`,
    c.WHO_ID                                  AS `LOGIN_ID`,
    c.SOURCE_ID                               AS `MAIN_ORDER_ID`,
    oc.GENERATETIME                           AS `HAPPEN_TIME`,
    0                                         AS `TYPE`,
    sum(c.AMOUNT)                             AS `CHANGED`
  FROM commission AS c
    JOIN ordercommission AS oc ON (oc.SOURCE_ID = c.SOURCE_ID AND oc.REFUND = c.REFUND)
  GROUP BY c.SOURCE_ID, oc.GENERATETIME
  UNION SELECT
          concat('WR-', wr.ID, '-', wr.WHOSE_ID) AS `ID`,
          wr.WHOSE_ID                            AS `LOGIN_ID`,
          NULL                                   AS `MAIN_ORDER_ID`,
          wr.MANAGETIME                          AS `HAPPEN_TIME`,
          1                                      AS `TYPE`,
          -wr.AMOUNT                             AS `CHANGED`
        FROM withdrawrequest AS wr
        WHERE wr.WITHDRAWSTATUS = 4;