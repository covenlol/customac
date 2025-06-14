package dev.phoenixhaven.customac.base.check;

import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.user.User;
import dev.phoenixhaven.customac.impl.check.badpackets.*;
import dev.phoenixhaven.customac.impl.check.combat.*;
import dev.phoenixhaven.customac.impl.check.combat.aim.*;
import dev.phoenixhaven.customac.impl.check.combat.autoblock.*;
import dev.phoenixhaven.customac.impl.check.combat.autoclicker.AutoclickerA;
import dev.phoenixhaven.customac.impl.check.combat.velocity.*;
import dev.phoenixhaven.customac.impl.check.movement.SprintA;
import dev.phoenixhaven.customac.impl.check.movement.elytraflight.ElytraFlightA;
import dev.phoenixhaven.customac.impl.check.movement.elytraflight.ElytraFlightB;
import dev.phoenixhaven.customac.impl.check.movement.entityflight.EntityFlightA;
import dev.phoenixhaven.customac.impl.check.movement.jump.JumpA;
import dev.phoenixhaven.customac.impl.check.movement.NoSlowA;
import dev.phoenixhaven.customac.impl.check.movement.strafe.*;
import dev.phoenixhaven.customac.impl.check.movement.flight.*;
import dev.phoenixhaven.customac.impl.check.movement.invalid.*;
import dev.phoenixhaven.customac.impl.check.movement.jesus.*;
import dev.phoenixhaven.customac.impl.check.movement.speed.*;
import dev.phoenixhaven.customac.impl.check.player.inventory.*;
import dev.phoenixhaven.customac.impl.check.player.scaffold.*;
import dev.phoenixhaven.customac.impl.check.player.timer.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class CheckManager {
    private final List<Check> checkList = new ArrayList<>();

    public void registerCheck(User user) {
        this.checkList.addAll(Arrays.asList(
                new ReachA(),
                new AimA(),
                new AimB(),
                new AimC(),
                new AimD(),
                new AutoclickerA(),
                new AutoBlockA(),
                new VelocityA(),
                new BadPacketsA(),
                new BadPacketsB(),
                new BadPacketsC(),
                new BadPacketsD(),
                new BadPacketsE(),
                new BadPacketsF(),
                new FlightA(),
                new FlightB(),
                new FlightC(),
                new ElytraFlightA(),
                new ElytraFlightB(),
                new EntityFlightA(),
                new JumpA(),
                new NoSlowA(),
                new SpeedA(),
                new SpeedB(),
                new SpeedC(),
                new SpeedD(),
                new StrafeA(),
                new SprintA(),
                new JesusA(),
                new InvalidA(),
                new InvalidB(),
                new InvalidC(),
                new InvalidD(),
                new TimerA(),
                new TimerB(),
                new ScaffoldA(),
                new ScaffoldB(),
                new InventoryA()));

        this.checkList.forEach(check -> {
            check.setUser(user);

            user.getEventManager().getEventList().add(check);
        });
    }

    public <T> T getCheck(Class<? extends Check> clazz) {
        return (T) this.getCheckList().stream().filter(check -> check.getClass() == clazz).findFirst().orElse(null);
    }
}
