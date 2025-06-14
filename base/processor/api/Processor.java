package dev.phoenixhaven.customac.base.processor.api;

import lombok.Getter;

@Getter
public class Processor {
    private final String name;

    public Processor() {
        this.name = this.getClass().getAnnotation(ProcessorInfo.class).value();
    }
}
