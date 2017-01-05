package apiSearch.output;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;

import apiSearch.intermediate.InterRep;
import apiSearch.intermediate.Invocation;
import apiSearch.intermediate.Method;
import apiSearch.staticClass.Setting;
import apiSearch.tool.Log;
import apiSearch.tool.Project;

import static org.elasticsearch.common.xcontent.XContentFactory.*;

public class Index extends Output {

	private TransportClient client;
	private long counter;
	private String src;

	public Index(long counter) {
		Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "escodehow").build();
		this.client = new TransportClient(settings);
		client.addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));

		this.counter = counter;
		src = new String();
	}

	public void setSrc(String src) {
		this.src = src;
	}

	@Override
	public void output(Project now, Map<String, InterRep> result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void outputSingleFile(Project now, String file, InterRep result) {
		long writeBegin = System.currentTimeMillis();

		if (result.getData().isEmpty()) {
			return;
		}

		for (Method method : result.getData()) {
			String api = "";
			for (Invocation invocation : method.getInvocations()) {
				api += invocation.getApi() + " ";
			}

			try {
				XContentBuilder snippet = jsonBuilder().startObject().field("all", method.getAll())
						.field("methodname", method.getMethodName()).field("classname", method.getClassName())
						.field("body", method.getBody()).field("comment", method.getComment()).field("filepath", file)
						.field("simhash", method.getSimHash()).field("api", api).field("spanstart", method.getStart())
						.field("length", method.getLength());

				snippet.startArray("invocations");
				for (Invocation invocation : method.getInvocations()) {
					// System.out.println(invocation.getApi());
					snippet.startObject();
					snippet.field("FullQualifyName", invocation.getApi()).field("Start", invocation.getStart())
							.field("Length", invocation.getLength());
					snippet.endObject();
				}
				snippet.endArray();

				snippet.endObject();

				// System.out.println(snippet.string());

				client.prepareIndex("snippet", "snippet", Long.toString(this.counter)).setSource(snippet).execute()
						.actionGet();
				// XContentBuilder tmp =
				// jsonBuilder().startObject().field("methodname",
				// method.getMethodName())
				// .field("classname", method.getClassName()).field("body",
				// method.getBody())
				// .field("filepath", file).field("simhash",
				// method.getSimHash()).field("api", api)
				// .field("spanstart", method.getStart()).field("length",
				// method.getLength())
				// .field("invocations", invocs).endObject();
				// System.out.println(tmp.string());
				counter++;
			} catch (IOException e) {
				e.printStackTrace();

				Log.log("project [" + this.src + "]\nfile [" + file + "] writing to elasticsearch:\n" + e.getMessage());
			}
		}

		float interval = (System.currentTimeMillis() - writeBegin) / Setting.TimeUnit;
	}

	@Override
	public void close() {
		this.client.close();
		this.client.threadPool().shutdown();
	}

}
