package com.gailo22.grpc.client;

import com.gailo22.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {

    public static void main(String[] args) {
        System.out.println("Hello gRPC client..");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

//        greetUnary(channel);
//        greetServerStreaming(channel);
//        greetClientStreaming(channel);
        greetBiDiStreaming(channel);

        channel.shutdown();
    }

    private static void greetUnary(ManagedChannel channel) {
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
    }

    private static void greetServerStreaming(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceBlockingStub client = GreetServiceGrpc.newBlockingStub(channel);
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("gailo")
                .setLastName("hahaha")
                .build();
        GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        client.greetManyTimes(greetManyTimesRequest)
                .forEachRemaining(response -> System.out.println(response.getResult()));
    }

    private static void greetClientStreaming(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<LongGreetRequest> requestObserver = asyncClient.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse value) {
                System.out.println("received response: "  + value.getResult());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("server completed sent response");
                latch.countDown();
            }
        });

        requestObserver.onNext(LongGreetRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("John").build()).build());
        requestObserver.onNext(LongGreetRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("Jan").build()).build());
        requestObserver.onNext(LongGreetRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("Joe").build()).build());
        requestObserver.onCompleted();

        try {
            latch.await(5L, TimeUnit.SECONDS);
        } catch (InterruptedException e) { }
    }

    private static void greetBiDiStreaming(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<GreetEveryoneRequest> requestObserver = asyncClient.greetEveryone(new StreamObserver<GreetEveryoneResponse>() {
            @Override
            public void onNext(GreetEveryoneResponse value) {
                System.out.println("Response from server: " + value.getResult());
            }

            @Override
            public void onError(Throwable t) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("server done sending data.");
                latch.countDown();
            }
        });

        Arrays.asList("John", "Joe", "Jazz").forEach(name -> {
            System.out.println("sending " + name);
            requestObserver.onNext(GreetEveryoneRequest.newBuilder().setGreeting(
                    Greeting.newBuilder().setFirstName(name).build())
                    .build());
        });

        requestObserver.onCompleted();

        try {
            latch.await(5L, TimeUnit.SECONDS);
        } catch (InterruptedException e) { }
    }
}
