package cn.lmjia.market.wechat.model;

import cn.lmjia.market.dealer.controller.team.TeamDataController;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * 成员信息
 *
 * @author CJ
 */
@Setter
@Getter
@ToString
public class MemberInfo {

    private String name;
    private String level;
    private LocalDate joinDate;
    /**
     * 这里的是全部的！
     */
    private String mobile;

    public static MemberInfo ofDiv(WebElement element) {
        MemberInfo info = new MemberInfo();
        List<WebElement> list = element.findElements(By.tagName("div"));
        info.setName(list.get(0).getText());
        info.setLevel(list.get(1).getText());
        info.setJoinDate(LocalDate.from(TeamDataController.teamDateFormatter.parse(list.get(2).getText())));
        info.setMobile(list.get(3).getText());
        return info;
    }

    private String mosaicMobile() {
        return TeamDataController.mosaicMobile(mobile);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MemberInfo)) return false;
        MemberInfo info = (MemberInfo) o;

        return Objects.equals(name, info.name) &&
                Objects.equals(level, info.level) &&
                Objects.equals(joinDate, info.joinDate) &&
                Objects.equals(mosaicMobile(), info.mosaicMobile())
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, level, joinDate, mosaicMobile());
    }
}
