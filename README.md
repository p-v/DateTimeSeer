# DateTimeSeer

An android seer who gets visions of the date and time you are thinking while typing. 

It tells you what you might be thinking and helps in what modern people call as autocompletion. Unfortunately, he currently knows only english.

![Demo](demo/demo.gif)

**Gradle**

Add the seer to `build.gradle` and you are good to go,

```groovy
dependencies {
    compile 'com.pv:datetimeseer:1.0.0'
}
```

The library exposes `SeerFilter` which extends android's `Filter` class and so can be hooked to anything which implements `Filterable`. 

Use the `ConfigBuilder` to provide Date/Time formats.

The sample app here demonstrate the usage of the `Filter` with the `AutoCompleteTextView`. 


### TODOS

- Add more documentation
- Some dirty code clean up
- Make the library more extensible
