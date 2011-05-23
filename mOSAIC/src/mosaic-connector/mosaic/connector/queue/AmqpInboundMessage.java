package mosaic.connector.queue;

/**
 * This class defines an inbound message and all information required to consume
 * it.
 * 
 * @author Georgiana Macariu
 * 
 */
public class AmqpInboundMessage implements IAmqpMessage {
	private final String callback;
	private final String consumer;
	private final String contentEncoding;
	private final String contentType;
	private final String correlation;
	private final byte[] data;
	private final long delivery;
	private final boolean durable;
	private final String exchange;
	private final String identifier;
	private final String routingKey;

	/**
	 * Constructs a inbound message.
	 * 
	 * @param consumer
	 *            a client-generated consumer tag to establish context
	 * @param delivery
	 * @param exchange
	 *            the exchange to publish the message to
	 * @param routingKey
	 *            the routing key
	 * @param data
	 *            the message body
	 * @param durable
	 *            <code>true</code> if delivery mode should be 2
	 * @param callback
	 *            the address of the Node to send replies to
	 * @param contentEncoding
	 * @param contentType
	 *            tThe RFC-2046 MIME type for the Message content (such as
	 *            "text/plain")
	 * @param correlation
	 *            this is a client-specific id that may be used to mark or
	 *            identify Messages between clients. The server ignores this
	 *            field.
	 * @param identifier
	 *            message-id is an optional property which uniquely identifies a
	 *            Message within the Message system. The Message producer is
	 *            usually responsible for setting the message-id in such a way
	 *            that it is assured to be globally unique. The server MAY
	 *            discard a Message as a duplicate if the value of the
	 *            message-id matches that of a previously received Message sent
	 *            to the same Node.
	 * 
	 */
	public AmqpInboundMessage(String consumer, long delivery, String exchange,
			String routingKey, byte[] data, boolean durable, String callback,
			String contentEncoding, String contentType, String correlation,
			String identifier) {
		super();
		this.consumer = consumer;
		this.delivery = delivery;
		this.exchange = exchange;
		this.routingKey = routingKey;
		this.identifier = identifier;
		this.correlation = correlation;
		this.callback = callback;
		this.contentType = contentType;
		this.contentEncoding = contentEncoding;
		this.data = data;
		this.durable = durable;
	}

	public String getCallback() {
		return callback;
	}

	public String getConsumer() {
		return consumer;
	}

	public String getContentEncoding() {
		return contentEncoding;
	}

	public String getContentType() {
		return contentType;
	}

	public String getCorrelation() {
		return correlation;
	}

	public byte[] getData() {
		return data;
	}

	public long getDelivery() {
		return delivery;
	}

	public boolean isDurable() {
		return durable;
	}

	public String getExchange() {
		return exchange;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getRoutingKey() {
		return routingKey;
	}
}
