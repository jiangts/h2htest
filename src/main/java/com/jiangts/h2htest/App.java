package com.jiangts.h2htest;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.hive2hive.core.H2HConstants;
import org.hive2hive.core.api.H2HNode;
import org.hive2hive.core.api.configs.FileConfiguration;
import org.hive2hive.core.api.configs.NetworkConfiguration;
import org.hive2hive.core.api.interfaces.IFileConfiguration;
import org.hive2hive.core.api.interfaces.IFileManager;
import org.hive2hive.core.api.interfaces.IH2HNode;
import org.hive2hive.core.api.interfaces.INetworkConfiguration;
import org.hive2hive.core.api.interfaces.IUserManager;
import org.hive2hive.core.exceptions.IllegalFileLocation;
import org.hive2hive.core.exceptions.NoPeerConnectionException;
import org.hive2hive.core.exceptions.NoSessionException;
import org.hive2hive.core.processes.framework.RollbackReason;
import org.hive2hive.core.processes.framework.exceptions.InvalidProcessStateException;
import org.hive2hive.core.processes.framework.interfaces.IProcessComponent;
import org.hive2hive.core.processes.framework.interfaces.IProcessComponentListener;
import org.hive2hive.core.security.UserCredentials;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws UnknownHostException, InvalidProcessStateException, NoPeerConnectionException, NoSessionException, IllegalFileLocation
    {
        System.out.println( "Hive2Hive Test" );
        // master peer configuration
        INetworkConfiguration masterConfig = NetworkConfiguration.create("masterID");

        // node peer configuration with bootstrap address
        INetworkConfiguration nodeConfig = NetworkConfiguration.create("nodeID", InetAddress.getByName("192.168.1.100"));

        // default file configuration
        IFileConfiguration defaultFileConfig = FileConfiguration.createDefault();

        IH2HNode master = H2HNode.createNode(masterConfig, defaultFileConfig);
        master.connect();

        IH2HNode node = H2HNode.createNode(nodeConfig, defaultFileConfig);
        node.connect();
        
        
        
        
        UserCredentials userCredentials = new UserCredentials("user@hive2hive.com", "password", "1234");

	     // perform operations on the user management
	     IUserManager userManager = node.getUserManager();
	     IProcessComponent registerProcess = userManager.register(userCredentials); 
	
	     // pause & resume the process
	     registerProcess.pause();
	     registerProcess.resume();
	
	     // attach a listener that are called when finished
	     registerProcess.attachListener(new IProcessComponentListener() {
	
	         //@Override
	         public void onSucceeded() {
	             System.out.println("Successfully registered");
	         }
	
	         //@Override
	         public void onFinished() {
	             // ignore
	         }
	
	         //@Override
	         public void onFailed(RollbackReason reason) {
	             System.out.println("Failed! Reason: " + reason.getHint());
	         }
	     });

	  	// for the login, the root folder must be specified
	     File root = new File(H2HConstants.DEFAULT_ROOT_PATH);
	     userManager.login(userCredentials, root.toPath());

	     // add a file
	     IFileManager fileManager = node.getFileManager();
	     IProcessComponent addProcess = fileManager.add(new File(root, "demo-file"));
    }
}
