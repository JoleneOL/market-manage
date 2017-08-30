package cn.lmjia.market.core.util;

import me.jiangcai.wx.model.message.TemplateMessageStyle;

/**
 * @author CJ
 */
public abstract class AbstractTemplateMessageStyle implements TemplateMessageStyle {

    @Override
    public String getTemplateId() {
        return null;
    }

    @Override
    public void setTemplateId(String templateId) {

    }

    @Override
    public String getTemplateIdShort() {
        return null;
    }

    @Override
    public String getTemplateTitle() {
        return null;
    }

    @Override
    public String getIndustryId() {
        return null;
    }
}
