DROP FUNCTION IF EXISTS `mm_agentLevel`;
DELIMITER // CREATE FUNCTION `mm_agentLevel`(
  id BIGINT
)
  RETURNS INT(11) DETERMINISTIC COMMENT '获取agent的等级\n0 表示是最高的' BEGIN DECLARE result INT(11);
    SELECT CASE WHEN t10.`ID` IS NOT NULL
      THEN 10
           WHEN t9.`ID` IS NOT NULL
             THEN 9
           WHEN t8.`ID` IS NOT NULL
             THEN 8
           WHEN t7.`ID` IS NOT NULL
             THEN 7
           WHEN t6.`ID` IS NOT NULL
             THEN 6
           WHEN t5.`ID` IS NOT NULL
             THEN 5
           WHEN t4.`ID` IS NOT NULL
             THEN 4
           WHEN t3.`ID` IS NOT NULL
             THEN 3
           WHEN t2.`ID` IS NOT NULL
             THEN 2
           WHEN t1.`ID` IS NOT NULL
             THEN 1
           ELSE 0 END
    INTO result
    FROM `AGENTLEVEL` AS t0 LEFT JOIN `AGENTLEVEL` AS t1 ON t0.`SUPERIOR_ID` = t1.`ID`
      LEFT JOIN `AGENTLEVEL` AS t2 ON t1.`SUPERIOR_ID` = t2.`ID`
      LEFT JOIN `AGENTLEVEL` AS t3 ON t2.`SUPERIOR_ID` = t3.`ID`
      LEFT JOIN `AGENTLEVEL` AS t4 ON t3.`SUPERIOR_ID` = t4.`ID`
      LEFT JOIN `AGENTLEVEL` AS t5 ON t4.`SUPERIOR_ID` = t5.`ID`
      LEFT JOIN `AGENTLEVEL` AS t6 ON t5.`SUPERIOR_ID` = t6.`ID`
      LEFT JOIN `AGENTLEVEL` AS t7 ON t6.`SUPERIOR_ID` = t7.`ID`
      LEFT JOIN `AGENTLEVEL` AS t8 ON t7.`SUPERIOR_ID` = t8.`ID`
      LEFT JOIN `AGENTLEVEL` AS t9 ON t8.`SUPERIOR_ID` = t9.`ID`
      LEFT JOIN `AGENTLEVEL` AS t10 ON t9.`SUPERIOR_ID` = t10.`ID`
    WHERE t0.`ID` = id;
    RETURN result;
  END//

DELIMITER ;