package org.nulleins.formats.iso8583;

import com.google.common.collect.Iterables;
import org.junit.Before;
import org.junit.Test;
import org.nulleins.formats.iso8583.types.BitmapType;
import org.nulleins.formats.iso8583.types.ContentType;
import org.nulleins.formats.iso8583.types.FieldType;
import org.nulleins.formats.iso8583.types.MTI;

import java.util.Collection;
import java.util.TreeSet;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;

public class MessageTests {

  private MessageFactory factory;
  private Message message400;
  private Message message410;
  private Message message210;
  private Message message200;
  private Message message200_2;

  @Before
  public void createFactory() {
    this.factory = new MessageFactory();
    factory.setId("testFactory");
    factory.setDescription("Test Message Schema");
    factory.setBitmapType(BitmapType.HEX);
    factory.setContentType(ContentType.TEXT);
    factory.setHeader("ISO015000077");

    final MessageTemplate template400 = MessageTemplate.create("ISO015000077", MTI.create(0x0400), BitmapType.HEX);
    template400.addField(FieldTemplate.localBuilder(template400).get().f(2).type(FieldType.NUMERIC).dim("fixed(3)").name("TestField").build());
    final MessageTemplate template410 = MessageTemplate.create("ISO015000077", MTI.create(0x0410), BitmapType.HEX);
    template400.addField(FieldTemplate.localBuilder(template400).get().f(2).type(FieldType.NUMERIC).dim("fixed(3)").name("TestField").build());
    final MessageTemplate template200 = MessageTemplate.create("ISO015000077", MTI.create(0x0200), BitmapType.HEX);
    template400.addField(FieldTemplate.localBuilder(template400).get().f(2).type(FieldType.NUMERIC).dim("fixed(3)").name("TestField").build());
    final MessageTemplate template210 = MessageTemplate.create("ISO015000077", MTI.create(0x0210), BitmapType.HEX);
    template400.addField(FieldTemplate.localBuilder(template400).get().f(2).type(FieldType.NUMERIC).dim("fixed(3)").name("TestField").build());

    factory.addTemplate(template400);
    factory.addTemplates(asList(template200, template210, template400, template410));
    factory.initialize();

    final MTI mt1 = MTI.create(0x0400);
    message400 = Message.Builder()
        .messageTypeIndicator(mt1)
        .template(factory.getTemplate(mt1))
        .build();
    final MTI mt2 = MTI.create(0x0410);
    message410 = Message.Builder()
        .messageTypeIndicator(mt2)
        .template(factory.getTemplate(mt2))
        .build();
    final MTI mt3 = MTI.create(0x0210);
    message210 = Message.Builder()
        .messageTypeIndicator(mt3)
        .template(factory.getTemplate(mt3))
        .build();
    final MTI mt4 = MTI.create(0x0200);
    message200 = Message.Builder()
        .messageTypeIndicator(mt4)
        .template(factory.getTemplate(mt4))
        .build();
    message200_2 = Message.Builder()
        .messageTypeIndicator(mt4)
        .header("ISO015000077")
        .template(factory.getTemplate(mt4))
        .build();
  }


  @Test
  public void messagesSorted() {
    final Collection<Message> messageList = asList(message400, message410, message210, message200, message200_2);
    final TreeSet<Message> sortedSet = new TreeSet<>(messageList);
    assertThat(sortedSet.size(), is(4));
    assertThat ( Iterables.getFirst(sortedSet, null), is(message200));
    assertThat ( Iterables.getLast(sortedSet), is(message410));
  }

  @Test
  public void fieldEquality() {
    final MTI mti = MTI.create(0x0400);
    final Message first = Message.Builder()
        .messageTypeIndicator(mti)
        .header("ISO015000077")
        .template(factory.getTemplate(mti))
        .build();
    final Message second = Message.Builder()
        .messageTypeIndicator(mti)
        .header("ISO015000077")
        .template(factory.getTemplate(mti))
        .build();
    assertThat(first, is(second));
    assertThat(first.hashCode(), is(second.hashCode()));

    first.setFieldValue(2, 1);
    second.setFieldValue(2, 2);
    assertThat(first, is(not(second)));
  }

  @Test ( expected = NoSuchMethodError.class)
  public void describesMessage() {
    final Iterable<String> description = message200.describe();
    assertThat(Iterables.getFirst(description, ""),
        is("MTI: 0200 (version=ISO 8583-1:1987 class=Financial Message function=Request origin=Acquirer) name: \"null\" header: [ISO015000077] #fields: 0"));
    description.iterator().remove();
  }

}