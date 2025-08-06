# AlphaFeed Java SDK

A Java SDK for interacting with the AlphaFeed.io API. This library provides easy access to AlphaFeed's historical and real-time data services.

## Features

- Historical data access with flexible filtering options
- Real-time data subscription via WebSocket
- Instruments discovery and search functionality
- Automatic parsing of JSON responses into Java objects
- Date field conversion from ISO 8601 format to Java Date objects
- Support for multiple instrument filtering (by names and IDs)

## Installation

### Using GitHub Packages

This package is available from GitHub Packages. To use it in your project, you need to:

#### 1. Configure Authentication

Add your GitHub credentials to your Maven `settings.xml` file (usually in `~/.m2/settings.xml`):

```xml
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>YOUR_GITHUB_TOKEN</password>
    </server>
  </servers>
</settings>
```

For the password, use a GitHub personal access token with `read:packages` scope.

#### 2. Add the Repository and Dependency

##### Maven

Add to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/alphafeedio/alphafeed-java-sdk</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.alphafeed.io</groupId>
        <artifactId>alphafeed-java-sdk</artifactId>
        <version>1.0.6</version> <!-- Use appropriate version -->
    </dependency>
</dependencies>
```

##### Gradle

Add to your `build.gradle`:

```groovy
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/alphafeedio/alphafeed-java-sdk")
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("gpr.key") ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation 'com.alphafeed.io:alphafeed-java-sdk:1.0'
}
```

Set the environment variables or gradle properties for authentication:

```groovy
// In gradle.properties:
gpr.user=YOUR_GITHUB_USERNAME
gpr.key=YOUR_GITHUB_TOKEN
```

Or set environment variables `GITHUB_USERNAME` and `GITHUB_TOKEN`.

#### 3. Building with GitHub Actions

If using GitHub Actions, add this to your workflow:

```yaml
jobs:
  build:
    # ...
    steps:
      - uses: actions/checkout@v2
      - name: Set up Java
        uses: actions/setup-java@v2
        with:
          java-version: '11'
          distribution: 'adopt'
      - name: Build with Maven
        run: mvn -B package --file pom.xml
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

### Manual Installation

You can also download the JAR file directly from the releases section and include it in your project:

1. Download the latest JAR from the releases page
2. Add it to your project's classpath
3. Include the following dependencies:
   - OkHttp 4.9.3 or higher
   - Gson 2.10.1 or higher
   - org.json 20231013 or higher

## Usage

### Initializing the SDK

```java
import com.alphafeed.io.AlphaFeedSDK;

// Initialize with your API key
AlphaFeedSDK sdk = new AlphaFeedSDK("your-api-key");
```

### Getting Historical Data

```java
import com.alphafeed.io.AlphaFeedSDK;
import com.alphafeed.io.model.SignalsHistoricalDataResponse;
import com.alphafeed.io.model.NewsSignal;
import java.util.Arrays;

// Initialize the SDK
AlphaFeedSDK sdk = new AlphaFeedSDK("your-api-key");

try {
    // Query historical data with instrument names
    SignalsHistoricalDataResponse response = sdk.getHistoricalData(
            "2025-07-01",                           // dateFrom (required)
            "2025-07-28",                           // dateTo (required)
            Arrays.asList("NVDA", "AAPL", "MSFT"),  // instrumentNames (optional)
            null,                                   // instrumentIds (optional)
            20,                                     // limit (optional)
            0,                                      // offset (optional)
            50,                                     // minScore (optional)
            0.5f,                                   // minImportance (optional)
            0.5f                                    // minSentiment (optional)
    );

    // Alternative: Query by instrument IDs
    SignalsHistoricalDataResponse response2 = sdk.getHistoricalData(
            "2025-07-01",                           // dateFrom (required)
            "2025-07-28",                           // dateTo (required)
            null,                                   // instrumentNames (optional)
            Arrays.asList(1, 2, 3),                 // instrumentIds (optional)
            20,                                     // limit (optional)
            0,                                      // offset (optional)
            50,                                     // minScore (optional)
            0.5f,                                   // minImportance (optional)
            0.5f                                    // minSentiment (optional)
    );

    // Process the data
    System.out.println("Total records: " + response.getPagination().getTotal());

    for (NewsSignal signal : response.getData()) {
        System.out.println("News: " + signal.getNewsTitle());
        System.out.println("Date: " + signal.getNewsDatetime());
        System.out.println("Score: " + signal.getSignalScore());
        System.out.println("Instrument: " + signal.getInstrumentName());
        System.out.println("Instrument ID: " + signal.getInstrumentId());
        System.out.println("-----------------------");
    }
} catch (IOException e) {
    System.err.println("Error: " + e.getMessage());
}
```

### Getting Available Instruments

```java
import com.alphafeed.io.AlphaFeedSDK;
import com.alphafeed.io.model.InstrumentsResponse;
import com.alphafeed.io.model.Instrument;
import java.util.Arrays;

// Initialize the SDK
AlphaFeedSDK sdk = new AlphaFeedSDK("your-api-key");

try {
    // Get all available instruments
    InstrumentsResponse allInstruments = sdk.getInstruments();
    
    // Filter by instrument types
    InstrumentsResponse stocksAndCrypto = sdk.getInstruments(
        Arrays.asList("NASDAQ"), // instrumentTypes (optional)
        null                              // keywords (optional)
    );
    
    // Search by keywords
    InstrumentsResponse appleInstruments = sdk.getInstruments(
        null,     // instrumentTypes (optional)
        "Apple"   // keywords (optional)
    );
    
    // Combined filtering
    InstrumentsResponse filtered = sdk.getInstruments(
        Arrays.asList("NASDAQ"),  // instrumentTypes (optional)
        "tech"                   // keywords (optional)
    );

    // Process the results
    System.out.println("Total instruments found: " + filtered.getTotal());
    
    for (Instrument instrument : filtered.getData()) {
        System.out.println("ID: " + instrument.getId());
        System.out.println("Name: " + instrument.getName());
        System.out.println("Tickers: " + instrument.getTickers());
        System.out.println("Type: " + instrument.getInstrumentType());
        System.out.println("-----------------------");
    }
} catch (IOException e) {
    System.err.println("Error: " + e.getMessage());
}
```

### Subscribing to Real-time Data

```java
import com.alphafeed.io.AlphaFeedSDK;
import com.alphafeed.io.NewsSignalListener;
import java.util.Arrays;

// Initialize the SDK
AlphaFeedSDK sdk = new AlphaFeedSDK("your-api-key");

// Subscribe to real-time signals with instrument names
sdk.subscribeToRealtime(new NewsSignalListener() {
    @Override
    public void onSignal(NewsSignal signal) {
        System.out.println("New signal received: " + signal.getNewsTitle());
        System.out.println("Instrument: " + signal.getInstrumentName());
        System.out.println("Instrument ID: " + signal.getInstrumentId());
        System.out.println("Score: " + signal.getSignalScore());
    }

    @Override
    public void onError(Throwable t) {
        System.err.println("Error: " + t.getMessage());
    }

    @Override
    public void onConnectionStateChange(boolean connected) {
        System.out.println("Connection state: " + (connected ? "Connected" : "Disconnected"));
    }
},
    Arrays.asList("NVDA", "AAPL"),  // instrumentNames (optional) - filter by instrument names
    null,                           // instrumentIds (optional) - filter by instrument IDs
    50,                             // minScore (optional) - minimum signal score
    0.5f,                           // minImportance (optional) - minimum importance score
    0.5f                            // minSentiment (optional) - minimum sentiment score
);

// Alternative: Subscribe using instrument IDs
sdk.subscribeToRealtime(new NewsSignalListener() {
    // listener implementation
},
    null,                           // instrumentNames (optional)
    Arrays.asList(1, 2, 3),         // instrumentIds (optional)
    50,                             // minScore (optional)
    0.5f,                           // minImportance (optional)
    0.5f                            // minSentiment (optional)
);

// Later, when you want to unsubscribe:
sdk.unsubscribeFromRealtime();
```

## Error Handling

The SDK throws standard `IOException` for REST API errors. For WebSocket connections, errors are delivered through the `onError` callback of the `NewsSignalListener` interface.
