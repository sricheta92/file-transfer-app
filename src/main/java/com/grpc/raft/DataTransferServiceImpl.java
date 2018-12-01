package com.grpc.raft;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.grpc.proxy.HeartbeatService;
import com.util.ConfigUtil;

import grpc.DataTransferServiceGrpc;
import grpc.FileTransfer;
import grpc.FileTransfer.ProxyInfo;
import io.grpc.stub.StreamObserver;

/**
 * Raft Server Implmentation 
 *
 */
public class DataTransferServiceImpl extends DataTransferServiceGrpc.DataTransferServiceImplBase{


	private RaftServer server;
	private HeartbeatService heartbeat;
	final static Logger logger = Logger.getLogger(DataTransferServiceImpl.class);

	public DataTransferServiceImpl(RaftServer serv){
		super();
		new ConfigUtil();
		server = serv;

	}
	/**
	 * This methods gets list of active proxies based on hearbeat between RAFT and proxy node
	 * @param request
	 * @param responseObserver
	 */
	public void  RequestFileUpload(FileTransfer.FileUploadInfo request, StreamObserver<FileTransfer.ProxyList> responseObserver) {

		logger.debug("Inside RequestFileUpload ...");
	
		List<FileTransfer.ProxyInfo> activeProxies= getLiveProxies();
		
		FileTransfer.ProxyList response = FileTransfer.ProxyList.newBuilder()
				.addAllLstProxy(activeProxies)
				.build();
		responseObserver.onNext(response);
		
		logger.debug("Finished RequestFileUpload ...");
		responseObserver.onCompleted();

	}

	/**
	 * This methods returns list of whatever files you have in your Raft hashmap
	 * @param request
	 * @param responseObserver
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public void ListFiles(FileTransfer.RequestFileList request, StreamObserver<FileTransfer.FileList> responseObserver) {

		logger.debug("ListFiles started...");
		Iterator<Map.Entry<String, String>> it = server.data.entrySet().iterator();

		FileTransfer.FileList.Builder responseBuilder = grpc.FileTransfer.FileList.newBuilder();
		int count =0;
		while(it.hasNext()){

			Map.Entry<String, String> entry  = it.next();
			String[] value = entry.getKey().split("_");
			responseBuilder.setLstFileNames(count, value[0]);
			count++;
		}

		if(responseBuilder.getLstFileNamesCount() == 0) {
			logger.debug("No Files Found in my cluster!!! ");
		}

		FileTransfer.FileList response = responseBuilder.build();
		responseObserver.onNext(response);
		logger.debug("ListFiles ended...");
		responseObserver.onCompleted();

	}

	/**
	 * This methods gets called from other team to our cluster to get files in our cluster.
	 * @param request
	 * @param responseObserver
	 */
	public void GetFileLocation(FileTransfer.FileInfo request, StreamObserver<FileTransfer.FileLocationInfo> responseObserver){

		logger.debug("GetFileLocation started...");
		
<<<<<<< HEAD
		//TODO waiting for Vishnu to return the list of live proxies
		
		
		
=======
		List<FileTransfer.ProxyInfo> activeProxies= getLiveProxies();
		boolean isFileFound = false;
		String  maxChunks="0";
		if(server.data.get(request.getFileName()+"_0")!= null) {
			isFileFound = true;
			String value =  server.data.get(request.getFileName()+"_0");
			maxChunks = value.split("\\$")[0];
		}

		FileTransfer.FileLocationInfo response = FileTransfer.FileLocationInfo.newBuilder()
				.addAllLstProxy(activeProxies)
				.setIsFileFound(isFileFound)
				.setFileName(request.getFileName())
				.setMaxChunks(Long.parseLong(maxChunks))
				.build();

		responseObserver.onNext(response);
		logger.debug("GetFileLocation ended...");
		responseObserver.onCompleted();

>>>>>>> 9d6340963c8580caec6539605fbada1fe67b766e
	}
	
	/**
	 * Utility method to get live proxies
	 * @return
	 */
	private List<ProxyInfo> getLiveProxies() {
		
		logger.debug("Getting live proxies ...");
		List<FileTransfer.ProxyInfo> activeProxies = new ArrayList<FileTransfer.ProxyInfo>();
		boolean[] proxyStatus = heartbeat.getProxyStatus();
		for(int i =0 ; i <proxyStatus.length; i++) {
			if(proxyStatus[i]) {
				FileTransfer.ProxyInfo proxy = FileTransfer.ProxyInfo.newBuilder()
						.setIp(ConfigUtil.proxyNodes.get(i).getIP())
						.setPort(""+ConfigUtil.proxyNodes.get(i).getPort())
						.build();
				activeProxies.add(proxy);
			}		  					
		}
		logger.debug("Got "+ activeProxies.size() + "live proxies..");
		return activeProxies;
	}
	/**
	 * This methods gets list of files from within the team. If not found then calls getFileLocation from other team
	 * @param request
	 * @param responseObserver
	 */
	public void  RequestFileInfo(FileTransfer.FileInfo request, StreamObserver<FileTransfer.FileLocationInfo> responseObserver){

		logger.debug("RequestFileInfo started...");
		FileTransfer.FileLocationInfo response = null;
		List<FileTransfer.ProxyInfo> activeProxies = null;
		String value = server.data.get(request.getFileName() + "_0");  
		RaftClient client =new RaftClient();
		String maxChunks = "0";
		//If not found in own team
		if(value == null) {
			logger.log(Level.INFO, "Fetching Files from other teams..");
			response = client.getFileFromOtherTeam(ConfigUtil.globalNodes, request);
		}else {
			maxChunks = value.split("\\$")[0];
			activeProxies= getLiveProxies();
			logger.log(Level.INFO, "File found in own cluster");
			response = FileTransfer.FileLocationInfo.newBuilder()
					.setIsFileFound(true)
					.setFileName(request.getFileName())
					.setMaxChunks(Long.parseLong(maxChunks))
					.addAllLstProxy(activeProxies)
					.build();
		}
		
<<<<<<< HEAD
	     FileTransfer.FileLocationInfo response = null;
		 ArrayList<FileTransfer.ProxyInfo> proxyList = new ArrayList<FileTransfer.ProxyInfo>();
		 //TODO
		// String value = (String) data.get(request.getFileName() + "_0");  change this. get this from the hashmap.
		 String value = "";
		 RaftClient client =new RaftClient();
		 
		 //If not found in own team
		 if(value == null) {
			 response = client.getFileFromOtherTeam(ConfigUtil.globalNodes, request);
		 }else {
			 
			 // TODO
			 // waiting for Vishnu to return the list of live proxies
			 
		 }
		 
		 if(response == null) {
			 response = FileTransfer.FileLocationInfo.newBuilder()
				      .setIsFileFound(false)
				      .setFileName(request.getFileName())
				      .setMaxChunks(0)
				      .build();
		 }
=======
		// This means that file is found nowhere neither in our cluster nor in other clusters.
		if(response == null) {
			logger.log(Level.WARN, "File is not found anywhere - neither in our cluster nor in other clusters");
			response = FileTransfer.FileLocationInfo.newBuilder()
					.setIsFileFound(false)
					.setFileName(request.getFileName())
					.setMaxChunks(0)
					.build();
		}
		

		responseObserver.onNext(response);
		logger.debug("RequestFileInfo ended...");
		responseObserver.onCompleted();
>>>>>>> 9d6340963c8580caec6539605fbada1fe67b766e
	}

}




