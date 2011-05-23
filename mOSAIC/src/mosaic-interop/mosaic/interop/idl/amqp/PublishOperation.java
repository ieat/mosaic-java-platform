/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package mosaic.interop.idl.amqp;

@SuppressWarnings("all")
public class PublishOperation extends
		org.apache.avro.specific.SpecificRecordBase implements
		org.apache.avro.specific.SpecificRecord {
	public static final org.apache.avro.Schema SCHEMA$ = org.apache.avro.Schema
			.parse("{\"type\":\"record\",\"name\":\"PublishOperation\",\"namespace\":\"mosaic.interop.idl.amqp\",\"fields\":[{\"name\":\"callback\",\"type\":\"string\"},{\"name\":\"content_encoding\",\"type\":\"string\"},{\"name\":\"content_type\",\"type\":\"string\"},{\"name\":\"correlation\",\"type\":\"string\"},{\"name\":\"data\",\"type\":\"bytes\"},{\"name\":\"durable\",\"type\":\"boolean\"},{\"name\":\"exchange\",\"type\":\"string\"},{\"name\":\"identifier\",\"type\":\"string\"},{\"name\":\"immediate\",\"type\":\"boolean\"},{\"name\":\"mandatory\",\"type\":\"boolean\"},{\"name\":\"routingKey\",\"type\":\"string\"}]}");
	public java.lang.CharSequence callback;
	public java.lang.CharSequence content_encoding;
	public java.lang.CharSequence content_type;
	public java.lang.CharSequence correlation;
	public java.nio.ByteBuffer data;
	public boolean durable;
	public java.lang.CharSequence exchange;
	public java.lang.CharSequence identifier;
	public boolean immediate;
	public boolean mandatory;
	public java.lang.CharSequence routingKey;

	public org.apache.avro.Schema getSchema() {
		return SCHEMA$;
	}

	// Used by DatumWriter. Applications should not call.
	public java.lang.Object get(int field$) {
		switch (field$) {
		case 0:
			return callback;
		case 1:
			return content_encoding;
		case 2:
			return content_type;
		case 3:
			return correlation;
		case 4:
			return data;
		case 5:
			return durable;
		case 6:
			return exchange;
		case 7:
			return identifier;
		case 8:
			return immediate;
		case 9:
			return mandatory;
		case 10:
			return routingKey;
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}

	// Used by DatumReader. Applications should not call.
	@SuppressWarnings(value = "unchecked")
	public void put(int field$, java.lang.Object value$) {
		switch (field$) {
		case 0:
			callback = (java.lang.CharSequence) value$;
			break;
		case 1:
			content_encoding = (java.lang.CharSequence) value$;
			break;
		case 2:
			content_type = (java.lang.CharSequence) value$;
			break;
		case 3:
			correlation = (java.lang.CharSequence) value$;
			break;
		case 4:
			data = (java.nio.ByteBuffer) value$;
			break;
		case 5:
			durable = (java.lang.Boolean) value$;
			break;
		case 6:
			exchange = (java.lang.CharSequence) value$;
			break;
		case 7:
			identifier = (java.lang.CharSequence) value$;
			break;
		case 8:
			immediate = (java.lang.Boolean) value$;
			break;
		case 9:
			mandatory = (java.lang.Boolean) value$;
			break;
		case 10:
			routingKey = (java.lang.CharSequence) value$;
			break;
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}
}
