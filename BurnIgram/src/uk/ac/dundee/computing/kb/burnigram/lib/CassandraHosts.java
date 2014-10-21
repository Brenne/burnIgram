package uk.ac.dundee.computing.kb.burnigram.lib;

import java.util.Iterator;
import java.util.Set;

import uk.ac.dundee.computing.kb.burnigram.stores.Globals;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;

/**
 * ********************************************************
 *
 *
 * @author administrator
 *
 * Hosts are 192.168.2.10 Seed for Vagrant hosts
 *
 *
 *
 *
 */
public final class CassandraHosts {

    private static Cluster cluster= null;
    static String Host = "127.0.0.1";  //at least one starting point to talk to

    public CassandraHosts() {

    }

    public static String getHost() {
        return (Host);
    }

    public static String[] getHosts(Cluster cluster) {

        if (cluster == null) {
        	if(Globals.DEBUG)
        		System.out.println("Creating cluster connection");
            cluster = Cluster.builder().addContactPoint(Host).build();
        }
        System.out.println("Cluster Name " + cluster.getClusterName());
        Metadata mdata = cluster.getMetadata();
        Set<Host> hosts = mdata.getAllHosts();
        String sHosts[] = new String[hosts.size()];

        Iterator<Host> it = hosts.iterator();
        int i = 0;
        while (it.hasNext()) {
            Host ch = it.next();
            sHosts[i] = (String) ch.getAddress().toString();
            if(Globals.DEBUG)
            	System.out.println("Hosts" + ch.getAddress().toString());
            i++;
        }

        return sHosts;
    }

    public static Cluster getCluster() {
    	if(cluster!=null && !cluster.isClosed()){
    		if(Globals.DEBUG)
    			System.out.println("cluster already loaded");
    		return cluster;
    	}
    	if(Globals.DEBUG)
    		System.out.println("getCluster");
        cluster = Cluster.builder()
                .addContactPoint(Host).build();
        getHosts(cluster);
        Keyspaces.SetUpKeySpaces(cluster);

        return cluster;

    }

    public void close() {
        cluster.close();
    }

}
