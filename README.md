# EventstoreDB POC

## Prerequisites

- direnv (`brew install direnv`)
- clone repo
    ```
    git clone git@github.com:dmadouros/EventstoreDbPoc.git
    ```
- `cd EventstoreDbPoc`
- `direnv allow` when prompted

## Getting Started

**Note:** This will _not_ run on Macs w/ M1s 😞.

1. Start EventstoreDB
    ```bash
    docker-compose up -d
    ```
2. Start application
    ```bash
    ./gradlew run
    ```
3. Add a pharmacy (one time only!)
    ```bash
    curl --location --request POST 'http://localhost:8080/pharmacies' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "npi": "0987654321",
        "ncpdpId": "76543231",
        "name": "King Soogers - Wildcat Pkwy",
        "address": {
            "line1": "2205 W Wildcat Reserve Pkwy",
            "city": "Highlands Ranch",
            "state": "CO",
            "zipCode": "80129"
        }
    }'
    ```
4. Submit a fake `RtpbiRequest`
    ```bash
    curl -s http://localhost:8080/receiveRtpbiRequest
    ```
5. View `RtpbiRequest` count
    ```bash
    curl -s http://localhost:8080/rtpbiRequestCount | jq
    ```
6. [View event streams](http://localhost:2113/web/index.html#/streams)
    Things to look for:
    - `pharmacy-*` stream contains a single event for `PharmacyAdded` and you can see the event as json by drilling into the stream and then drilling into the event
    - Each `rtpbiRequest` has its own stream (based on a the request uuid). If you drill into one of the streams you should see two events: one for `RtpbiRequestReceived` and another for `RtpbiRequestNormalized`
    - The count increments once per `RtpbiRequestReceived`

What's going on?

1. When an http request is received for at `receiveRtpbiReqeust`, it generates a "fake" `rtpbiRequest` with a unique identifier and puts a `RtpbiRequestReceived` event into the message store and returns a 200 http status code. This request/response cycle does nothing else(!).
2. Behind the scenes there are two subscriptions to events
    - A subscription that fires an UpdateRtpbiRequestReceivedCount Command that updates an in-memory "projection" that increments the count for each RtpbiRequestReceived. This projection is rebuilt at application startup time but could be store in a differrent data store. This is visible by issuing a second http request to `rtpbiRequestCount`.
    - A subscription that fires a NormalizeRequest command. This subscription receives the RtpbiRequestReceived event, "normalizes" the request and writes a RtpbiRequestNormalized event to the message store. This is visible in the stream browser in the EventstoreDB admin console within one of the `rtpbiReqeust-*` streams. Note that the stream per `rtpbiRequest` tells the story of a single `rtpbiRequest`.

As previously noted, the count projection is rebuilt at application startup by replaying all `RtpbiRequestReceived` events and incrementing a counter for each one. However, the `RtpbiRequestNormalized` picks up where it left off and does NOT replay the events as normalizing the `rtpbiRequest` twice would be a mistake (technically, this isn't harmful in this example but could be in a larger system since the `RtpbiRequestNormalized` could trigger additional activity).
