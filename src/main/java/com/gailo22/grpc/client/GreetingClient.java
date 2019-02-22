package com.gailo22.grpc.client;

import com.gailo22.proto.greet.GreetRequest;
import com.gailo22.proto.greet.GreetResponse;
import com.gailo22.proto.greet.GreetServiceGrpc;
import com.gailo22.proto.greet.Greeting;
import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {

    public static void main(String[] args) {
        System.out.println("Hello gRPC client..");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        GreetServiceGrpc.GreetServiceBlockingStub client = GreetServiceGrpc.newBlockingStub(channel);
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("gailo")
                .setLastName("hahaha")
                .build();

        GreetRequest greetRequest = GreetRequest.newBuilder()
                .setGreeting(greeting)
                .build();

//        GreetServiceGrpc.GreetServiceFutureStub futureStub = GreetServiceGrpc.newFutureStub(channel);
//        ListenableFuture<GreetResponse> greet = futureStub.greet(greetRequest);

        GreetResponse greetResponse = client.greet(greetRequest);
        System.out.println("response: " + greetResponse.getResult());

        channel.shutdown();
    }
}
