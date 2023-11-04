package org.jinq.jpa.jpqlquery;

import java.util.ArrayList;
import java.util.List;


public class FunctionExpression extends Expression
{
   List<Expression> arguments = new ArrayList<>();
   String functionName;
   boolean isCustomSqlFunction = false;
   
   public static FunctionExpression singleParam(String name, Expression base)
   {
      FunctionExpression func = new FunctionExpression();
      func.arguments.add(base);
      func.functionName = name;
      return func;
   }

   public static FunctionExpression twoParam(String name, Expression param1, Expression param2)
   {
      FunctionExpression func = new FunctionExpression();
      func.arguments.add(param1);
      func.arguments.add(param2);
      func.functionName = name;
      return func;
   }
   
   public static FunctionExpression threeParam(String name, Expression param1, Expression param2, Expression param3)
   {
      FunctionExpression func = new FunctionExpression();
      func.arguments.add(param1);
      func.arguments.add(param2);
      func.arguments.add(param3);
      func.functionName = name;
      return func;
   }
   
   public static FunctionExpression customSqlFunction(String name, Expression...params)
   {
      FunctionExpression func = new FunctionExpression();
      for (Expression param: params)
         func.arguments.add(param);
      func.functionName = name;
      func.isCustomSqlFunction = true;
      return func;
   }

   @Override
   public void generateQuery(QueryGenerationState queryState, OperatorPrecedenceLevel operatorPrecedenceScope)
   {
      if (isCustomSqlFunction)
      {
         queryState.appendQuery("function('");
         queryState.appendQuery(functionName);
         queryState.appendQuery("'");
         for (Expression arg: arguments)
         {
            queryState.appendQuery(", ");
            arg.generateQuery(queryState, OperatorPrecedenceLevel.JPQL_UNRESTRICTED_OPERATOR_PRECEDENCE);
         }
         queryState.appendQuery(")");
      }
      else
      {
         queryState.appendQuery(functionName);
         queryState.appendQuery("(");
         boolean isFirst = true;
         for (Expression arg: arguments)
         {
            if (!isFirst) queryState.appendQuery(", ");
            isFirst = false;
            arg.generateQuery(queryState, OperatorPrecedenceLevel.JPQL_UNRESTRICTED_OPERATOR_PRECEDENCE);
         }
         queryState.appendQuery(")");
      }
   }

   @Override
   public void prepareQueryGeneration(
         QueryGenerationPreparationPhase preparePhase,
         QueryGenerationState queryState)
   {
      for (Expression arg: arguments)
         arg.prepareQueryGeneration(preparePhase, queryState);
   }
   
   @Override public boolean equals(Object obj)
   {
      if (!getClass().equals(obj.getClass())) return false;
      FunctionExpression o = (FunctionExpression)obj;
      if (!functionName.equals(o.functionName)) return false;
      if (arguments.size() != o.arguments.size()) return false;
      for (int n = 0; n < arguments.size(); n++)
         if (!arguments.get(n).equals(o.arguments.get(n)))
            return false;
      return true;
   }

   @Override
   public void visit(ExpressionVisitor visitor)
   {
      visitor.visitFunction(this);
   }
   
   @Override
   public FunctionExpression copy()
   {
      FunctionExpression func = new FunctionExpression();
      for (Expression arg: arguments)
         func.arguments.add(arg.copy());
      func.functionName = functionName;
      return func;
   }
}
