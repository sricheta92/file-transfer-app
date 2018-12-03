package com.grpc.raft;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import grpc.FileTransfer;
import grpc.RaftServiceGrpc;
import grpc.Team;
import grpc.TeamClusterServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Contains test code for the RaftServer
 */
public class TestRaftConnectionClass {

	public static void main(String [] args){
		ManagedChannel channel = ManagedChannelBuilder
				.forTarget("10.0.20.4:10000").usePlaintext(true).build();
		TeamClusterServiceGrpc.TeamClusterServiceBlockingStub stub =
				TeamClusterServiceGrpc.newBlockingStub(channel);

		String [] arr = new String[]{""};
		ArrayList<String> addr = new ArrayList<String>(Arrays.asList(
				new String[]{"localhost:8000", "localhost:9001", "127.0.0.1:1006", "YEET!"})
		);
		Team.ChunkLocations request = Team.ChunkLocations.newBuilder()
				.setFileName("poop8.jpg")
				.setChunkId(0)
				.addAllDbAddresses(addr)
				.setMaxChunks(2)
				.build();
		System.out.println(stub.updateChunkLocations(request).getIsAck());
		//channel.shutdown();


		channel = ManagedChannelBuilder.forTarget("10.0.20.4:10000").usePlaintext(true).build();
		stub = TeamClusterServiceGrpc.newBlockingStub(channel);
		Team.FileData req2 = Team.FileData.newBuilder()
				.setFileName("poop8.jpg")
				.setChunkId(0)
				.build();
		Team.ChunkLocations res2 = stub.getChunkLocations(req2);
		System.out.println("DB Addresses: ");
		for(String s : res2.getDbAddressesList())
			System.out.println(s);

		/*
		Futures.addCallback(stub.updateChunkLocations(request), new FutureCallback<Team.Ack>() {
			@Override
			public void onSuccess(@Nullable Team.Ack ack) {
				System.out.println("Successful response! "+ack.getIsAck());
			}

			@Override
			public void onFailure(Throwable throwable) {
				System.out.println("Something broke!");
				throwable.printStackTrace();
			}
		});
		*/
	}

	public void pollValue(String addr, String key){

	}
}
