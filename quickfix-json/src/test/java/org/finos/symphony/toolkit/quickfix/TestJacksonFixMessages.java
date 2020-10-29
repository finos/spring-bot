package org.finos.symphony.toolkit.quickfix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.finos.symphony.toolkit.quickfix.QuickfixjModule;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import quickfix.FieldConvertError;
import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.Message;
import quickfix.Message.Header;
import quickfix.StringField;
import quickfix.field.BeginString;
import quickfix.field.IOIID;
import quickfix.field.IOIQty;
import quickfix.field.IOITransType;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntrySize;
import quickfix.field.MDEntryTime;
import quickfix.field.MDEntryType;
import quickfix.field.MsgType;
import quickfix.field.NoMDEntries;
import quickfix.field.SenderCompID;
import quickfix.field.SendingTime;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TargetCompID;
import quickfix.field.Text;
import quickfix.field.converter.UtcTimeOnlyConverter;
import quickfix.field.converter.UtcTimestampConverter;
import quickfix.fix50sp2.IOI;
import quickfix.fix50sp2.MarketDataSnapshotFullRefresh;

public class TestJacksonFixMessages {

	private IOI createIOIFixMessage() {
		IOI fixMessage = new IOI();
        Message.Header header = fixMessage.getHeader();
        header.setField(new BeginString("FIX.5.0"));
        header.setField(new SenderCompID("Deutsche Bank"));
        header.setField(new TargetCompID("Credit Suisse"));

        UUID uuid = UUID.fromString("638d5f83-ab40-4c3e-aebe-15bc2620996f");
        String randomUUIDString = uuid.toString();

        fixMessage.set(new IOIID(randomUUIDString));
        fixMessage.set(new IOITransType('N'));
        fixMessage.set(new Symbol("MSFT"));

        fixMessage.set(new Side('B'));

        fixMessage.set(new IOIQty("1000"));
        fixMessage.set(new Text("Place My Order!"));
        return fixMessage;
	}
	
	private Message createComplexFixMessage() {
		MarketDataSnapshotFullRefresh fixMessage = new MarketDataSnapshotFullRefresh();
		fixMessage.getHeader().setField(new BeginString("FIX.5.0"));
		
		MarketDataSnapshotFullRefresh.NoMDEntries g = new MarketDataSnapshotFullRefresh.NoMDEntries();
		
		g.set(new MDEntryType('0'));
		g.set(new MDEntryPx(1.5d));
		g.set(new MDEntrySize(25d));
		g.set(new MDEntryTime(LocalTime.NOON));
		fixMessage.addGroup(g);
		
		g.set(new MDEntryType('1'));
		g.set(new MDEntryPx(1.35d));
		g.set(new MDEntrySize(1225d));
		g.set(new MDEntryTime(LocalTime.NOON));
		fixMessage.addGroup(g);
		
		fixMessage.getTrailer().setField(new StringField(6123, "User Defined Field Value"));
		
		return fixMessage;
	}
	
	
	@Test
	public void testIOIFixConversion() throws IOException {
		Map<String, IOI> in = new HashMap<>();
		in.put("object001", createIOIFixMessage());
		
		ObjectMapper om = new ObjectMapper();
		om.registerModule(new QuickfixjModule());
		
		String out = om.writeValueAsString(in);
		String expected = getExpected("expected1.json");
		System.out.println(out);
		Assert.assertEquals(expected, out);

		// let's go round again, and check they match
		TypeReference<Map<String, IOI>> ref = new TypeReference<Map<String, IOI>>() {};
		Map<String, IOI> stage = om.readValue(out, ref);
		String out2 = om.writeValueAsString(stage);
		Assert.assertEquals(out, out2);
	}
	
	@Test
	public void testFixConversion2() throws IOException {
		Map<String, Object> in = new HashMap<>();
		in.put("object001", createComplexFixMessage());
		
		ObjectMapper om = new ObjectMapper();
		om.registerModule(new QuickfixjModule());
		
		String out = om.writeValueAsString(in);
		String expected = getExpected("expected2.json");
		System.out.println(out);
		Assert.assertEquals(expected, out);
		
		// let's go round again, and check they match
		TypeReference<Map<String, IOI>> ref = new TypeReference<Map<String, IOI>>() {};
		Object stage = om.readValue(out, ref);
		String out2 = om.writeValueAsString(stage);
		Assert.assertEquals(out, out2);
	}

	
	@Test
	public void testDeserializeFixExample() throws IOException, FieldNotFound, FieldConvertError {
		String in = getExpected("example2.json");
		ObjectMapper om = new ObjectMapper();
		om.registerModule(new QuickfixjModule());
		
		MarketDataSnapshotFullRefresh o = om.readValue(in, MarketDataSnapshotFullRefresh.class);
		
		// header
		Header h = o.getHeader();
		Assert.assertEquals("FIX.5.0", h.getField(new BeginString()).getObject());
		Assert.assertEquals("W", h.getField(new MsgType()).getObject());
		Assert.assertEquals("SENDER", h.getField(new SenderCompID()).getObject());
		Assert.assertEquals("TARGET", h.getField(new TargetCompID()).getObject());	
		LocalDateTime utc = h.getField(new SendingTime()).getObject();
		LocalDateTime fromTime =UtcTimestampConverter.convertToLocalDateTime("20160802-21:14:38.717");
		Assert.assertEquals(fromTime, utc);
		
		// body
		NoMDEntries noMDEntries = o.getNoMDEntries();
		Assert.assertEquals(2, noMDEntries.getValue());
		List<Group> groups = o.getGroups(noMDEntries.getField());
		Assert.assertEquals(2, groups.size());
		quickfix.fix50sp2.MarketDataSnapshotFullRefresh.NoMDEntries g1 = (quickfix.fix50sp2.MarketDataSnapshotFullRefresh.NoMDEntries) groups.get(0);
		quickfix.fix50sp2.MarketDataSnapshotFullRefresh.NoMDEntries g2 = (quickfix.fix50sp2.MarketDataSnapshotFullRefresh.NoMDEntries) groups.get(1);
		
		
		Assert.assertTrue('0'==g1.getMDEntryType().getValue());
		Assert.assertTrue(1.5d==g1.getMDEntryPx().getValue());
		Assert.assertTrue(75d==g1.getMDEntrySize().getValue());
		LocalTime t1 = g1.getMDEntryTime().getValue();
		LocalTime t2 = UtcTimeOnlyConverter.convertToLocalTime("21:14:38.688");		
		Assert.assertEquals(t1, t2);

		Assert.assertTrue('1'==g2.getMDEntryType().getValue());
		Assert.assertTrue(1.75d==g2.getMDEntryPx().getValue());
		Assert.assertTrue(25d==g2.getMDEntrySize().getValue());
		t1 = g2.getMDEntryTime().getValue();
		t2 = UtcTimeOnlyConverter.convertToLocalTime("21:14:38.688");		
		Assert.assertEquals(t1, t2);

	}


	private String getExpected(String name) {
		InputStream io = getClass().getResourceAsStream(name);
		String result = new BufferedReader(new InputStreamReader(io))
				  .lines().collect(Collectors.joining("\n"));
		return result;
	}
}
