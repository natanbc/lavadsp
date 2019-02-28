[ ![Download](https://api.bintray.com/packages/natanbc/maven/lavadsp/images/download.svg) ](https://bintray.com/natanbc/maven/lavadsp/_latestVersion)

# lavadsp

A bunch of lavaplayer audio filters implemented with native code

## Getting Started

### Installing

Replace `VERSION` with the version you want to use. The latest version can be found in the badge above.

#### Maven

```xml
<repositories>
    <repository>
        <id>jcenter</id>
        <name>jcenter</name>
        <url>http://jcenter.bintray.com/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.natanbc</groupId>
        <artifactId>lavadsp</artifactId>
        <version>VERSION</version>
    </dependency>
</dependencies>
```

#### Gradle

```gradle
repositories {
    jcenter()
}

dependencies {
    compile 'com.github.natanbc:lavadsp:VERSION'
}
```

### Basic Usage

```java
AudioPlayer player = manager.createPlayer();
player.setFilterFactory((track, format, output)->{
    TimescalePcmAudioFilter audioFilter = new TimescalePcmAudioFilter(output, format.channelCount, format.sampleRate);
    audioFilter.setSpeed(1.5); //1.5x normal speed
    return Collections.singletonList(audioFilter);
});
```

As of the time of writing (June 9, 2018), the following filters exist:

* [Distortion](https://natanbc.github.io/lavadsp/com/github/natanbc/lavadsp/distortion/DistortionPcmAudioFilter.html)
* [Timescale](https://natanbc.github.io/lavadsp/com/github/natanbc/lavadsp/timescale/TimescalePcmAudioFilter.html)
* [Tremolo](https://natanbc.github.io/lavadsp/com/github/natanbc/lavadsp/tremolo/TremoloPcmAudioFilter.html)
* [Vibrato](https://natanbc.github.io/lavadsp/com/github/natanbc/lavadsp/vibrato/VibratoPcmAudioFilter.html)

### Chaining

Filters may be chained to merge their effects:
```java
AudioPlayer player = manager.createPlayer();
player.setFilterFactory((track, format, output)->{
    TremoloPcmAudioFilter tremolo = new TremoloPcmAudioFilter(output, format.channelCount, format.sampleRate);
    tremolo.setDepth(0.75);
    TimescalePcmAudioFilter timescale = new TimescalePcmAudioFilter(tremolo, format.channelCount, format.sampleRate);
    timescale.setSpeed(1.5);
    return Arrays.asList(tremolo, timescale);
});
```

An alternative is using lavaplayer's FilterChainBuilder class. An example usage can be found [here](https://github.com/natanbc/andesite-node/blob/dc59e2243346de77975d2b9e2c5d6a52f0eb729c/api/src/main/java/andesite/node/player/filter/FilterChainConfiguration.java#L179-L194)
