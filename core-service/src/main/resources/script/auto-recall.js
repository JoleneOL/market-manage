/**
 * 自动重新调用
 * https://kuanyes.quip.com/gA1PAfnTc3TP#QPLACAcfQZ5
 * Created by CJ on 30/07/2017.
 */
if (_this.executedCount >= 5) {
    //延迟到1千年之后
    _this.targetInstant = _this.targetInstant.plusSeconds(31556952000);
} else if (_this.executedCount >= 4) {
    _this.targetInstant = _this.targetInstant.plusSeconds(3 * 3600);
} else if (_this.executedCount >= 3) {
    _this.targetInstant = _this.targetInstant.plusSeconds(30 * 60);
} else if (_this.executedCount >= 2) {
    _this.targetInstant = _this.targetInstant.plusSeconds(10 * 60);
} else if (_this.executedCount >= 1) {
    _this.targetInstant = _this.targetInstant.plusSeconds(2 * 60);
}
