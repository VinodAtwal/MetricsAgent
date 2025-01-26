package org.va;

import org.va.transformer.PerformanceTransformer;

import java.lang.instrument.Instrumentation;
import java.util.concurrent.ConcurrentHashMap;

public class Premain {

    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new PerformanceTransformer(agentArgs));
    }
}