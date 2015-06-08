package org.exoplatform.bch.es;

import org.elasticsearch.action.admin.indices.stats.CommonStats;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.node.internal.InternalNode;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.exoplatform.bch.es.security.ConversationState;
import org.exoplatform.bch.es.security.Identity;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;

/**
 * Created by bdechateauvieux on 6/4/15.
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.TEST, numDataNodes = 0)
public class ElasticTest extends ElasticsearchIntegrationTest {

    @Before
    public void waitForNodes() {
        setCurrentUser("Alice");
        // wait for 4 active nodes
        internalCluster().ensureAtLeastNumDataNodes(4);
        assertEquals("All nodes in cluster should have HTTP endpoint exposed", 4, cluster().httpAddresses().length);
    }

    @Test
    public void test_indexing_createsDocument() throws IOException, InterruptedException {
        //Given
        assertFalse(indexExists("wikipages"));
        IndexingService indexingService = new IndexingService(cluster().httpAddresses()[0].getPort());
        Page page = new Page();
        page.setTitle("RDBMS Guidelines");
        long docCount;
        try {
            docCount = getDocCount();
        } catch (IndexMissingException e) {
            docCount = 0;
        }

        //When
        indexingService.index(page);

        //Then
        assertTrue(indexExists("wikipages"));
        Thread.sleep(2 * 1000);
        assertThat(getDocCount(), is(docCount + 1));
    }

    private long getDocCount() {
        CommonStats stats = client().admin().indices().prepareStats("wikipages").execute().actionGet().getPrimaries();
        return stats.getDocs().getCount();
    }

    @Test
    public void test_search_returnsAlicePage() throws IOException, InterruptedException {
        //Given
        assertFalse(indexExists("wikipages"));
        IndexingService indexingService = new IndexingService(cluster().httpAddresses()[0].getPort());
        Page page = new Page();
        page.setTitle("RDBMS Guidelines");
        page.setAllowedUsers(new String[]{"Alice"});
        indexingService.index(page);
        Thread.sleep(2 * 1000);

        //When
        List<Page> pages = indexingService.search("RDBMS");

        //Then
        assertThat(pages.size(), is(1));
    }

    @Test
    public void test_search_doesntReturnBobPage() throws IOException, InterruptedException {
        //Given
        assertFalse(indexExists("wikipages"));
        IndexingService indexingService = new IndexingService(cluster().httpAddresses()[0].getPort());
        Page page = new Page();
        page.setTitle("RDBMS Guidelines");
        page.setAllowedUsers(new String[]{"Bob"});
        indexingService.index(page);
        Thread.sleep(2 * 1000);

        //When
        List<Page> pages = indexingService.search("RDBMS");
//        Thread.sleep(2000 * 1000);

        //Then
        assertThat(pages.size(), is(0));
    }

    private void setCurrentUser(String userId) {
        ConversationState.setCurrent(new ConversationState(new Identity(userId)));
    }

    /**
     * Configuration of the ES integration tests
     */
    @Override
    protected Settings nodeSettings(int nodeOrdinal) {
        return ImmutableSettings.settingsBuilder()
                .put(super.nodeSettings(nodeOrdinal))
                .put(RestController.HTTP_JSON_ENABLE, true)
                .put(InternalNode.HTTP_ENABLED, true)
                .build();
    }
}
