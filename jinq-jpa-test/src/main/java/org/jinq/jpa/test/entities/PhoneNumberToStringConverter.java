package org.jinq.jpa.test.entities;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PhoneNumberToStringConverter implements AttributeConverter<PhoneNumber, String>
{
   @Override
   public String convertToDatabaseColumn(PhoneNumber phone)
   {
      return phone.countryCode + "-" + phone.areaCode + "-" + phone.number;
   }

   @Override
   public PhoneNumber convertToEntityAttribute(String dbData)
   {
      String[] parts = dbData.split("-");
      PhoneNumber phone = new PhoneNumber(parts[0], parts[1], parts[2]);
      return phone;
   }
   
}