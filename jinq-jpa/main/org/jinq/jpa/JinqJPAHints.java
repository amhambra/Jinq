package org.jinq.jpa;

import jakarta.persistence.EntityGraph;

class JinqJPAHints
{
   public JinqJPAHints() { }
   public JinqJPAHints(JinqJPAHints oldHints)
   {
      automaticResultsPagingSize = oldHints.automaticResultsPagingSize;
      queryLogger = oldHints.queryLogger;
      lambdaClassLoader = oldHints.lambdaClassLoader;
      dieOnError = oldHints.dieOnError;
      useCaching = oldHints.useCaching;
      isObjectEqualsSafe = oldHints.isObjectEqualsSafe;
      isCollectionContainsSafe = oldHints.isCollectionContainsSafe;
      jakartaPersistenceFetchgraph = oldHints.jakartaPersistenceFetchgraph;
   }
   
   public int automaticResultsPagingSize = 10000;
   public JPAQueryLogger queryLogger = null;
   public ClassLoader lambdaClassLoader = null;
   public boolean dieOnError = true;
   public boolean useCaching = true;
   public boolean isObjectEqualsSafe = true;
   public boolean isAllEqualsSafe = true;
   public boolean isCollectionContainsSafe = true;
   public EntityGraph<?> jakartaPersistenceFetchgraph = null;
   
   public boolean setHint(String name, Object val)
   {
      if ("automaticPageSize".equals(name) && val instanceof Integer)
         automaticResultsPagingSize = (int)val;
      else if ("queryLogger".equals(name) && val instanceof JPAQueryLogger)
         queryLogger = (JPAQueryLogger)val;
      else if ("lambdaClassLoader".equals(name) && val instanceof ClassLoader)
         lambdaClassLoader = (ClassLoader)val;
      else if ("exceptionOnTranslationFail".equals(name) && val instanceof Boolean)
         dieOnError = (Boolean)val;
      else if ("useCaching".equals(name) && val instanceof Boolean)
         useCaching = (Boolean)val;
      else if ("isObjectEqualsSafe".equals(name) && val instanceof Boolean)
         isObjectEqualsSafe = (Boolean)val;
      else if ("isAllEqualsSafe".equals(name) && val instanceof Boolean)
         isAllEqualsSafe = (Boolean)val;
      else if ("isCollectionContainsSafe".equals(name) && val instanceof Boolean)
         isCollectionContainsSafe = (Boolean)val;
      else if ("javax.persistence.fetchgraph".equals(name) && val instanceof EntityGraph)
         jakartaPersistenceFetchgraph = (EntityGraph<?>)val;
      else if ("jakarta.persistence.fetchgraph".equals(name) && val instanceof EntityGraph)
         jakartaPersistenceFetchgraph = (EntityGraph<?>)val;
      else
         return false;
      return true;
   }
}
