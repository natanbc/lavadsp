[ ![Download](https://api.bintray.com/packages/natanbc/maven/lavadsp/images/download.svg) ](https://bintray.com/natanbc/maven/lavadsp/_latestVersion)

# lavadsp

A bunch of lavaplayer audio filters implemented with native code

## Getting Started

### Installing

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

However, this gets complicated as more filters are added,
so the recommended way to chain filters is using a [ChainedFilter](https://natanbc.github.io/lavadsp/com/github/natanbc/lavadsp/chain/ChainedFilter.html):

```java
AudioPlayer player = manager.createPlayer();

ChainedFilterBuilder builder = new ChainedFilterBuilder();

builder.add(TimescalePcmAudioFilter::new);
builder.add(TremoloPcmAudioFilter::new);
builder.addConfigurator(TremoloPcmAudioFilter.class, filter->filter.setDepth(0.75));
builder.addConfigurator(TimescalePcmAudioFilter.class, filter->filter.setSpeed(1.5));

player.setFilterFactory(builder);
```

If you plan on changing configuration after the filter chain
has been created, you can use an [UnlinkedChainedFilter](https://natanbc.github.io/lavadsp/com/github/natanbc/lavadsp/chain/UnlinkedChainedFilter.html):

```java
AudioPlayer player = manager.createPlayer();

ChainedFilterBuilder builder = new ChainedFilterBuilder();

builder.add(TimescalePcmAudioFilter::new);
builder.add(TremoloPcmAudioFilter::new);
builder.addConfigurator(TremoloPcmAudioFilter.class, filter->filter.setDepth(0.75));
builder.addConfigurator(TimescalePcmAudioFilter.class, filter->filter.setSpeed(1.5));
UnlinkedChainedFilter filter = builder.buildUnlinked(manager.getConfiguration().getOutputFormat());

player.setFilterFactory(filter);

//some time later

filter.configure(TimescalePcmAudioFilter.class, f->f.setSpeed(0.5));
filter.configure(TremoloPcmAudioFilter.class, f->f.setDepth(0.25));
```
