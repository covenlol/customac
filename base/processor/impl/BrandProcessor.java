package dev.phoenixhaven.customac.base.processor.impl;

import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInCustomPayload;
import dev.phoenixhaven.customac.CustomAC;
import dev.phoenixhaven.customac.base.processor.api.Processor;
import dev.phoenixhaven.customac.base.processor.api.ProcessorInfo;
import dev.phoenixhaven.customac.base.user.User;

import java.nio.charset.StandardCharsets;

@SuppressWarnings("ForLoopReplaceableByForEach")
@ProcessorInfo("BrandProcessor")
public class BrandProcessor extends Processor {
    private final String[] blockedBrands = {"Pandaware", "", "LOLIMAHCKER", "EROUAXWASHERE", "lmaohax", "1946203560", "MCnetHandler", "Misplace", "reach", "gg", "Remix", "n"};
    private final String[] blockedBrands2 = {"crystalware", "vape", "bspkrs", "vive"};

    public void handlePacket(Object packet, String type, User user) {
        if (type.equalsIgnoreCase(Packet.Client.CUSTOM_PAYLOAD)) {
            WrappedInCustomPayload payload = new WrappedInCustomPayload(packet, user.getPlayer());

            if (payload.getTag().equals("MC|Brand")) {
                if (user.getMovementProcessor().getTicks() > 20) {
                    user.runKickSync();
                    return;
                }
                String data = new String(payload.getData(), StandardCharsets.UTF_8);
                data = data.substring(1);
                data = data.replace("(Velocity)", "");
                data = data.replace(" ", "");
                for (int i = 0; i < blockedBrands.length; i++) {
                    if (data.equalsIgnoreCase(blockedBrands[i]) || charCheck(data) || data.equals("Vanilla")) {
                        user.runKickSync();
                        return;
                    }
                }
                for (int i = 0; i < blockedBrands2.length; i++) {
                    if (data.equalsIgnoreCase(blockedBrands2[i]) || charCheck(data)) {
                        user.runKickSync();
                        return;
                    }
                }
                user.setClientBrand(data);
                CustomAC.getInstance().sendBrandAlert(user, data);
            }
        }
    }

    private boolean charCheck(String data) {
        for (char character : data.toCharArray()) {
            if (!Character.isAlphabetic(character) && !Character.isDigit(character) && !String.valueOf(character).equals("-") && !String.valueOf(character).equals(",")) {
                return true;
            }
        }
        return false;
    }
}
