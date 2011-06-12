/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package mosaic.interop.idl.amqp;

@SuppressWarnings("all")
public class Response extends org.apache.avro.specific.SpecificRecordBase
		implements org.apache.avro.specific.SpecificRecord {
	public static final org.apache.avro.Schema SCHEMA$ = org.apache.avro.Schema
			.parse("{\"type\":\"record\",\"name\":\"Response\",\"namespace\":\"mosaic.interop.idl.amqp\",\"fields\":[{\"name\":\"response\",\"type\":[{\"type\":\"record\",\"name\":\"OperationResponse\",\"fields\":[{\"name\":\"token\",\"type\":{\"type\":\"record\",\"name\":\"CompletionToken\",\"fields\":[{\"name\":\"id\",\"type\":\"string\"},{\"name\":\"client_id\",\"type\":\"string\"}]}},{\"name\":\"name\",\"type\":{\"type\":\"enum\",\"name\":\"OperationNames\",\"symbols\":[\"DECLARE_EXCHANGE\",\"DECLARE_QUEUE\",\"BIND_QUEUE\",\"CONSUME\",\"PUBLISH\",\"GET\",\"ACK\",\"CANCEL\"]}},{\"name\":\"is_error\",\"type\":\"boolean\"},{\"name\":\"response\",\"type\":[{\"type\":\"error\",\"name\":\"AmqpError\",\"fields\":[{\"name\":\"explanation\",\"type\":\"string\"}]},\"boolean\",\"string\"]}]},{\"type\":\"record\",\"name\":\"ConsumeOkMssg\",\"fields\":[{\"name\":\"consumer_tag\",\"type\":\"string\"}]},{\"type\":\"record\",\"name\":\"CancelOkMssg\",\"fields\":[{\"name\":\"consumer_tag\",\"type\":\"string\"}]},{\"type\":\"record\",\"name\":\"CancelMssg\",\"fields\":[{\"name\":\"consumer_tag\",\"type\":\"string\"}]},{\"type\":\"record\",\"name\":\"DeliveryMssg\",\"fields\":[{\"name\":\"consumer_tag\",\"type\":\"string\"},{\"name\":\"delivery_tag\",\"type\":\"long\"},{\"name\":\"exchange\",\"type\":\"string\"},{\"name\":\"routing_key\",\"type\":\"string\"},{\"name\":\"deliveryMode\",\"type\":\"int\"},{\"name\":\"data\",\"type\":\"bytes\"}]},{\"type\":\"record\",\"name\":\"ShutdownMssg\",\"fields\":[{\"name\":\"consumer_tag\",\"type\":\"string\"},{\"name\":\"message\",\"type\":\"string\"}]}]}]}");
	public java.lang.Object response;

	public org.apache.avro.Schema getSchema() {
		return SCHEMA$;
	}

	// Used by DatumWriter. Applications should not call.
	public java.lang.Object get(int field$) {
		switch (field$) {
		case 0:
			return response;
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}

	// Used by DatumReader. Applications should not call.
	@SuppressWarnings(value = "unchecked")
	public void put(int field$, java.lang.Object value$) {
		switch (field$) {
		case 0:
			response = (java.lang.Object) value$;
			break;
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}
}
