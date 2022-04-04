# EventstoreDB POC

## Prerequisites

- direnv (`brew install direnv`)
- jq (`brew install jq`)
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
4. Add a medication (one time only!)
    ```bash
    curl --location --request POST 'http://localhost:8080/medications' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "ndc": "1234567890",
        "name": "Advil",
        "quomCode": "C12345"
    }'
    ```
5. Add a provider (one time only!)
    ```bash
    curl --location --request POST 'http://localhost:8080/providers' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "npi": "1234567890",
        "firstName": "Doogie",
        "lastName": "Howser",
        "credentials": "MD"
    }'
    ```
5. Submit a fake `RtpbiRequest`
    ```bash
    curl -s http://localhost:8080/receiveRtpbiRequest
    ```
6. View `RtpbiRequest` count
    ```bash
    curl -s http://localhost:8080/rtpbiRequestCount | jq
    ```
7. [View event streams](http://localhost:2113/web/index.html#/streams)
8. View the denormalized rtpbi request
    ```bash
    curl -s http://localhost:8080/denormalizedRequests/{id} | jq
    ```
    replacing `{id}` with one of the UUIDs for an `rtpbiRequest` in the [stream browser](http://localhost:2113/web/index.html#/streams)

### Things to look for:

- `pharmacy-*` stream contains a single event for `PharmacyAdded` and you can see the event as json by drilling into the stream and then drilling into the event
- Each `rtpbiRequest` has its own stream (based on a the request uuid). If you drill into one of the streams you should see two events: one for `RtpbiRequestReceived` and another for `RtpbiRequestNormalized`
- The count increments once per `RtpbiRequestReceived`

### What's going on?

1. When an http request is received for at `receiveRtpbiReqeust`, it generates a "fake" `rtpbiRequest` with a unique identifier and puts a `RtpbiRequestReceived` event into the message store and returns a 200 http status code. This request/response cycle does nothing else(!).
2. Behind the scenes there are two subscriptions to events
    - A subscription that fires an `UpdateRtpbiRequestReceivedCount` command that updates an in-memory "projection" that increments the count for each RtpbiRequestReceived. This projection is rebuilt at application startup time but could be store in a differrent data store. This is visible by issuing a second http request to `rtpbiRequestCount`.
    - A subscription that fires an `IdentifyPatient` command. This subscription receives the `RtpbiRequestReceived` event, looks up a pharmacy for the requested pharmacy npi and writes a `PharmacyFound` event to the message store. This is visible in the stream browser in the EventstoreDB admin console within one of the `rtpbiReqeust-*` streams. Note that the stream per `rtpbiRequest` tells the story of a single `rtpbiRequest`.

As previously noted, the count projection is rebuilt at application startup by replaying all `RtpbiRequestReceived` events and incrementing a counter for each one. However, the `PharmacyFound` picks up where it left off and does NOT replay the events as identifying the pharmacy for the `rtpbiRequest` twice would be a mistake (technically, this isn't harmful in this example but could be in a larger system since the `PharmacyFound` could trigger additional activity).

## To Reset

1. Stop the application (`Ctrl-C`)
2. Shutdown EventstoreDB
    ```bash
    docker-compose down --volumes
    ```
