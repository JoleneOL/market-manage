CREATE FUNCTION `mm_loginBelongs`(
  id BIGINT, superior BIGINT
)
  RETURNS SMALLINT DETERMINISTIC COMMENT 'id 从属于或者间接从属于 superior 则1' BEGIN DECLARE result SMALLINT;
  SELECT count(t0.`ID`) > 0
  INTO result
  FROM `loginrelation` AS t0
  WHERE t0.`FROM_ID` = superior AND t0.`TO_ID` = id;
  RETURN result;
END