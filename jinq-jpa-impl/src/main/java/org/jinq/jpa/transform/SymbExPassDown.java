package org.jinq.jpa.transform;

import ch.epfl.labos.iu.orm.queryll2.symbolic.TypedValue;

public class SymbExPassDown
{
   // Parent value (can be null if there is no parent for this part of the expression tree)
   public TypedValue parent;
   // Like SQL, JPQL has uneven support for booleans (they can only be used in certain contexts)
   // This is a variable indicating the context of use of a boolean, so that booleans can be translated correctly
   public boolean isExpectingConditional;
   public boolean canAcceptCharSequence = false;
   public SymbExPassDown copy()
   {
      SymbExPassDown copy = new SymbExPassDown();
      copy.parent = parent;
      copy.isExpectingConditional = isExpectingConditional;
      copy.canAcceptCharSequence = canAcceptCharSequence;
      return copy;
   }
   public SymbExPassDown acceptingCharSequence()
   {
      SymbExPassDown newCopy = copy();
      newCopy.canAcceptCharSequence = true;
      return newCopy;
   }
   public static SymbExPassDown with(TypedValue parent, boolean isExpectingConditional)
   {
      SymbExPassDown toReturn = new SymbExPassDown();
      toReturn.parent = parent;
      toReturn.isExpectingConditional = isExpectingConditional;
      return toReturn;
   }
}
