package me.jiangcai.logistics.demo;

import lombok.Getter;
import me.jiangcai.logistics.event.InstallationEvent;
import me.jiangcai.logistics.event.ShiftEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author CJ
 */
@Component
@Getter
public class LogisticsEventCatch {

    private InstallationEvent installationEvent;
    private ShiftEvent shiftEvent;

    @EventListener(ShiftEvent.class)
    public void forShiftEvent(ShiftEvent event) {
        this.shiftEvent = event;
    }

    @EventListener(InstallationEvent.class)
    public void forInstallationEvent(InstallationEvent event) {
        this.installationEvent = event;
    }

}
