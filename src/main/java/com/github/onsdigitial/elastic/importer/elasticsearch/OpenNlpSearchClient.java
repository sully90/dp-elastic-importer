package com.github.onsdigitial.elastic.importer.elasticsearch;

import com.github.onsdigital.elasticutils.client.Host;
import com.github.onsdigital.elasticutils.client.bulk.configuration.BulkProcessorConfiguration;
import com.github.onsdigital.elasticutils.client.generic.RestSearchClient;
import com.github.onsdigital.elasticutils.client.generic.TransportSearchClient;
import com.github.onsdigital.elasticutils.client.http.SimpleRestClient;
import com.github.onsdigital.elasticutils.util.ElasticSearchHelper;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.net.UnknownHostException;

/**
 * @author sullid (David Sullivan) on 28/11/2017
 * @project dp-search-service
 */
public class OpenNlpSearchClient<T> extends TransportSearchClient<T> {

    public OpenNlpSearchClient(TransportClient client, BulkProcessorConfiguration configuration) {
        super(client, configuration);
    }

    public IndexRequest createIndexRequest(String index, byte[] messageBytes) {
        return super.createIndexRequestWithPipeline(index, messageBytes, Pipeline.OPENNLP.getPipelineName(), XContentType.JSON);
    }

    @Override
    public IndexRequest createIndexRequest(String index, byte[] messageBytes, XContentType xContentType) {
        return super.createIndexRequestWithPipeline(index, messageBytes, Pipeline.OPENNLP.getPipelineName(), xContentType);
    }

    public enum Pipeline {
        OPENNLP("opennlp-pipeline");

        private String pipelineName;

        Pipeline(String pipelineName) {
            this.pipelineName = pipelineName;
        }

        public String getPipelineName() {
            return pipelineName;
        }
    }

    public static OpenNlpSearchClient getLocalClient() throws UnknownHostException {
        BulkProcessorConfiguration configuration = ElasticSearchHelper.getDefaultBulkProcessorConfiguration();
        return getLocalClient(configuration);
    }

    public static OpenNlpSearchClient getLocalClient(BulkProcessorConfiguration configuration) throws UnknownHostException {
        return new OpenNlpSearchClient(ElasticSearchHelper.getTransportClient(Host.LOCALHOST), configuration);
    }
}
