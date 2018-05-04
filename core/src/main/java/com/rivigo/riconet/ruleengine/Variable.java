package com.rivigo.riconet.ruleengine;

/**
 * @author ajay mittal
 */
import java.util.Map;

public class Variable implements Expression
{
    private String name;

    public Variable(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    @Override
    public BaseType<?> interpret(Map<String, ?> bindings)
    {
        return new BaseType(bindings.get(this.getName()),bindings.get(this.getName()).getClass());
    }
}
