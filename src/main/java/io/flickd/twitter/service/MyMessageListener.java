package io.flickd.twitter.service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageListener;
import org.apache.pulsar.client.api.schema.GenericObject;
import org.apache.pulsar.common.schema.KeyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.util.StreamUtils;

import com.google.cloud.spring.storage.GoogleStorageResource;
import com.google.cloud.storage.Storage;

public class MyMessageListener<T> implements MessageListener<T> {

	@Value("${gcs-resource-bucket}")
	private String bucket;

	@Value("${tweet-data.chunk.size}")
	private int chunksize;
	
	@Autowired
	Storage storage;

	private List<String> list = new ArrayList<>();

	public String writeGcs(String data) throws IOException {
		Resource resource = new GoogleStorageResource(storage, "gs://" + bucket + "/"+Instant.now(), true);

		try (OutputStream os = ((WritableResource) resource).getOutputStream()) {
			os.write(data.getBytes());
		}
		return "file was updated\n";
	}

	@Override
	public void received(Consumer<T> consumer, Message<T> msg) {
		
//		Schema schema = msg.getReaderSchema().get();
//
//		KeyValueSchema<GenericObject, GenericObject> keyValueSchema = (KeyValueSchema) schema;
//        Schema<?> keySchema = null;
//        Schema<?> valueSchema = null;
//        keySchema = keyValueSchema.getKeySchema();
//        valueSchema = keyValueSchema.getValueSchema();
        KeyValue<GenericObject, GenericObject> keyValue =
                (KeyValue<GenericObject, GenericObject>) ((GenericObject) msg.getValue()).getNativeObject();
        GenericObject key = null;
        GenericObject value = null;
        key = keyValue.getKey();
        value = keyValue.getValue();
        
        String keyAsJson = key.getNativeObject().toString();
        String valueAsJson = value.getNativeObject().toString();
		StringBuffer sb1= new StringBuffer(keyAsJson);  
		sb1.deleteCharAt(sb1.length()-1).append(", ");  
		StringBuffer sb2= new StringBuffer(valueAsJson);
		sb2.deleteCharAt(0);
		String tweetDataAsJson = sb1.append(sb2.toString()).toString();
		try {
			list.add(tweetDataAsJson);
			if (list.size() >= chunksize) {
//				StringBuffer tweetJsonArray = new StringBuffer("[");
				StringBuffer tweetJsonArray = new StringBuffer();
				list.forEach(tweet -> {
					tweetJsonArray.append(tweet).append(System.getProperty("line.separator"));
					});
//				tweetJsonArray.deleteCharAt(tweetJsonArray.length()-1).append("]");  
				tweetJsonArray.deleteCharAt(tweetJsonArray.length()-1);  
				writeGcs(tweetJsonArray.toString());
				list.clear();
			} 
			consumer.acknowledge(msg);
		} catch (Exception e) {
			consumer.negativeAcknowledge(msg);
		}
	}
}
