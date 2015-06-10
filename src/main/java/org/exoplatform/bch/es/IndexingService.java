package org.exoplatform.bch.es;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.mapping.PutMapping;
import org.apache.commons.lang3.StringUtils;
import org.exoplatform.bch.es.security.ConversationState;
import org.exoplatform.bch.es.security.MembershipEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by bdechateauvieux on 6/4/15.
 */
public class IndexingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexingService.class);

    private final int esPort;
    private final JestClient client;

    public IndexingService(int esPort) {
        this.esPort = esPort;

        //Instanciate Client
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://localhost:" + this.esPort)
                .multiThreaded(true)
                .build());
        this.client = factory.getObject();
    }

    public List<Page> search(String query) throws IOException {
        String querySt = "{\n" +
                "    \"query\": {\n" +
                "        \"filtered\" : {\n" +
                "            \"query\" : {\n" +
                "                \"match\" : { \"title\" : \""+query+"\" }" +
                "            },\n" +
                "            \"filter\" : {\n" +
                "               \"bool\" : { " +
                "                  \"should\" : [ " +
                                        getFilterForCurrentUser()+
                                        getFilterForMembership()+
                                        getFilterForOwner()+
                "                   ]\n"+
                "               }\n"+
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";
        LOGGER.debug(querySt);

        Search search = new Search.Builder(querySt)
                // multiple index or types can be added.
                .addIndex("wikipages")
                .build();

        SearchResult result = client.execute(search);
//        List<SearchResult.Hit<Page, Void>> hits = result.getHits(Page.class);
// or
        return result.getSourceAsObjectList(Page.class);
    }

    private String getFilterForOwner() {
        return "{\"term\" : { \"owner\" : \""+getCurrentUser()+"\" }}";
    }

    private String getFilterForMembership() {
        StringBuilder result = new StringBuilder();
        result.append("{\"regexp\" : { ");
        result.append("     \"allowedIdentities\" : \"" + StringUtils.join(getMemberships(), "|") + "\" ");
        result.append("}},");
        return result.toString();
    }

    private String getFilterForCurrentUser() {
        return "{\"term\" : { \"allowedIdentities\" : \""+getCurrentUser()+"\" }},";
    }

    public void index(Page page) throws IOException {
        //Creating an index
        client.execute(new CreateIndex.Builder("wikipages").build());
        //Create Mapping
        PutMapping putMapping = new PutMapping.Builder(
                "wikipages",
                "page",
                "{ \"page\" : " +
                    "{ \"properties\" : {" +
                        "\"allowedIdentities\" : {\"type\" : \"string\", \"index\" : \"not_analyzed\"} ," +
                        "\"owner\" : {\"type\" : \"string\", \"index\" : \"not_analyzed\"} " +
                        "}" +
                    "} " +
                "}"
        ).build();
        this.client.execute(putMapping);
        //Indexing page
        Index index = new Index.Builder(page).index("wikipages").type("page").build();
        this.client.execute(index);
    }

    public String getCurrentUser() {
        ConversationState conversationState = ConversationState.getCurrent();
        if (conversationState != null) {
            return ConversationState.getCurrent().getIdentity().getUserId();
        }
        return null;
    }

    private Set<String> getMemberships() {
        ConversationState conversationState = ConversationState.getCurrent();
        if (conversationState != null) {
            Set<String> entries = new HashSet<>();
            for (MembershipEntry entry : ConversationState.getCurrent().getIdentity().getMemberships()) {
                //If it's a wildcard membership, add a point to transform it to regexp
                if (entry.getMembershipType().equals(MembershipEntry.ANY_TYPE)) {
                    entries.add(entry.toString().replace("*", ".*"));
                }
                //If it's not a wildcard membership
                else {
                    //Add the membership
                    entries.add(entry.toString());
                    //Also add a wildcard membership (not as a regexp) in order to match to wildcard permission
                    //Ex: membership dev:/pub must match permission dev:/pub and permission *:/pub
                    entries.add("*:"+entry.getGroup());
                }
            }
            return entries;
        }
        return null;
    }
}
