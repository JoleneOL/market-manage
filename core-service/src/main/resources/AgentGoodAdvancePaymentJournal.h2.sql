CREATE OR REPLACE VIEW `AgentGoodAdvancePaymentJournal`
AS
  SELECT
    concat('APO-', c.ID) AS `ID`,
    c.BELONGS_ID                                  AS `LOGIN_ID`,
    c.ID                               AS `AGENT_PREPAYMENT_ORDER_ID`,
    c.ORDERTIME                           AS `HAPPEN_TIME`,
    0                                         AS `TYPE`,
    -c.GOODTOTALPRICEAMOUNTINDEPENDENT                          AS `CHANGED`
  FROM agentprepaymentorder AS c
  UNION SELECT
          concat('APR-', wr.ID) AS `ID`,
          wr.LOGIN_ID                            AS `LOGIN_ID`,
          NULL                                   AS `AGENT_PREPAYMENT_ORDER_ID`,
          wr.HAPPENTIME                          AS `HAPPEN_TIME`,
          1                                      AS `TYPE`,
          wr.AMOUNT                             AS `CHANGED`
        FROM agentgoodadvancepayment AS wr
        WHERE wr.APPROVED = 1