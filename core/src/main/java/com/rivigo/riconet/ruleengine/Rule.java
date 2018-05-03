package com.rivigo.riconet.ruleengine;

/**
 * @author ajay mittal
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Rule
{
    private List<Expression> expressions;
    private ActionDispatcher dispatcher;

    public static class Builder
    {
        private List<Expression> expressions = new ArrayList<>();
        private ActionDispatcher dispatcher = null;

        public Builder withExpression(Expression expr)
        {
            expressions.add(expr);
            return this;
        }

        public Builder withDispatcher(ActionDispatcher dispatcher)
        {
            this.dispatcher = dispatcher;
            return this;
        }

        public Rule build()
        {
            return new Rule(expressions, dispatcher);
        }
    }

    public Rule(List<Expression> expressions, ActionDispatcher dispatcher)
    {
        this.expressions = expressions;
        this.dispatcher = dispatcher;
    }

    public boolean eval(Map<String, ?> bindings)
    {
        boolean eval = true;
        for (Expression expression : expressions)
        {
            eval = (Boolean)expression.interpret(bindings).getValue();
            if (eval && dispatcher!=null)
                dispatcher.fire();
            if(!eval)
                return eval;
        }
        return eval;
    }
}
