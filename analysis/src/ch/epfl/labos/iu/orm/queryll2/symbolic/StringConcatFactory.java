package ch.epfl.labos.iu.orm.queryll2.symbolic;

import java.util.List;
import java.util.Objects;

import org.jinq.rebased.org.objectweb.asm.Handle;
import org.jinq.rebased.org.objectweb.asm.Opcodes;
import org.jinq.rebased.org.objectweb.asm.Type;

import ch.epfl.labos.iu.orm.queryll2.symbolic.MethodCallValue.StaticMethodCallValue;

/**
 * Some versions of the JDK handle string concatenation using
 * invokedynamic and special method handles created using
 * StringConcatFactory. So we need to track those method handles
 * here.
 */
public class StringConcatFactory extends TypedValue
{
   String recipe;
   List<TypedValue> args;
   
   // TODO: Handle parameters passed in to the lambda 
   public StringConcatFactory(Type functionalInterface, String recipe, List<TypedValue> args/*, Handle lambdaMethod, List<TypedValue> capturedArgs*/)
   {
      super(functionalInterface);
      this.recipe = recipe;
      this.args = args;
   }

   @Override public <I,O,E extends Exception> O visit(TypedValueVisitor<I,O,E> visitor, I input) throws E
   {
      return visitor.stringConcatFactoryValue(this, input);
   }
   
   public StringConcatFactory withNewArgs(List<TypedValue> newArgs)
   {
      return new StringConcatFactory(type, recipe, newArgs);
   }

   @Override
   public String toString()
   {
      String argString = "";
      boolean isFirst = true;
      for (TypedValue val: args)
      {
         if (!isFirst)
            argString += ",";
         isFirst = false;
         argString += val;
      }
      return "StringConcatFactory.makeConcatWithConstants(\"" + recipe + "\","
            + argString + ")";
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + Objects.hash(args, recipe);
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      StringConcatFactory other = (StringConcatFactory) obj;
      return Objects.equals(args, other.args)
            && Objects.equals(recipe, other.recipe);
   }

}
