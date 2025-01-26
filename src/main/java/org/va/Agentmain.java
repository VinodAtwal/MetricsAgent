package org.va;

import org.va.transformer.PerformanceTransformer;

import java.lang.instrument.Instrumentation;

public class Agentmain {
    public static void agentmain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new PerformanceTransformer(agentArgs));
    }
}
