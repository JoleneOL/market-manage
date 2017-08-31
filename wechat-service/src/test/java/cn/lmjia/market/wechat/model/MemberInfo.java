package cn.lmjia.market.wechat.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.util.StringUtils;

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
        // 还不知道长什么样子呢
        info.setMobile(list.get(2).getText());
        return info;
    }

    private String[] m() {
        if (StringUtils.isEmpty(mobile))
            return new String[2];
        return new String[]{
                mobile.substring(0, 3)
                , mobile.substring(mobile.length() - 4, mobile.length())
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MemberInfo)) return false;
        MemberInfo info = (MemberInfo) o;

        return Objects.equals(name, info.name) &&
                Objects.equals(level, info.level) &&
//                Objects.equals(mobile, info.mobile)
                Objects.equals(m()[0], info.m()[0]) &&
                Objects.equals(m()[1], info.m()[1])
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, level, m()[0], m()[1]);
    }
}
