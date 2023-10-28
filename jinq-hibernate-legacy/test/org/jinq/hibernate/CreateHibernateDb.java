package org.jinq.hibernate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import jakarta.persistence.Query;

import org.hibernate.Session;
import org.jinq.hibernate.test.entities.CreditCard;
import org.jinq.hibernate.test.entities.Customer;
import org.jinq.hibernate.test.entities.Item;
import org.jinq.hibernate.test.entities.ItemType;
import org.jinq.hibernate.test.entities.Lineorder;
import org.jinq.hibernate.test.entities.PhoneNumber;
import org.jinq.hibernate.test.entities.Sale;
import org.jinq.hibernate.test.entities.Supplier;

public class CreateHibernateDb
{
   Session session;

   public CreateHibernateDb(Session session)
   {
      this.session = session;
   }
   
   private Customer createCustomer(String name, String country, int debt, int salary, String phone)
   {
      Customer c = new Customer();
      c.setName(name);
      c.setDebt(debt);
      c.setSalary(salary);
      c.setCountry(country);
      c.setPhone(new PhoneNumber("1", "555", phone));
      return c;
   }
   
   private Sale createSale(Customer customer, int year)
   {
      return createSale(customer, year, 1, 1, 1, false);
   }

   private Sale createSale(Customer customer, int year, int month, int day, int hour, boolean isRush)
   {
      LocalDate localDate = LocalDate.of(year, month, day);
      LocalTime localTime = LocalTime.of(hour, 0);
      LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
      // All these conversions from the new Java 8 time API to the old Java date stuff is
      // sort of bogus, but it's fine because Java is ambiguous as to how timezones and dates
      // are treated in the database, so we can ignore those problems for the purposes of the tests 
      Date date = Date.from(java.sql.Timestamp.valueOf(localDateTime).toInstant());
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      // SQL and JPA has some issues with timezones, so we'll manually
      // set the years and stuff for dates using deprecated methods to 
      // ensure we consistently get certain values in the database.
      java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
      java.sql.Time sqlTime = java.sql.Time.valueOf(localTime);
      java.sql.Timestamp sqlTimestamp = java.sql.Timestamp.valueOf(localDateTime);
      
      CreditCard creditCard = new CreditCard();
      creditCard.setName(customer.getName());
      creditCard.setNumber(123456);
      creditCard.setCvv(year);
      
      Sale s = new Sale();
      s.setDate(date);
      s.setCalendar(cal);
      s.setSqlDate(sqlDate);
      s.setSqlTime(sqlTime);
      s.setSqlTimestamp(sqlTimestamp);
      s.setCustomer(customer);
      s.setCreditCard(creditCard);
      s.setRush(isRush);
      
      // For tests of new Java 8 java.time stuff for Hibernate 5.2+ only
      s.setDuration(Duration.ofDays(1));
      s.setLocalDateTime(localDateTime);
      s.setLocalDate(localDate);
      s.setLocalTime(localTime);
      s.setOffsetDateTime(OffsetDateTime.of(localDateTime, ZoneOffset.UTC));
      s.setOffsetTime(OffsetTime.of(localTime, ZoneOffset.UTC));
      s.setZonedDateTime(s.getOffsetDateTime().toZonedDateTime());
      s.setInstant(s.getOffsetDateTime().toInstant());

      return s;
   }

   private Item createItem(String name, int uuid, ItemType type, int purchasePrice, int salePrice)
   {
      Item i = new Item();
      i.setName(name);
      i.setUuid(new UUID(0, uuid));
      i.setType(type);
      i.setSaleprice(salePrice);
      i.setPurchaseprice(purchasePrice);
      return i;
   }
   
   private Lineorder addLineorder(Sale s, Item item, int quantity, int transactionConfirmation)
   {
      Lineorder lo = new Lineorder();
      s.addLineorder(lo);
      item.addLineorder(lo);
      lo.setQuantity(quantity);
      lo.setTotal(BigDecimal.valueOf(quantity * item.getPurchaseprice()));
      lo.setTransactionConfirmation(BigInteger.valueOf(transactionConfirmation));
      return lo;
   }
   
   private Supplier createSupplier(String name, String country, long revenue, boolean hasFreeShipping, boolean isPreferredSupplier)
   {
      Supplier s = new Supplier();
      s.setName(name);
      s.setCountry(country);
      s.setRevenue(revenue);
      s.setHasFreeShipping(hasFreeShipping);
      s.setPreferredSupplier(isPreferredSupplier);
      s.setSignature(name.getBytes(Charset.forName("UTF-8")));
      // Another ambiguous date usage
      s.setSignatureExpiry(Date.from(Instant.now()));
      return s;
   }
   
   // Used to test the invocation of custom DB functions installed on the database 
   public static int isRegexMatch_DbFunction(String str, String regex)
   {
      return str.matches(regex) ? 1 : 0;
   }

   void createDatabase()
   {
      session.beginTransaction();

      Customer alice = createCustomer("Alice", "Switzerland", 100, 200, "5551111");
      Customer bob = createCustomer("Bob", "Switzerland", 200, 300, "5552222");
      Customer carol = createCustomer("Carol", "USA", 300, 250, "5553333");
      Customer dave = createCustomer("Dave", "UK", 100, 500, "5554444");
      Customer eve = createCustomer("Eve", "Canada", 10, 30, "5555555"); 
      session.persist(alice);
      session.persist(bob);
      session.persist(carol);
      session.persist(dave);
      session.persist(eve);

      Supplier hw = createSupplier("HW Supplier", "Canada", 500, false, false);
      Supplier talentSupply = createSupplier("Talent Agency", "USA", 1000, true, false);
      Supplier conglomerate = createSupplier("Conglomerate", "Switzerland", 10000000L, false, true);
      session.persist(hw);
      session.persist(talentSupply);
      session.persist(conglomerate);

      Item widgets = createItem("Widgets", 1, ItemType.SMALL, 5, 10);
      Item wudgets = createItem("Wudgets", 2, ItemType.SMALL, 2, 3);
      Item talent = createItem("Talent", 3, ItemType.OTHER, 6, 1000);
      Item lawnmowers = createItem("Lawnmowers", 4, ItemType.BIG, 100, 102);
      Item screws = createItem("Screws", 5, ItemType.SMALL, 1, 2);
      widgets.setSuppliers(Arrays.asList(hw, conglomerate));
      wudgets.setSuppliers(Arrays.asList(hw));
      talent.setSuppliers(Arrays.asList(talentSupply));
      lawnmowers.setSuppliers(Arrays.asList(conglomerate));
      screws.setSuppliers(Arrays.asList(hw));
      session.persist(widgets);
      session.persist(wudgets);
      session.persist(talent);
      session.persist(lawnmowers);
      session.persist(screws);

      session.flush();
      
      Sale s1 = createSale(alice, 2005, 2, 2, 10, false);
      Sale s2 = createSale(alice, 2004);
      Sale s3 = createSale(carol, 2003);
      Sale s4 = createSale(carol, 2004, 2, 2, 5, true);
      Sale s5 = createSale(dave, 2001);
      Sale s6 = createSale(eve, 2005);
      session.persist(s1);
      session.persist(s2);
      session.persist(s3);
      session.persist(s4);
      session.persist(s5);
      session.persist(s6);
      
      session.flush();
      
      session.persist(addLineorder(s1, widgets, 1, 1000));
      session.persist(addLineorder(s2, wudgets, 2, 2000));
      session.persist(addLineorder(s2, screws, 1, 3000));
      session.persist(addLineorder(s2, lawnmowers, 2, 4000));
      session.persist(addLineorder(s3, screws, 1000, 5000));
      session.persist(addLineorder(s4, widgets, 200, 6000));
      session.persist(addLineorder(s5, talent, 6, 7000));
      session.persist(addLineorder(s6, widgets, 2, 8000));
      session.persist(addLineorder(s6, wudgets, 2, 9000));
      session.persist(addLineorder(s6, lawnmowers, 2, 10000));
      session.persist(addLineorder(s6, screws, 7, 11000));

      // Add a custom SQL function
      {
         Query q = session.createNativeQuery("CREATE FUNCTION isRegexMatch( str VARCHAR(200), regex VARCHAR(200) ) RETURNS INTEGER LANGUAGE JAVA PARAMETER STYLE JAVA NO SQL EXTERNAL NAME 'org.jinq.hibernate.CreateHibernateDb.isRegexMatch_DbFunction' ");
         q.executeUpdate();
      }

      session.getTransaction().commit();
   }
}
